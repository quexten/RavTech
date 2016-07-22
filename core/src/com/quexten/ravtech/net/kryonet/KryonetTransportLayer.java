
package com.quexten.ravtech.net.kryonet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.nio.ByteBuffer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.ByteBufferOutput;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.util.InputStreamSender;
import com.quexten.ravtech.history.CreateChangeable;
import com.quexten.ravtech.history.ModifyChangeable;
import com.quexten.ravtech.history.RemoveChangeable;
import com.quexten.ravtech.net.Packet.Packet_BodyAngularVelocity;
import com.quexten.ravtech.net.Packet.Packet_BodyLinearVelocity;
import com.quexten.ravtech.net.Packet.Packet_DKChangeable;
import com.quexten.ravtech.net.Packet.Packet_DeleteLobbyData;
import com.quexten.ravtech.net.Packet.Packet_GameStateRequest;
import com.quexten.ravtech.net.Packet.Packet_Instantiate;
import com.quexten.ravtech.net.Packet.Packet_LobbyData;
import com.quexten.ravtech.net.Packet.Packet_LoginAnswer;
import com.quexten.ravtech.net.Packet.Packet_LoginRequest;
import com.quexten.ravtech.net.Packet.Packet_ObjectPosition;
import com.quexten.ravtech.net.Packet.Packet_ObjectRotation;
import com.quexten.ravtech.net.Packet.Packet_PlayingState;
import com.quexten.ravtech.net.Packet.Packet_RPC;
import com.quexten.ravtech.net.Packet.Packet_RavNetStream;
import com.quexten.ravtech.net.Packet.Packet_SetLobbyData;
import com.quexten.ravtech.net.Packet.Packet_StreamChunk;
import com.quexten.ravtech.net.Packet.Packet_StreamHeader;
import com.quexten.ravtech.util.Debug;
import com.quexten.ravtech.net.RavNetwork;
import com.quexten.ravtech.net.TransportLayer;

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
	ByteBuffer writeBuffer = ByteBuffer.allocateDirect(bufferSize);

	
	public KryonetTransportLayer(RavNetwork net, int udpPort, int tcpPort) {
		this(net);
		this.udpPort = udpPort;
		this.tcpPort = tcpPort;
	}

	public KryonetTransportLayer(RavNetwork net) {
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
	public void udpate() {
	}

	@Override
	public void dispose() {
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
	public void discoverHosts() {
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
	public void send(Object packet, boolean reliable) {
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
	public void sendTo(Object connectionIdentifier, Object packet, boolean reliable) {
		int connectionId = connectionIdentifier instanceof Connection ? ((Connection) connectionIdentifier).getID()
				: (int) connectionIdentifier;	
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
	public void sendLargeTo(Object connectionInformation, String type, Object additionalInformation, Object object) {
		Debug.log("ConnectionInformation", connectionInformation);
		Debug.log("Type", type);
		Debug.log("Packet", object);
		Debug.log("Additional Info", additionalInformation);
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		writeBuffer.clear();
		
		output.setOutputStream(outputStream);
		streamKryo.writeClassAndObject(output, object);
		output.flush();		
				
		byte[] buffer = null;
		try {
			buffer = ((String) object).getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Debug.log("BufferLength", buffer.length);
		sendStreamTo(connectionInformation, type, additionalInformation, new ByteArrayInputStream(buffer), buffer.length);
	}
	
	@Override
	public void sendStreamTo (Object connectionInformation, String type, Object additionalInformation, InputStream stream, int size) {
		
		final int streamId = (int) (Math.random() * Integer.MAX_VALUE);	//TODO Replace random streamId with negotiated streaId
		
		Packet_StreamHeader streamHeader = new Packet_StreamHeader();
		streamHeader.additionalInfo = additionalInformation;
		streamHeader.streamId = streamId;
		streamHeader.type = type;
		streamHeader.streamLength = size;
		sendTo(connectionInformation, streamHeader, true);
		
		// Send data in 512 byte chunks.
		((Connection) connectionInformation).addListener(new InputStreamSender(stream, 512) {
			protected Object next(byte[] bytes) {
				Packet_StreamChunk chunkPacket = new Packet_StreamChunk();
				chunkPacket.streamId = streamId;
				chunkPacket.chunkBytes = bytes;
				return chunkPacket;
			}
		});
	}
	
	@Override
	public void createLobby(Object connectionInformation) {
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
	public boolean joinLobby(Object connectionInformation) {
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
	public void leaveLobby() {
		server.stop();
		client.stop();
	}

	@Override
	public void onSetLobbyData(String key, Object value) {
		if (isHost) {
			Packet_SetLobbyData packet = new Packet_SetLobbyData();
			packet.key = key;
			packet.value = value;
			server.sendToAllTCP(packet);
		}
	}

	@Override
	public void onDeleteLobbyData(String key) {
		if (isHost) {
			Packet_DeleteLobbyData packet = new Packet_DeleteLobbyData();
			packet.key = key;
			server.sendToAllTCP(packet);
		}
	}

	@Override
	public void onLobbyDataUpdate() {
	}

	public void register(Kryo kryo) {
		// Game Packets
		kryo.register(Packet_LoginRequest.class);
		kryo.register(Packet_LoginAnswer.class);
		kryo.register(Packet_ObjectPosition.class);
		kryo.register(Packet_ObjectRotation.class);
		kryo.register(Packet_BodyLinearVelocity.class);
		kryo.register(Packet_BodyAngularVelocity.class);
		kryo.register(Packet_RavNetStream.class);
		kryo.register(Packet_Instantiate.class);
		kryo.register(Packet_RPC.class);
		kryo.register(Packet_StreamHeader.class);
		kryo.register(Packet_StreamChunk.class);
		kryo.register(Packet_LobbyData.class);
		kryo.register(Packet_SetLobbyData.class);
		kryo.register(Packet_DeleteLobbyData.class);
		kryo.register(Packet_GameStateRequest.class);

		// Net Debug/Edit packets
		kryo.register(Packet_DKChangeable.class);
		kryo.register(ModifyChangeable.class);
		kryo.register(CreateChangeable.class);
		kryo.register(RemoveChangeable.class);
		kryo.register(Packet_PlayingState.class);
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
		public String toString() {
			return ipAdress + " - " + this.hostName;
		}

	}
	
}
