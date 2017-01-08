package com.quexten.ravtech.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.quexten.ravtech.RavTech;

public class DesktopLauncher {
	public static void main (String[] arg) {		
		final Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowedMode(1280, 720);
		config.setDecorated(true);
		config.useVsync(true);		
		config.setTitle("Test");

		DesktopEngineConfiguration engineConfiguration = new DesktopEngineConfiguration();
		
		final RavTech ravtech = new RavTech(engineConfiguration);
		
		engineConfiguration.remoteEdit = false;
		new Lwjgl3Application(ravtech, config);
	}
}
