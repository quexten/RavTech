
package com.quexten.ravtech.net;

import java.io.InputStream;

import com.badlogic.gdx.utils.Array;

public abstract class TransportLayer {

	public boolean isHost;
	public Array<String> hosts = new Array<String>();

	public Array<Runnable> onConnectHooks = new Array<Runnable>();

	public RavNetwork net;

	public TransportLayer (RavNetwork net) {
		this.net = net;
	}

	// General Methods
	public abstract void udpate ();

	public abstract void dispose ();

	public abstract void discoverHosts ();

	// Sending / Recieving Methods
	public abstract void send (Packet packet, boolean reliable);

	public abstract void sendTo (Object connectionIdentifier, Packet packet, boolean reliable);

	public abstract void sendLargeTo (Object connectionInformation, Packet packet, String type, Object additionalInformation);

	public abstract void sendStreamTo (Object connectionInformation, InputStream stream, int size, String type,
		Object additionalInformation);

	public abstract void recieve (Packet packet);

	// Lobby Methods
	public abstract void createLobby (Object lobbyInformation);

	public abstract boolean joinLobby (Object connectionInformation);

	public abstract void leaveLobby ();

	public abstract void onSetLobbyData (String key, Object value);

	public abstract void onDeleteLobbyData (String key);

	public abstract void onLobbyDataUpdate ();

	public abstract class Host {

		public String hostName;

		@Override
		public abstract String toString ();

	}

}
