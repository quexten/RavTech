
package com.quexten.ravtech.net;

import java.io.InputStream;

import com.badlogic.gdx.utils.Array;
import com.quexten.ravtech.util.Debug;

public class RavNetwork {

	public Array<TransportLayer> transportLayers = new Array<TransportLayer>();
	public Lobby lobby;

	private Array<PacketProcessor> processors = new Array<PacketProcessor>();	
	
	public RavNetwork() {
	}

	public void update() {
		for (int i = 0; i < transportLayers.size; i++)
			transportLayers.get(i).udpate();
	}

	public void dispose() {
		for (TransportLayer layer : transportLayers)
			layer.dispose();
	}

	// Lobby Options
	public void createLobby(int requestedTCPPort, int requestedUDPPort, int maximumPlayers) {
		if (isInLobby())
			leaveLobby();
		lobby = new Lobby(this, maximumPlayers, true);

		for (TransportLayer layer : transportLayers) {
			Debug.log("createLobby", layer);
			layer.createLobby(null);
		}
	}

	public boolean joinLobby(String connectionId) {
		if (isInLobby())
			leaveLobby();
		Debug.log("Trying to join Lobby", connectionId);
		boolean lobbyJoined = false;
		for (TransportLayer layer : transportLayers) {
			lobbyJoined = layer.joinLobby(connectionId);
			if (lobbyJoined)
				break;
		}
		lobby = new Lobby(this, 4, false);
		return lobbyJoined;
	}

	public void leaveLobby() {
		if (isInLobby()) {
			for (TransportLayer layer : transportLayers)
				layer.leaveLobby();
			lobby = null;
		}
	}

	public boolean isInLobby() {
		return lobby != null;
	}

	public void send(Object packet, boolean reliable) {
		for (int i = 0; i < transportLayers.size; i++)
			transportLayers.get(i).send(packet, reliable);
	}

	public void sendTo(Object connectionIdentifier, Object packet, boolean reliable) {
		for (int i = 0; i < transportLayers.size; i++)
			transportLayers.get(i).sendTo(connectionIdentifier, packet, reliable);
	}

	public void sendStreamTo(Object connectionInformation, String type, Object additionalInformation,
			InputStream stream, int size) {
		for (int i = 0; i < transportLayers.size; i++)
			transportLayers.get(i).sendStreamTo(connectionInformation, type, additionalInformation, stream, size);
	}
	
	public void sendLargePacketTo(Object connectionInformation, String type, Object additionalInformation, Object packet) {
		for(int i = 0; i < transportLayers.size; i++) {
			transportLayers.get(i).sendLargeTo(connectionInformation, type, additionalInformation, packet);
		}
	}

	public void processPacket(final Object packet, TransportLayer layer, Player player) {
		if (lobby == null)
			lobby = new Lobby(this, 4, false);

		for(int i = 0; i < processors.size; i++) {
			if(processors.get(i).process(player, packet))
				return;
		}
	}

	public void discoverHosts() {
		for (int i = 0; i < transportLayers.size; i++) {
			final TransportLayer layer = transportLayers.get(i);
			layer.discoverHosts();
		}
	}
	
	public void addProcessor(PacketProcessor processor) {
		this.processors.add(processor);
	}

}
