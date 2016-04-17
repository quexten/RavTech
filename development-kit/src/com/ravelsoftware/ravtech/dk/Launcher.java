
package com.ravelsoftware.ravtech.dk;

import java.io.File;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowListener;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.dk.adb.AdbManager;
import com.ravelsoftware.ravtech.scripts.lua.LuaJScriptLoader;
import com.ravelsoftware.ravtech.settings.SettingsValueListener;
import com.ravelsoftware.ravtech.util.Debug;

public class Launcher {

	public static void main (String[] args) {
		RavTech.isEditor = true;
		initializeEngine();
	}

	static void initializeEngine () {
		final RavTechDKApplication ravtech = new RavTechDKApplication();

		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowedMode(1600, 900);
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
			public boolean windowIsClosing () {
				System.exit(0);
				return true;
			}

		});
		RavTech.scriptLoader = new LuaJScriptLoader();
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
