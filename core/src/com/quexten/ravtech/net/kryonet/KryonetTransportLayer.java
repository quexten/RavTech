
package com.quexten.ravtech.net.kryonet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntMap;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.ByteBufferInput;
import com.esotericsoftware.kryo.io.ByteBufferOutput;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.util.InputStreamSender;
import com.quexten.ravtech.history.CreateChangeable;
import com.quexten.ravtech.history.ModifyChangeable;
import com.quexten.ravtech.history.RemoveChangeable;
import com.quexten.ravtech.net.Packet;
import com.quexten.ravtech.net.RavNetwork;
import com.quexten.ravtech.net.TransportLayer;
import com.quexten.ravtech.util.Debug;

public class KryonetTransportLayer extends TransportLayer {

	int bufferSize = 8192;
	int timeout = 600;
	int udpPort = 54554;
	int tcpPort = 54555;

	Array<KryonetDiscoveryRequest> requests = new Array<KryonetDiscoveryRequest>();

	Server server;
	public Client client;

	ServerListener serverListener;
	ClientListener clientListener;

	// Client used only for getting lobby details of discovered lobbies
	Client discoveryClient = new Client(bufferSize, bufferSize);
	ClientListener discoveryClientListener;

	Kryo streamKryo = new Kryo();
	ByteBufferOutput output = new ByteBufferOutput(bufferSize);
	ByteBufferInput input = new ByteBufferInput(bufferSize);

	IntMap<LargePacketBuffer> largePacketBuffers = new IntMap<LargePacketBuffer>();

	public KryonetTransportLayer (RavNetwork net, int udpPort, int tcpPort) {
		this(net);
		this.udpPort = udpPort;
		this.tcpPort = tcpPort;
	}

	public KryonetTransportLayer (RavNetwork net) {
		super(net);
		server = new Server(bufferSize, bufferSize);
		client = new Client(bufferSize, bufferSize);

		serverListener = new ServerListener(this);
		clientListener = new ClientListener(this);

		server.addListener(serverListener);
		client.addListener(clientListener);

		register(server.getKryo());
		register(client.getKryo());
		register(streamKryo);
	}

	@Override
	public void udpate () {
	}

	@Override
	public void dispose () {
		try {
			server.close();
			client.close();
			server.dispose();
			client.dispose();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void discoverHosts () {
		for (InetAddress address : client.discoverHosts(udpPort, timeout)) {
			String trimmedAdress = String.valueOf(address).substring(1);
			KryonetDiscoveryRequest request = new KryonetDiscoveryRequest();
			request.ipAdress = trimmedAdress;
			request.tcpPort = 54555;
			request.udpPort = 54554;
			requests.add(request);

			KryoHost kryoHost = new KryoHost();
			kryoHost.hostName = "Quexten";
			kryoHost.ipAdress = request.ipAdress;
			this.hosts.add(request.ipAdress);
		}
	}

	@Override
	public void send (Packet packet, boolean reliable) {
		if (isHost)
			for (int i = 0; i < serverListener.trustedConnections.size; i++) {
				int connectionId = serverListener.trustedConnections.get(i);
				if (reliable)
					server.sendToTCP(connectionId, packet);
				else
					server.sendToUDP(connectionId, packet);
			}
		else if (reliable)
			client.sendTCP(packet);
		else
			client.sendUDP(packet);

	}

	@Override
	public void sendTo (Object connectionIdentifier, Packet packet, boolean reliable) {
		int connectionId = connectionIdentifier instanceof Connection ? ((Connection)connectionIdentifier).getID()
			: (int)connectionIdentifier;
		if (isHost) {
			if (reliable)
				server.sendToTCP(connectionId, packet);
			else
				server.sendToUDP(connectionId, packet);
		} else if (reliable)
			client.sendTCP(packet);
		else
			client.sendUDP(packet);
	}

	@Override
	public void sendLargeTo (Object connectionInformation, Packet packet, String type, Object additionalInformation) {
		Debug.log("ConnectionInformation", connectionInformation);
		Debug.log("Type", type);
		Debug.log("Additional Info", additionalInformation);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		output.setOutputStream(outputStream);
		streamKryo.writeClassAndObject(output, packet);
		output.flush();	
		
		byte[] buffer = outputStream.toByteArray();
		
		Debug.log("BufferLength", buffer.length);
		
		sendStreamTo(connectionInformation, new ByteArrayInputStream(buffer), buffer.length, type, additionalInformation);
	}

	@Override
	public void sendStreamTo (Object connectionInformation, InputStream stream, int size, String type,
		Object additionalInformation) {

		final int streamId = (int)(Math.random() * Integer.MAX_VALUE);

		Packet.StreamHeader streamHeader = new Packet.StreamHeader();
		streamHeader.additionalInfo = additionalInformation;
		streamHeader.streamId = streamId;
		streamHeader.type = type;
		streamHeader.streamLength = size;
		sendTo(connectionInformation, streamHeader, true);

		// Send data in 512 byte chunks.
		((Connection)connectionInformation).addListener(new InputStreamSender(stream, 1024) {
			@Override
			protected Object next (byte[] bytes) {
				Packet.StreamChunk chunkPacket = new Packet.StreamChunk();
				chunkPacket.streamId = streamId;
				chunkPacket.chunkBytes = bytes;
				return chunkPacket;
			}
		});
	}

	@Override
	public void recieve (Packet packet) {
		if (packet instanceof Packet.StreamHeader) {
			Packet.StreamHeader streamHeader = ((Packet.StreamHeader)packet);
			if (streamHeader.type.equals(RavNetwork.LARGE_Packet_HEADER_TYPE)) {
				this.largePacketBuffers.put(streamHeader.streamId, new LargePacketBuffer(streamHeader));
			}
		}

		if (packet instanceof Packet.StreamChunk) {
			Packet.StreamChunk streamChunk = ((Packet.StreamChunk)packet);
			Debug.log("StreamChunk", streamChunk.streamId);
			if (this.largePacketBuffers.containsKey(streamChunk.streamId)) {
				this.largePacketBuffers.get(streamChunk.streamId).addChunk(streamChunk.chunkBytes);
				if(this.largePacketBuffers.get(streamChunk.streamId).isComplete()) {
					recieve(this.largePacketBuffers.get(streamChunk.streamId).getPacket());
				}
			}			
		}
		this.net.processPacket(packet, net.lobby.players.get(packet.senderId));
	}

	@Override
	public void createLobby (Object connectionInformation) {
		this.isHost = true;

		System.out.println("CreateLobby Tcp: " + tcpPort + " Udp: " + udpPort);
		server.start();
		System.out.println("Server started");
		try {
			server.bind(tcpPort, udpPort);
		} catch (IOException ex) {
			System.out.println("[RAVNET] Could not bind server on TCP: " + tcpPort + " UDP: " + udpPort);
			ex.printStackTrace();
		}
	}

	@Override
	public boolean joinLobby (Object connectionInformation) {
		this.isHost = false;
		client.start();
		try {
			client.connect(timeout, String.valueOf(connectionInformation), tcpPort, udpPort);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return client.isConnected();
	}

	@Override
	public void leaveLobby () {
		server.stop();
		client.stop();
	}

	@Override
	public void onSetLobbyData (String key, Object value) {
		if (isHost) {
			Packet.SetLobbyData packet = new Packet.SetLobbyData();
			packet.key = key;
			packet.value = value;
			server.sendToAllTCP(packet);
		}
	}

	@Override
	public void onDeleteLobbyData (String key) {
		if (isHost) {
			Packet.DeleteLobbyData packet = new Packet.DeleteLobbyData();
			packet.key = key;
			server.sendToAllTCP(packet);
		}
	}

	@Override
	public void onLobbyDataUpdate () {
	}

	public void register (Kryo kryo) {
		// Game Packets
		kryo.register(Packet.LoginRequest.class);
		kryo.register(Packet.LoginAnswer.class);
		kryo.register(Packet.ObjectPosition.class);
		kryo.register(Packet.ObjectRotation.class);
		kryo.register(Packet.BodyLinearVelocity.class);
		kryo.register(Packet.BodyAngularVelocity.class);
		kryo.register(Packet.RavNetStream.class);
		kryo.register(Packet.Instantiate.class);
		kryo.register(Packet.RPC.class);
		kryo.register(Packet.StreamHeader.class);
		kryo.register(Packet.StreamChunk.class);
		kryo.register(Packet.LobbyData.class);
		kryo.register(Packet.SetLobbyData.class);
		kryo.register(Packet.DeleteLobbyData.class);
		kryo.register(Packet.GameStateRequest.class);

		// Net Debug/Edit packets
		kryo.register(Packet.DKChangeable.class);
		kryo.register(ModifyChangeable.class);
		kryo.register(CreateChangeable.class);
		kryo.register(RemoveChangeable.class);
		kryo.register(Packet.PlayingState.class);
		kryo.register(Color.class);
		kryo.register(TextureFilter.class);

		// Basic types
		kryo.register(Array.class);
		kryo.register(IntArray.class);
		kryo.register(Object[].class);
		kryo.register(int[].class);
		kryo.register(Vector2.class);
		kryo.register(byte[].class);
		kryo.register(com.esotericsoftware.kryo.util.ObjectMap.class);
		kryo.setRegistrationRequired(false);
	}

	public class KryoHost extends Host {

		public String ipAdress;

		@Override
		public String toString () {
			return ipAdress + " - " + this.hostName;
		}

	}

	class LargePacketBuffer {

		Packet.StreamHeader streamHeader;
		byte[] buffer;
		int recievedByteCount;

		public LargePacketBuffer (Packet.StreamHeader streamHeader) {
			this.streamHeader = streamHeader;
			this.buffer = new byte[streamHeader.streamLength];
			Debug.log("CreateLargePacket", streamHeader.type);
		}

		public void addChunk (byte[] chunk) {
			for (int i = recievedByteCount; i < recievedByteCount + chunk.length; i++) {
				buffer[i] = chunk[i - recievedByteCount];
			}
			recievedByteCount += chunk.length;
			Debug.log("Recived", streamHeader.type + " | " + 100 * ((float) recievedByteCount / (float)buffer.length));
		}

		public Packet getPacket () {
			input.setInputStream(new ByteArrayInputStream(buffer));
			Packet packet = ((Packet) streamKryo.readClassAndObject(input));
			Debug.log("Packet", packet);
			return packet;
		}

		public boolean isComplete () {
			return recievedByteCount == buffer.length;
		}

	}

}
