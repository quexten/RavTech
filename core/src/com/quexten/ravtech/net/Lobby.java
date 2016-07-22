
package com.quexten.ravtech.net;

import java.util.Iterator;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntMap.Entry;
import com.badlogic.gdx.utils.ObjectMap;
import com.quexten.ravtech.Hook;
import com.quexten.ravtech.RavTech;
import com.quexten.ravtech.util.Debug;
import com.quexten.ravtech.util.HashUtil;

public class Lobby {

	RavNetwork network;

	int currentId;
	public int maximumPlayers;
	public boolean isOwner;
	public ObjectMap<String, Object> values;
	public IntMap<Player> players;
	private String passwordHash;

	public Array<Hook> onJoinedHooks = new Array<Hook>();
	public Array<Hook> onLeftHooks = new Array<Hook>();

	public Lobby(RavNetwork network, int maximumPlayers, boolean isOwner) {
		this.network = network;
		this.maximumPlayers = maximumPlayers;
		this.isOwner = isOwner;
		this.values = new ObjectMap<String, Object>();
		this.players = new IntMap<Player>();
		if (isOwner)
			values.put("Test", "succes");
	}

	public void setLobbyData(String key, Object value) {
		if (isOwner) {
			values.put(key, value);
			for (int i = 0; i < RavTech.net.transportLayers.size; i++)
				RavTech.net.transportLayers.get(i).onSetLobbyData(key, value);
		} else
			Debug.logError("[RavNetwork]", "Can't set lobbydata; Invoker isn't owner.");
	}

	public void deleteLobbyData(String key) {
		if (isOwner) {
			values.remove(key);
			for (int i = 0; i < RavTech.net.transportLayers.size; i++)
				RavTech.net.transportLayers.get(i).onDeleteLobbyData(key);
		} else
			Debug.logError("[RavNetwork]", "Can't delete lobbydata; Invoker isn't owner.");
	}

	public Object getLobbyData(String key) {
		return values.get(key);
	}

	public void setPassword(String password) {
		passwordHash = password == null ? null : new String(HashUtil.generateHash(password.getBytes()));
	}

	public boolean checkPassword(String password) {
		if (passwordHash == null)
			return true;
		return passwordHash.equals(new String(HashUtil.generateHash(password.getBytes())));
	}

	public boolean hasPassword() {
		return passwordHash != null;
	}

	public Player playerJoined(Object connectionInformation, String username) {
		Player player = new Player(this, connectionInformation, username);
		players.put(currentId++, player);
		return player;
	}

	public void playerLeft(Object connectionInforrmation) {

	}

	public Player getPlayerForConnection(Object connectionInformation) {
		Iterator<Entry<Player>> entries = players.iterator();
		Entry<Player> entry = null;
		if (entries.hasNext())
			while ((entry = entries.next()) != null) {
				if (entry.value.connectionInformation == connectionInformation) {
					return entry.value;
				}
			}

		return null;
	}

}
