
package com.quexten.ravtech.net.kryonet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entries;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import com.quexten.ravtech.Hook;
import com.quexten.ravtech.HookApi;
import com.quexten.ravtech.RavTech;
import com.quexten.ravtech.net.Player;
import com.quexten.ravtech.net.Packet.NetViewPacket;
import com.quexten.ravtech.net.Packet.Packet_DKChangeable;
import com.quexten.ravtech.net.Packet.Packet_Instantiate;
import com.quexten.ravtech.net.Packet.Packet_LobbyData;
import com.quexten.ravtech.net.Packet.Packet_LoginAnswer;
import com.quexten.ravtech.net.Packet.Packet_LoginRequest;
import com.quexten.ravtech.util.Debug;

public class ServerListener extends Listener {

	static final String LOG_TAG = "RavNetwork-Server";

	KryonetTransportLayer layer;
	IntArray trustedConnections = new IntArray();

	public ServerListener(KryonetTransportLayer layer) {
		this.layer = layer;
	}

	@Override
	public void connected(Connection connection) {
		Debug.logDebug(LOG_TAG, connection.getRemoteAddressTCP() + " is trying to conntect!");
		connection.updateReturnTripTime();

		// Return lobby data
		Packet_LobbyData lobbyDataPacket = new Packet_LobbyData();
		lobbyDataPacket.values = new ObjectMap<String, Object>();
		Entries<String, Object> entries = layer.net.lobby.values.iterator();
		while (entries.hasNext) {
			Entry<String, Object> entry = entries.next();
			lobbyDataPacket.values.put(entry.key, entry.value);
		}
	}

	@Override
	public void disconnected(Connection connection) {
		trustedConnections.removeValue(connection.getID());
		Log.info("[Server]" + connection.getRemoteAddressTCP() + " disconnected.");

		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				HookApi.runHooks(layer.net.lobby.onLeftHooks);
			}
		});
	}

	@Override
	public void received(final Connection connection, final Object packet) {
		if (packet instanceof NetViewPacket || packet instanceof Packet_Instantiate
				|| packet instanceof Packet_DKChangeable)
			if (trustedConnections.contains(connection.getID()))
				sendToAllExcept(connection.getID(), packet, false);

		if (packet instanceof Packet_LoginRequest)
			if (layer.net.lobby.checkPassword(((Packet_LoginRequest) packet).password)) {

				Debug.log(LOG_TAG, "Authentification of " + connection.getRemoteAddressTCP() + " successful as: "
						+ ((Packet_LoginRequest) packet).username);
				trustedConnections.add(connection.getID());

				Packet_LoginAnswer answer = new Packet_LoginAnswer();
				
				layer.net.lobby.playerJoined(connection, ((Packet_LoginRequest) packet).username)
				.send(answer, true);
				
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						HookApi.runHooks(layer.net.lobby.onJoinedHooks,
								layer.net.lobby.getPlayerForConnection(connection));
					}
				});		
			} else {
				Debug.log(LOG_TAG, "Authentification failed for " + connection.getRemoteAddressTCP());
				connection.close();
			}
		if (trustedConnections.contains(connection.getID()))
			layer.net.processPacket(packet, layer.net.transportLayers.get(0), layer.net.lobby.getPlayerForConnection(connection));
	}

	private void sendToAllExcept(int connectionIdentifier, Object packet, boolean reliable) {
		if (layer.isHost)
			for (int i = 0; i < trustedConnections.size; i++) {
				int connectionId = trustedConnections.get(i);
				if (connectionId != (int) connectionIdentifier)
					if (reliable)
						layer.server.sendToTCP(connectionId, packet);
					else
						layer.server.sendToUDP(connectionId, packet);
			}
	}

}
