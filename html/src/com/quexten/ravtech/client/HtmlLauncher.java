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
import com.quexten.ravtech.components.GameObject;

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
     		
           return ravtech;
        }
}