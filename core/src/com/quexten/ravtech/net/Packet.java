
package com.quexten.ravtech.net;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.quexten.ravtech.history.Changeable;

public class Packet {

	public static class Packet_LoginRequest {
		public String username;
		public String password;
	}

	public static class Packet_LoginAnswer {
		boolean accepted = false;
		int ownerid;
	}
	
	public static class Packet_StreamHeader {
		
		public int streamId = (int) (Math.random() * Integer.MAX_VALUE);
		public String type;

		public int streamLength;

		// Can be used for ravviewid's for the gamestate, file directory for
		// assets or otherwise		
		public Object additionalInfo;
	}

	public static class Packet_StreamChunk {
		public int streamId;
		public byte[] chunkBytes;
	}

	public static class Packet_Instantiate {
		public String prefabPath;
		Vector2 position;
		float rotation;
		IntArray ravViewIDs;
	}

	public static class NetViewPacket {
		public int id;
	}

	public static class Packet_ObjectPosition extends NetViewPacket {
		public float x;
		public float y;
	}

	public static class Packet_ObjectRotation extends NetViewPacket {
		public float rotation;
	}

	public static class Packet_BodyLinearVelocity extends NetViewPacket {
		public float xvel;
		public float yvel;
	}

	public static class Packet_BodyAngularVelocity extends NetViewPacket {
		public float omega;
	}

	public static class Packet_RavNetStream extends NetViewPacket {
		public Array<Object> objects;
	}

	public static class Packet_RPC extends NetViewPacket {
		public Array<Object> arguments;
		public String function;
	}

	public static class Packet_DKChangeable {
		public Changeable changeable;
	}

	public static class Packet_PlayingState {
		public boolean playing;
	}

	public static class LobbyPacket {
	}

	public static class Packet_LobbyData {
		public ObjectMap<String, Object> values;
	}

	public static class Packet_SetLobbyData extends LobbyPacket {
		public String key;
		public Object value;
	}

	public static class Packet_DeleteLobbyData extends LobbyPacket {
		public String key;
	}
	
	public static class Packet_GameStateRequest {
	}
	
}
