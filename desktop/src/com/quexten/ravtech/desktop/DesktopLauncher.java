package com.quexten.ravtech.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.quexten.ravtech.Hook;
import com.quexten.ravtech.HookApi;
import com.quexten.ravtech.RavTech;
import com.quexten.ravtech.Scene;

public class DesktopLauncher {
	public static void main (String[] arg) {		
		final Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowedMode(1280, 720);
		config.setDecorated(true);
		config.useVsync(true);		
		config.setTitle("RavTech");
		DesktopEngineConfiguration engineConfiguration = new DesktopEngineConfiguration();
		
		final RavTech ravtech = new RavTech(engineConfiguration);
		
		engineConfiguration.remoteEdit = false;
		
		HookApi.addHook("onBoot", new Hook() {
 		  @Override
 		  public void run() {
 			  RavTech.files.loadAsset("map.scene", Scene.class);
 			  RavTech.files.finishLoading();
 			  RavTech.currentScene = RavTech.files.getAsset("map.scene", Scene.class);	  
 		  }
 	  });
		
		new Lwjgl3Application(ravtech, config);
	}
}
