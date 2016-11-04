
package com.quexten.ravtech.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Files;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowListener;
import com.badlogic.gdx.utils.Json;
import com.quexten.ravtech.Hook;
import com.quexten.ravtech.HookApi;
import com.quexten.ravtech.RavTech;
import com.quexten.ravtech.Scene;
import com.quexten.ravtech.files.zip.ArchiveFileHandleResolver;
import com.quexten.ravtech.net.kryonet.KryonetTransportLayer;
import com.quexten.ravtech.project.Project;
import com.quexten.ravtech.remoteedit.RemoteEditConnectionScreen;
import com.quexten.ravtech.scripts.lua.LuaJScriptLoader;
import com.quexten.ravtech.settings.RavSettings;
import com.quexten.ravtech.util.Debug;

public class DesktopLauncher {

	public static void main (String[] arg) {
		Lwjgl3Files files = new Lwjgl3Files();
		DesktopEngineConfiguration engineConfiguration = new Json().fromJson(DesktopEngineConfiguration.class,
			files.getFileHandle("config.json", FileType.Internal).readString());

		final Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowedMode(1280, 720);
		config.setDecorated(true);
		config.useVsync(true);
		config.setWindowListener(new Lwjgl3WindowListener() {

			@Override
			public void iconified () {
			}

			@Override
			public void deiconified () {
			}

			@Override
			public void focusLost () {
			}

			@Override
			public void focusGained () {
			}

			@Override
			public boolean closeRequested () {
				System.exit(0);
				return true;
			}

			@Override
			public void filesDropped (String[] files) {
			}

		});
		config.setTitle(engineConfiguration.title);
		config.setResizable(engineConfiguration.resizable);

		boolean useExternalAssetBundle = engineConfiguration.useAssetBundle;
		engineConfiguration.assetResolver = useExternalAssetBundle
			? new ArchiveFileHandleResolver(new Lwjgl3Files().local("assets.ravpack")) : new InternalFileHandleResolver();

		final RavTech ravtech = new RavTech(engineConfiguration);
		
		engineConfiguration.remoteEdit = true;
		if(!engineConfiguration.remoteEdit)
			HookApi.onBootHooks.add(new Hook() {
				@Override
				public void run() {
					RavTech.files.loadAsset("project.json", Project.class);
					RavTech.files.finishLoading();
					RavTech.project = RavTech.files.getAsset("project.json");
					
					RavTech.files.loadAsset(RavTech.project.startScene, Scene.class);
					RavTech.files.finishLoading();
					RavTech.currentScene = RavTech.files.getAsset(RavTech.project.startScene);
					Debug.log("RavTech", "BootUp");
				}
			});
		else
			HookApi.onBootHooks.add(new Hook() {
				@Override
				public void run() {
					Project project = new Project();
					project.appId = "com.quexten.ravtech.remoteedit";
					project.appName = "RavTech-RemoteEdit";
					project.buildVersion = 1;
					project.developerName = "quexten";
					project.majorVersion = 0;
					project.minorVersion = 0;
					project.microVersion = 1;
					project.startScene = "";
					project.versionName = "Alpha";
					
					RavTech.project = project;					
					RavTech.settings = new RavSettings(project.appName);
					RavTech.net.transportLayers.add(new KryonetTransportLayer(RavTech.net));
					ravtech.setScreen(new RemoteEditConnectionScreen());
					RavTech.ui.debugConsole.setVisible(false);
				}
			});
		
		System.out.println(
			"Initializing Ravtech - Desktop using " + (useExternalAssetBundle ? " External " : " Internal ") + "FileHandle");

		RavTech.scriptLoader = new LuaJScriptLoader();
		new Lwjgl3Application(ravtech, config);
	}

	/** public static void main (String[] arg) { Lwjgl3ApplicationConfiguration appConfig = new Lwjgl3ApplicationConfiguration();
	 * appConfig.setWindowedMode(1600, 900);
	 * 
	 * 
	 * EngineConfiguration engineConfig = new EngineConfiguration(); engineConfig.title = "Remote Edit";
	 * 
	 * final RavTech ravtech = new RavTech(new InternalFileHandleResolver(), engineConfig); HookApi.onBootHooks.add(new Hook() {
	 * @Override public void run () { RavTech.net.transportLayers.add(new KryonetTransportLayer(RavTech.net));
	 *           ravtech.setScreen(new RemoteEditConnectionScreen()); RavTech.ui.debugConsole.setVisible(false); } });
	 *           RavTech.scriptLoader = new LuaJScriptLoader();
	 * 
	 *           new Lwjgl3Application(ravtech, appConfig); } */
}
