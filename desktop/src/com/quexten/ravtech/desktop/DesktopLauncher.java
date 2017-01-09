package com.quexten.ravtech.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.quexten.ravtech.Hook;
import com.quexten.ravtech.HookApi;
import com.quexten.ravtech.RavTech;
import com.quexten.ravtech.Scene;
import com.quexten.ravtech.components.Camera;

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
 			  Scene scene = new Scene();
 			  Camera camera = new Camera();
 			  camera.finishedLoading();
 			  camera.camera.setRenderToFramebuffer(false);
 			  scene.addGameObject(0, 0)
 			  	.addComponent(camera);
 			  camera.camera.setClearColor(Color.GREEN);
 		  }
 	  });
		
		new Lwjgl3Application(ravtech, config);
	}
}
