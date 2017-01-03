
package com.quexten.ravtech;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.quexten.ravtech.files.RavFiles;
import com.quexten.ravtech.input.RavInput;
import com.quexten.ravtech.project.Project;
import com.quexten.ravtech.screens.PlayScreen;
import com.quexten.ravtech.scripts.ScriptLoader;
import com.quexten.ravtech.settings.RavSettings;
import com.quexten.ravtech.ui.RavUI;
import com.quexten.ravtech.net.RavNetwork;

public class RavTech extends Game {

	// RavTech Version
	public static final int majorVersion = 0;
	public static final int minorVersion = 2;
	public static final int microVersion = 1;

	// TODO Make this variable obsolete and remove it
	public static boolean isEditor = false;

	// Configurations
	public static EngineConfiguration engineConfiguration;
	public static Project project;

	// Scene
	public static Scene currentScene = new Scene();
	public static SceneHandler sceneHandler;
	public static ScriptLoader scriptLoader;

	// RavTech Components
	public static RavFiles files;
	public static RavInput input;
	public static RavNetwork net;
	public static RavSettings settings;
	public static RavUI ui;

	public RavTech (EngineConfiguration applicationConfig) {
		engineConfiguration = applicationConfig;
	}

	@Override
	public void create () {
		HookApi.runHooks("onPreBoot");

		Gdx.app.setLogLevel(3);

		// Initialize Core Components
		files = new RavFiles(engineConfiguration.assetResolver);
		input = new RavInput();
		net = new RavNetwork();
		ui = new RavUI();

		sceneHandler = new SceneHandler();
		setScreen(new PlayScreen());

		HookApi.runHooks("onBoot");		
	}

	@Override
	public void render () {
		//Update
		input.update();
		
		HookApi.runHooks("onUpdate");
		
		//Render
		super.render();
		
		if (!RavTech.isHeadless()) {			
			HookApi.runHooks("onRender");
			
			ui.render();
		}
	}

	public static boolean isHeadless () {
		return Gdx.app.getType() == ApplicationType.HeadlessDesktop;
	}

}
