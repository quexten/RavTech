package com.quexten.ravtech.net;

import java.io.InputStream;

public class Player {
	
	Lobby lobby;
	
	Object connectionInformation;
	String username;	
	
	public Player(Lobby lobby, Object connectionInformation, String username) {
		this.lobby = lobby;
		this.connectionInformation = connectionInformation;
		this.username = username;
	}
	
	public void send(Object packet, boolean reliable) {
		lobby.network.sendTo(connectionInformation, packet, reliable);
	}
	
	public void sendStream(String type, Object additionalInformation, InputStream stream, int size) {
		lobby.network.sendStreamTo(connectionInformation, type, additionalInformation, stream, size);
	}

	public void sendLargePacket(String type, Object additionalInformation, Object packet) {
		lobby.network.sendLargePacketTo(connectionInformation, type, additionalInformation, packet);
	}
	
}
