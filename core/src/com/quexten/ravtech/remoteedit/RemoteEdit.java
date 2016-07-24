
package com.quexten.ravtech.remoteedit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.quexten.ravtech.Hook;
import com.quexten.ravtech.RavTech;
import com.quexten.ravtech.Scene;
import com.quexten.ravtech.files.SceneLoader;
import com.quexten.ravtech.net.Packet;
import com.quexten.ravtech.net.Packet.Packet_GameState;
import com.quexten.ravtech.net.Packet.Packet_GameStateRequest;
import com.quexten.ravtech.net.Packet.Packet_StreamChunk;
import com.quexten.ravtech.net.Packet.Packet_StreamHeader;
import com.quexten.ravtech.net.PacketProcessor;
import com.quexten.ravtech.net.Player;
import com.quexten.ravtech.net.RavNetwork;
import com.quexten.ravtech.project.Project;
import com.quexten.ravtech.screens.PlayScreen;
import com.quexten.ravtech.util.ZipUtil;

public class RemoteEdit {

	public static void host () {
		RavTech.net.createLobby(54555, 54554, 4);

		RavTech.net.lobby.onJoinedHooks.add(new Hook() {
			@Override
			public void run (Object arg) {
				Player player = (Player)arg;
				player.sendStream(Gdx.files.local("temp/build.ravpack").read(), (int)Gdx.files.local("temp/build.ravpack").length(),
					"Assets", "");
			}
		});

		RavTech.net.addProcessor(new PacketProcessor() {
			@Override
			public boolean process (Player player, Packet packet) {
				if (packet instanceof Packet_GameStateRequest) {
					player.sendLargePacket(new Packet_GameState(new Json().toJson(RavTech.currentScene), "map.map"),
						RavNetwork.LARGE_PACKET_HEADER_TYPE, "map.map");
				}
				return false;
			}
		});
	}

	public static void connect (String connectionId) {
		final FileHandle cacheAssetFile = Gdx.files.local("cache").child("assets.ravpack");

		final RemoteEditLoadingScreen loadingScreen = new RemoteEditLoadingScreen();

		RavTech.net.joinLobby(connectionId);
		RavTech.net.addProcessor(new PacketProcessor() {

			int recievingStreamId = -1;
			int length = -1;
			int recievedLength;

			@Override
			public boolean process (Player player, Packet packet) {
				if (packet instanceof Packet_StreamHeader) {
					Packet_StreamHeader streamHeader = ((Packet_StreamHeader)packet);
					if (streamHeader.type.equals("Assets")) {
						recievingStreamId = streamHeader.streamId;
						length = streamHeader.streamLength;

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

				if (packet instanceof Packet_StreamChunk) {
					Packet_StreamChunk streamChunk = ((Packet_StreamChunk)packet);
					if (streamChunk.streamId == recievingStreamId) {
						cacheAssetFile.writeBytes(streamChunk.chunkBytes, true);
						recievedLength += streamChunk.chunkBytes.length;

						loadingScreen.percentage = (float)recievedLength / (float)length;
						if (recievedLength == length) {
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
							player.send(new Packet_GameStateRequest(), true);
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
				if (packet instanceof Packet_GameState) {
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
								new DummySceneLoader(((Packet_GameState)packet).state));
							RavTech.files.loadAsset(((Packet_GameState)packet).path, Scene.class);
							RavTech.files.finishLoading();
							RavTech.files.getAssetManager().setLoader(Scene.class, new SceneLoader(RavTech.files.getResolver()));
							RavTech.currentScene = RavTech.files.getAsset(((Packet_GameState)packet).path);

							((RavTech)Gdx.app.getApplicationListener()).setScreen(new PlayScreen());
							RavTech.sceneHandler.paused = true;
							RavTech.sceneHandler.update(0);
						}
					});
				}
				return false;
			}
		});
	}

}
