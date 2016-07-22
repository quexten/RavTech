
package com.quexten.ravtech.net.kryonet;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.quexten.ravtech.net.Packet.LobbyPacket;
import com.quexten.ravtech.net.Packet.Packet_DeleteLobbyData;
import com.quexten.ravtech.net.Packet.Packet_LoginAnswer;
import com.quexten.ravtech.net.Packet.Packet_LoginRequest;
import com.quexten.ravtech.net.Packet.Packet_SetLobbyData;
import com.quexten.ravtech.net.Packet.Packet_StreamChunk;
import com.quexten.ravtech.util.Debug;

public class ClientListener extends Listener {

	static final String LOG_TAG = "RavNetwork-Client";

	KryonetTransportLayer layer;

	public ClientListener(KryonetTransportLayer layer) {
		this.layer = layer;
	}

	@Override
	public void connected(Connection connection) {
		Debug.logDebug(LOG_TAG, "Connection established, sending login request");
		Packet_LoginRequest request = new Packet_LoginRequest();
		request.username = System.getProperty("user.name");
		connection.sendTCP(request);
	}

	@Override
	public void disconnected(Connection connection) {
		Debug.logDebug(LOG_TAG, "Connection lost.");
		layer.net.leaveLobby();
	}

	public void received(Connection connection, final Object o) {
		if(! (o instanceof Packet_StreamChunk))
		Debug.logDebug(LOG_TAG, "Recieved:" + o);
		if (o instanceof LobbyPacket) {
			if (o instanceof Packet_SetLobbyData)
				layer.net.lobby.values.put(((Packet_SetLobbyData) o).key, ((Packet_SetLobbyData) o).value);
			if (o instanceof Packet_DeleteLobbyData)
				layer.net.lobby.values.remove(((Packet_DeleteLobbyData) o).key);
			return;
		}
		
		if(o instanceof Packet_LoginAnswer) {
			layer.net.lobby.playerJoined(connection, "Host");
		}
		
		
		if(layer.net.lobby != null)
		layer.net.processPacket(o, layer.net.transportLayers.get(0), layer.net.lobby.getPlayerForConnection(connection));
	}

}
