package com.quexten.ravtech.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.quexten.ravtech.Hook;
import com.quexten.ravtech.HookApi;
import com.quexten.ravtech.RavTech;
import com.quexten.ravtech.Scene;
import com.quexten.ravtech.components.Camera;
import com.quexten.ravtech.components.GameObject;
import com.quexten.ravtech.components.SpriteRenderer;
import com.quexten.ravtech.files.SceneLoader;

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
		
		HookApi.addHook("onBoot", new Hook() {
 		  @Override
 		  public void run() {
 			  RavTech.files.loadAsset("map.scene", Scene.class);
 			  RavTech.files.finishLoading();
 			  RavTech.currentScene = RavTech.files.getAsset("map.scene", Scene.class);
 		  }
 	  });
		HookApi.addHook("onUpdate", new Hook() {
			int i = 0;
			@Override
			public void run() {
				i++;
				GameObject.find("Camera").transform.setLocalPosition(0, (float)(Math.sin(i * 0.01) * 5));
			}
		});
		
		new Lwjgl3Application(ravtech, config);
	}
}
