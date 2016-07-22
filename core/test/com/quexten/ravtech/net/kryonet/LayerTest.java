package com.quexten.ravtech.net.kryonet;

import com.quexten.ravtech.Test;
import com.quexten.ravtech.net.RavNetwork;

public class LayerTest implements Test {

	static final int TCP_PORT = 54555;
	static final int UDP_PORT = 54554;
	
	public boolean test() {
		
		RavNetwork serverNetwork = new RavNetwork();
		serverNetwork.transportLayers.add(new KryonetTransportLayer(serverNetwork));
		serverNetwork.createLobby(TCP_PORT, UDP_PORT, 2);
		
		RavNetwork clientNetwork = new RavNetwork();
		clientNetwork.transportLayers.add(new KryonetTransportLayer(clientNetwork));
		clientNetwork.joinLobby("127.0.0.1");
		
		return clientNetwork.isInLobby();
	}

}
