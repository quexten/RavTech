
package com.quexten.ravtech.net.kryonet;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.quexten.ravtech.HookApi;
import com.quexten.ravtech.net.Packet;
import com.quexten.ravtech.util.Debug;

public class ClientListener extends Listener {

	static final String LOG_TAG = "RavNetwork-Client";

	KryonetTransportLayer layer;

	public ClientListener (KryonetTransportLayer layer) {
		this.layer = layer;
	}

	@Override
	public void connected (Connection connection) {
		Debug.logDebug(LOG_TAG, "Connection established, sending login request");

		Packet.LoginRequest request = new Packet.LoginRequest();
		request.username = System.getProperty("user.name");
		connection.sendTCP(request);
	}

	@Override
	public void disconnected (Connection connection) {
		Debug.logDebug(LOG_TAG, "Connection lost.");

		layer.net.leaveLobby();
	}

	@Override
	public void received (Connection connection, final Object object) {
		if(!(object instanceof Packet))
			return;
		Packet packet = ((Packet)object);
		
		if (packet instanceof Packet.LobbyPacket) {
			if (packet instanceof Packet.SetLobbyData)
				layer.net.lobby.values.put(((Packet.SetLobbyData)packet).key, ((Packet.SetLobbyData)packet).value);
			if (packet instanceof Packet.DeleteLobbyData)
				layer.net.lobby.values.remove(((Packet.DeleteLobbyData)packet).key);
			return;
		}

		if (packet instanceof Packet.LoginAnswer) {
			layer.net.lobby.playerJoined(connection, "quexten");
			layer.net.lobby.ownId = ((Packet.LoginAnswer)packet).id;
			HookApi.postHooks(layer.net.lobby.onJoinedHooks, layer.net.lobby.getPlayerForConnection(connection));
		}

		if (layer.net.lobby != null)
			layer.recieve(packet);
	}

}
