
package com.quexten.ravtech.net;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.quexten.ravtech.history.Changeable;

public class Packet {

	public int senderId;

	public static class LoginRequest extends Packet {
		public String username;
		public String password;
	}

	public static class LoginAnswer extends Packet {
		public boolean accepted;
		public String message;
		public IntMap<Player> players;
		public int id;
	}

	public static class AssetRequest extends Packet {
		public ObjectMap<String, String> fileHashes = new ObjectMap<String, String>();
	}
	
	public static class StreamHeader extends Packet {
		public int streamId = (int)(Math.random() * Integer.MAX_VALUE);
		public String type;

		public int streamLength;

		public Object additionalInfo;
	}

	public static class StreamChunk extends Packet {
		public int streamId;
		public byte[] chunkBytes;
	}

	public static class Instantiate extends Packet {
		public String prefabPath;
		Vector2 position;
		float rotation;
		IntArray ravViewIDs;
	}

	public static class NetViewPacket extends Packet {
		public int id;
	}

	public static class ObjectPosition extends NetViewPacket {
		public float x;
		public float y;
	}

	public static class ObjectRotation extends NetViewPacket {
		public float rotation;
	}

	public static class BodyLinearVelocity extends NetViewPacket {
		public float xvel;
		public float yvel;
	}

	public static class BodyAngularVelocity extends NetViewPacket {
		public float omega;
	}

	public static class RavNetStream extends NetViewPacket {
		public Array<Object> objects;
	}

	public static class RPC extends NetViewPacket {
		public Array<Object> arguments;
		public String function;
	}

	public static class DKChangeable extends Packet {
		public Changeable changeable;
	}

	public static class PlayingState extends Packet {
		public boolean playing;
	}

	public static class LobbyPacket extends Packet {
	}

	public static class LobbyData extends Packet {
		public ObjectMap<String, Object> values;
	}

	public static class SetLobbyData extends LobbyPacket {
		public String key;
		public Object value;
	}

	public static class DeleteLobbyData extends LobbyPacket {
		public String key;
	}

	public static class GameStateRequest extends Packet {
	}

	public static class GameState extends Packet {
		public String state;
		public String path;

		public GameState () {
		}

		public GameState (String state, String path) {
			this.state = state;
			this.path = path;
		}
	}

}
