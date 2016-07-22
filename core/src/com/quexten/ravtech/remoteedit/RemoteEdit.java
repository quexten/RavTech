
package com.quexten.ravtech.remoteedit;

import java.io.ByteArrayInputStream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.esotericsoftware.kryo.io.ByteBufferInput;
import com.quexten.ravtech.Hook;
import com.quexten.ravtech.RavTech;
import com.quexten.ravtech.Scene;
import com.quexten.ravtech.files.SceneLoader;
import com.quexten.ravtech.net.Packet.Packet_GameStateRequest;
import com.quexten.ravtech.net.Packet.Packet_StreamChunk;
import com.quexten.ravtech.net.Packet.Packet_StreamHeader;
import com.quexten.ravtech.net.PacketProcessor;
import com.quexten.ravtech.net.Player;
import com.quexten.ravtech.project.Project;
import com.quexten.ravtech.screens.PlayScreen;
import com.quexten.ravtech.util.Debug;
import com.quexten.ravtech.util.ZipUtil;

public class RemoteEdit {

	public static void host() {
		RavTech.net.createLobby(54555, 54554, 4);		
		
		RavTech.net.lobby.onJoinedHooks.add(new Hook() {
			@Override
			public void run(Object arg) {
				Player player = (Player) arg;
				player.sendStream("Assets", "", Gdx.files.local("temp/build.ravpack").read(), (int) Gdx.files.local("temp/build.ravpack").length());
			}
		});		
		
		RavTech.net.addProcessor(new PacketProcessor() {
			@Override
			public boolean process(Player player, Object packet) {
				if(packet instanceof Packet_GameStateRequest) {
					player.sendLargePacket("GameState", "map.map", new Json().toJson(RavTech.currentScene));
				}			
				return false;
			}			
		});
	}

	public static void connect(String connectionId) {
		final FileHandle cacheAssetFile = Gdx.files.local("cache").child("assets.ravpack");

		RavTech.net.joinLobby(connectionId);
		RavTech.net.addProcessor(new PacketProcessor() {

			int recievingStreamId = -1;
			int length = -1;
			int recievedLength;

			@Override
			public boolean process(Player player, Object packet) {
				if (packet instanceof Packet_StreamHeader) {
					Packet_StreamHeader streamHeader = ((Packet_StreamHeader) packet);
					if (streamHeader.type.equals("Assets")) {
						recievingStreamId = streamHeader.streamId;
						length = streamHeader.streamLength;
						return true;
					} else {
						return false;
					}
				}

				if (packet instanceof Packet_StreamChunk) {
					Packet_StreamChunk streamChunk = ((Packet_StreamChunk) packet);
					if (streamChunk.streamId == recievingStreamId) {
						cacheAssetFile.writeBytes(streamChunk.chunkBytes, true);
						recievedLength += streamChunk.chunkBytes.length;
						//Debug.log("Recieved", (float) recievedLength + "/" + (float) length + " "
						//		+ ((float) recievedLength / (float) length) * 100 + "%");
						if (recievedLength == length) {
							ZipUtil.extract(cacheAssetFile, Gdx.files.local("cache"));
							Debug.log("Ziputil", "done");
							final FileHandle cacheHandle = Gdx.files.local("cache");
							
							RavTech.files.setResolver(new FileHandleResolver() {
								@Override
								public FileHandle resolve(String fileName) {
									return cacheHandle.child(fileName);
								}
							});
							
							Debug.log("Cache files", cacheHandle);
							
							RavTech.files.loadAsset("project.json", Project.class);
							RavTech.files.finishLoading();
							RavTech.project = RavTech.files.getAsset("project.json");
							player.send(new Packet_GameStateRequest(), true);
						}
						return true;
					}
				}
				return false;
			}
		});

		RavTech.net.addProcessor(new PacketProcessor() {

			int recievingStreamId = -1;
			int length = -1;
			int recievedLength;
			byte[] buffer;

			ByteBufferInput input = new ByteBufferInput(8192);
			String additionalInformation;
			
			
			@Override
			public boolean process(Player player, Object packet) {
				if (packet instanceof Packet_StreamHeader) {
					Packet_StreamHeader streamHeader = ((Packet_StreamHeader) packet);
					Debug.log("Type", streamHeader.type);
					Debug.log("Additional Info", streamHeader.additionalInfo);
					Debug.log("Id", streamHeader.streamId);
					Debug.log("Length", streamHeader.streamLength);
					
					if (streamHeader.type.equals("GameState")) {
						recievingStreamId = streamHeader.streamId;
						length = streamHeader.streamLength;
						buffer = new byte[length];
						this.additionalInformation = (String) streamHeader.additionalInfo;
						return true;
					} else {
						return false;
					}
				}

				if (packet instanceof Packet_StreamChunk) {
					Packet_StreamChunk streamChunk = ((Packet_StreamChunk) packet);
					if (streamChunk.streamId == recievingStreamId) {
						for (int i = recievedLength; i < recievedLength + streamChunk.chunkBytes.length; i++) {
							buffer[i] = streamChunk.chunkBytes[i - recievedLength];
						}
						recievedLength += streamChunk.chunkBytes.length;
						Debug.log("Recieved", (float) recievedLength + "/" + (float) length + " "
								+ ((float) recievedLength / (float) length) * 100 + "%");
						if (recievedLength == length) {
							input.setInputStream(new ByteArrayInputStream(buffer));
							Gdx.app.postRunnable(new Runnable() {
								@Override
								public void run() {
									try {
										FileHandle cacheHandle = Gdx.files.local("cache");
										for(int i = 0; i < cacheHandle.list().length; i++) {
											Debug.log("Entry", cacheHandle.list()[i]);
											Debug.log("Exists", cacheHandle.list()[i].exists());
										}
										
										final String content = new String(buffer, "UTF-8");
										RavTech.files.setResolver(new FileHandleResolver() {
											@Override
											public FileHandle resolve(String fileName) {
												Debug.log("Resolve", fileName);
												Debug.log("Exists", Gdx.files.local("cache").child(fileName).exists());
												return Gdx.files.local("cache").child(fileName);
											}									
										});
										cacheHandle.child("Test.test").writeString("test", false);
										RavTech.files.getAssetManager().setLoader(Scene.class, new DummySceneLoader(content));
										RavTech.files.loadAsset(additionalInformation, Scene.class);
										RavTech.files.finishLoading();
										RavTech.files.getAssetManager().setLoader(Scene.class, new SceneLoader(RavTech.files.getResolver()));
										RavTech.currentScene = RavTech.files.getAsset(additionalInformation);
									} catch(Exception ex) {
										ex.printStackTrace();
									}
		
									((RavTech) Gdx.app.getApplicationListener()).setScreen(new PlayScreen());
									RavTech.sceneHandler.paused = true;
									RavTech.sceneHandler.update(0);
									RavTech.sceneHandler.paused = false;
								}
							});
							
							
						}
						return true;
					}
				}
				return false;
			}
		});
	}

}
