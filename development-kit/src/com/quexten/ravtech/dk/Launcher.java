
package com.quexten.ravtech.dk;

import java.io.File;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3FileHandle;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Preferences;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowListener;
import com.kotcrab.vis.ui.VisUI;
import com.quexten.ravtech.EngineConfiguration;
import com.quexten.ravtech.Hook;
import com.quexten.ravtech.HookApi;
import com.quexten.ravtech.RavTech;
import com.quexten.ravtech.dk.adb.AdbManager;
import com.quexten.ravtech.net.kryonet.KryonetTransportLayer;
import com.quexten.ravtech.remoteedit.RemoteEdit;
import com.quexten.ravtech.scripts.lua.LuaJScriptLoader;
import com.quexten.ravtech.settings.RavSettings;
import com.quexten.ravtech.settings.SettingsValueListener;
import com.quexten.ravtech.util.Debug;

public class Launcher {

	public static void main (String[] args) {
		RavTech.isEditor = true;
		initializeEngine();
	}

	static void initializeEngine () {
		final RavTech ravtech = new RavTech(new EngineConfiguration());

		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowedMode(1600, 900);
		config.useVsync(false);
		config.setTitle("RavTech Development Kit Version " + RavTechDK.getVersionString());
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
				return false;
			}

			@Override
			public void filesDropped (String[] files) {
				Debug.log("Dropped", files);
			}
		});
		RavTech.scriptLoader = new LuaJScriptLoader();
	
		
		
		HookApi.onBootHooks.add(new Hook() {
			@Override
			public void run () {
				RavTech.sceneHandler.paused = true;
				
				//TODO Have network run on new thread
				RavTech.net.transportLayers.add(new KryonetTransportLayer(RavTech.net));
				RemoteEdit.host();
				
				/*if (RavTech.settings.getString("RavTechDK.project.path").isEmpty()
					|| !new Lwjgl3FileHandle(RavTech.settings.getString("RavTechDK.project.path"), FileType.Absolute).child("project.json")
						.exists()) {
					final Project project = new Project();
					final ProjectSettingsWizard wizard = new ProjectSettingsWizard(project, true);
					wizard.setSize(330, 330);
					RavTech.ui.getStage().addActor(wizard);
				} else {
					final Preferences preferences = new Lwjgl3Preferences(
						new Lwjgl3FileHandle(new File(".prefs/", "RavTech"), FileType.External));
					RavTechDK.setProject(preferences.getString("RavTechDK.project.path"));
				}*/
				
			}
		});
		
		HookApi.onPreBootHooks.add(new Hook() {
			@Override
			public void run() {
				VisUI.dispose();
				VisUI.load(Gdx.files.internal("tinted/x1/tinted.json"));				
			}
		});
		
		HookApi.onBootHooks.add(new Hook() {
			@Override
			public void run() {
				RavTech.settings = new RavSettings("RavTechDK");
				
				RavTechDK.initialize();
				final Preferences preferences = new Lwjgl3Preferences(
					new Lwjgl3FileHandle(new File(".prefs/", "RavTech"), FileType.External));
				RavTechDK.setProject(preferences.getString("RavTechDK.project.path"));
				
			}
		});
		new Lwjgl3Application(ravtech, config);

		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run () {
				registerSettingsListeners();
			}
		});
	}

	public static void registerSettingsListeners () {
		RavTech.settings.addValueListener("RavTechDK.android.sdk.dir", new SettingsValueListener() {
			@Override
			public void settingChanged (Object oldValue, Object newValue) {
				Gdx.files.local("builder/local.properties")
					.writeString("# Location of the android SDK\n" + "sdk.dir=" + String.valueOf(newValue), false);
				Debug.log("Adb", Gdx.files.local("builder/local.properties").path());
				AdbManager.adbLocation = new File(String.valueOf(newValue) + System.getProperty("file.separator") + "platform-tools");
				AdbManager.initAdbConnection();
			}
		});
	}
}
