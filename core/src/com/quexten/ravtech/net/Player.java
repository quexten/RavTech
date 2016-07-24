
package com.quexten.ravtech.net;

import java.io.InputStream;

public class Player {

	Lobby lobby;

	Object connectionInformation;
	String username;

	public Player (Lobby lobby, Object connectionInformation, String username) {
		this.lobby = lobby;
		this.connectionInformation = connectionInformation;
		this.username = username;
	}

	public void send (Packet packet, boolean reliable) {
		lobby.network.sendTo(connectionInformation, packet, reliable);
	}

	public void sendStream (InputStream stream, int size, String type, Object additionalInformation) {
		lobby.network.sendStreamTo(connectionInformation, stream, size, type, additionalInformation);
	}

	public void sendLargePacket (Packet packet, String type, Object additionalInformation) {
		lobby.network.sendLargePacketTo(connectionInformation, packet, type, additionalInformation);
	}

}
