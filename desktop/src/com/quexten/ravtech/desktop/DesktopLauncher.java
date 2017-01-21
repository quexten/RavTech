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
 			  for(int x = 0; x < 200; x++) {
 				  for(int y = 0; y < 200; y++) {
 					  GameObject object = RavTech.currentScene.addGameObject(7, 0);
		 			  SpriteRenderer renderer = new SpriteRenderer();
		 			  renderer.setTexture("textures/error.png");
		 			  object.addComponent(renderer);
 				  }
 			  } 			  
 		  }
 	  });
		
		new Lwjgl3Application(ravtech, config);
	}
}
