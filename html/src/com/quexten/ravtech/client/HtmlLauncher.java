package com.quexten.ravtech.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.quexten.ravtech.Hook;
import com.quexten.ravtech.HookApi;
import com.quexten.ravtech.RavTech;
import com.quexten.ravtech.Scene;
import com.quexten.ravtech.components.Camera;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(480, 320);
        }

        @Override
        public ApplicationListener createApplicationListener () {
      	  RavTech ravtech = new RavTech(new HtmlEngineConfiguration());
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
           return ravtech;
        }
}