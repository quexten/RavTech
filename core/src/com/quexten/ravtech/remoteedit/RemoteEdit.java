
package com.quexten.ravtech.remoteedit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.quexten.ravtech.Hook;
import com.quexten.ravtech.RavTech;
import com.quexten.ravtech.Scene;
import com.quexten.ravtech.components.SpriteRenderer;
import com.quexten.ravtech.files.SceneLoader;
import com.quexten.ravtech.history.ChangeListener;
import com.quexten.ravtech.history.ChangeManager;
import com.quexten.ravtech.history.Changeable;
import com.quexten.ravtech.net.Packet;
import com.quexten.ravtech.net.PacketProcessor;
import com.quexten.ravtech.net.Player;
import com.quexten.ravtech.net.RavNetwork;
import com.quexten.ravtech.project.Project;
import com.quexten.ravtech.screens.PlayScreen;
import com.quexten.ravtech.util.Debug;
import com.quexten.ravtech.util.ZipUtil;

public class RemoteEdit {

	public static void host () {
		RavTech.net.createLobby(54555, 54554, 4);

		RavTech.net.addProcessor(new PacketProcessor() {
			@Override
			public boolean process (Player player, Packet packet) {
				if (packet instanceof Packet.AssetRequest) {
					Debug.log("Server", "Recieved assetrequest" + player);
					player.sendStream(Gdx.files.local("temp/build.ravpack").read(),
						(int)Gdx.files.local("temp/build.ravpack").length(), "Assets", "");
				}
				return false;
			}
		});

		RavTech.net.addProcessor(new PacketProcessor() {
			@Override
			public boolean process (Player player, Packet packet) {				
				if (packet instanceof Packet.GameStateRequest) {
					Debug.log("Server", "Recieved gamestate request");
					player.sendLargePacket(new Packet.GameState(new Json().toJson(RavTech.currentScene), "map.map"),
						RavNetwork.LARGE_Packet_HEADER_TYPE, "map.map");
				}
				return false;
			}
		});

		ChangeManager.addChangeableListener(new ChangeListener() {
			@Override
			public void changed (Changeable changeable) {
				Packet.DKChangeable changePacket = new Packet.DKChangeable();
				changePacket.changeable = changeable;
				Debug.log("chage", changePacket);
				RavTech.net.send(changePacket, true);
			}
		});
	}

	public static void connect (String connectionId) {
		final FileHandle cacheAssetFile = Gdx.files.local("cache").child("assets.ravpack");
		cacheAssetFile.delete();
		
		final RemoteEditLoadingScreen loadingScreen = new RemoteEditLoadingScreen();

		RavTech.net.joinLobby(connectionId);

		RavTech.net.lobby.onJoinedHooks.add(new Hook() {
			@Override
			public void run (Object arg) {
				Player player = (Player)arg;
				Debug.log("Joined", "start");
				Packet.AssetRequest assetRequest = new Packet.AssetRequest();
				assetRequest.fileHashes = FileHasher.getHashes(Gdx.files.local("cache"));	
				Debug.log("Joined","send");
				player.send(assetRequest, true);
			}
		});

		RavTech.net.addProcessor(new PacketProcessor() {

			int recievingStreamId = -1;
			int length = -1;
			int recievedLength;

			@Override
			public boolean process (Player player, Packet packet) {
				if (packet instanceof Packet.StreamHeader) {
					Packet.StreamHeader streamHeader = ((Packet.StreamHeader)packet);					
					if (streamHeader.type.equals("Assets")) {						
						recievingStreamId = streamHeader.streamId;
						length = streamHeader.streamLength;
						
						Debug.log("Streamheader", recievingStreamId + " | " + length / Math.pow(10, 6) + "MB");
						
						Gdx.app.postRunnable(new Runnable() {
							@Override
							public void run () {
								((RavTech)Gdx.app.getApplicationListener()).setScreen(loadingScreen);
							}
						});
						return true;
					} else {
						return false;
					}
				}

				if (packet instanceof Packet.StreamChunk) {
					Packet.StreamChunk streamChunk = ((Packet.StreamChunk)packet);
					if (streamChunk.streamId == recievingStreamId) {
						cacheAssetFile.writeBytes(streamChunk.chunkBytes, true);
						recievedLength += streamChunk.chunkBytes.length;

						loadingScreen.percentage = (float)recievedLength / (float)length;
						if (recievedLength == length) {
							Gdx.files.local("cach").child("project.json").delete();
							ZipUtil.extract(cacheAssetFile, Gdx.files.local("cache"));
							final FileHandle cacheHandle = Gdx.files.local("cache");

							RavTech.files.setResolver(new FileHandleResolver() {
								@Override
								public FileHandle resolve (String fileName) {
									return cacheHandle.child(fileName);
								}
							});

							RavTech.files.loadAsset("project.json", Project.class);
							RavTech.files.finishLoading();
							RavTech.project = RavTech.files.getAsset("project.json");
							player.send(new Packet.GameStateRequest(), true);
						}
						return false;
					}
				}
				return false;
			}
		});

		RavTech.net.addProcessor(new PacketProcessor() {
			@Override
			public boolean process (Player player, final Packet packet) {
				if (packet instanceof Packet.GameState) {
					Gdx.app.postRunnable(new Runnable() {
						@Override
						public void run () {
							RavTech.files.setResolver(new FileHandleResolver() {
								@Override
								public FileHandle resolve (String fileName) {
									return Gdx.files.local("cache").child(fileName);
								}
							});

							RavTech.files.getAssetManager().setLoader(Scene.class,
								new DummySceneLoader(((Packet.GameState)packet).state));
							RavTech.files.loadAsset(((Packet.GameState)packet).path, Scene.class);
							RavTech.files.finishLoading();
							RavTech.files.getAssetManager().setLoader(Scene.class, new SceneLoader(RavTech.files.getResolver()));
							RavTech.currentScene = RavTech.files.getAsset(((Packet.GameState)packet).path);

							((RavTech)Gdx.app.getApplicationListener()).setScreen(new PlayScreen());
							RavTech.sceneHandler.paused = true;
							RavTech.sceneHandler.update(0);
						}
					});
				}
				return false;
			}
		});

		RavTech.net.addProcessor(new PacketProcessor() {
			@Override
			public boolean process (Player player, final Packet packet) {
				Debug.log("Packet", packet);
				if (packet instanceof Packet.DKChangeable) {
					Debug.log("dummy", ((Packet.DKChangeable)packet).changeable.isDummy);
					Debug.log("local", ((Packet.DKChangeable)packet).changeable.isLocal);
					((Packet.DKChangeable)packet).changeable.redo();
					ChangeManager.addChangeable(((Packet.DKChangeable)packet).changeable);
				}
				return false;
			}
		});
	}

}
