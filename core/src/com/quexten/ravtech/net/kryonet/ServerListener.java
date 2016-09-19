
package com.quexten.ravtech.net.kryonet;

import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entries;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.quexten.ravtech.HookApi;
import com.quexten.ravtech.net.Packet;
import com.quexten.ravtech.util.Debug;

public class ServerListener extends Listener {

	static final String LOG_TAG = "RavNetwork-Server";

	KryonetTransportLayer layer;

	IntArray trustedConnections = new IntArray();

	public ServerListener (KryonetTransportLayer layer) {
		this.layer = layer;
	}

	@Override
	public void connected (Connection connection) {
		Debug.logDebug(LOG_TAG, connection.getRemoteAddressTCP() + " is trying to conntect!");
		connection.updateReturnTripTime();

		// Return lobby data
		Packet.LobbyData lobbyDataPacket = new Packet.LobbyData();
		lobbyDataPacket.values = new ObjectMap<String, Object>();
		Entries<String, Object> entries = layer.net.lobby.values.iterator();
		while (entries.hasNext) {
			Entry<String, Object> entry = entries.next();
			lobbyDataPacket.values.put(entry.key, entry.value);
		}
	}

	@Override
	public void disconnected (Connection connection) {
		Debug.logDebug(LOG_TAG, connection.getRemoteAddressTCP() + " disconnected.");

		trustedConnections.removeValue(connection.getID());
		HookApi.postHooks(layer.net.lobby.onLeftHooks);
	}

	@Override
	public void received (final Connection connection, Object object) {
		if(!(object instanceof Packet))
			return;
		Packet packet = ((Packet)object);

		if (packet instanceof Packet.LoginRequest)
			if (layer.net.lobby.checkPassword(((Packet.LoginRequest)packet).password)) {
				Debug.logDebug(LOG_TAG, "Authentification of " + connection.getRemoteAddressTCP() + " successful as: "
					+ ((Packet.LoginRequest)packet).username);

				trustedConnections.add(connection.getID());

				Packet.LoginAnswer answer = new Packet.LoginAnswer();
				answer.id = layer.net.lobby.players.size;
				layer.net.lobby.playerJoined(connection, ((Packet.LoginRequest)packet).username).send(answer, true);

				HookApi.postHooks(layer.net.lobby.onJoinedHooks, layer.net.lobby.getPlayerForConnection(connection));
			} else {
				Debug.log(LOG_TAG, "Authentification failed for " + connection.getRemoteAddressTCP());
				connection.close();
			}
		if (trustedConnections.contains(connection.getID()))
			layer.recieve(packet);
	}

}
