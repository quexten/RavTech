
package com.quexten.ravtech;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.quexten.ravtech.files.RavFiles;
import com.quexten.ravtech.input.RavInput;
import com.quexten.ravtech.project.Project;
import com.quexten.ravtech.screens.PlayScreen;
import com.quexten.ravtech.scripts.ScriptLoader;
import com.quexten.ravtech.scripts.WebGLScriptManager;
import com.quexten.ravtech.settings.RavSettings;
import com.quexten.ravtech.ui.RavUI;

public class RavTech extends Game {

	public static final int majorVersion = 0;
	public static final int minorVersion = 2;
	public static final int microVersion = 1;

	public static boolean isEditor;

	public static EngineConfiguration engineConfiguration;
	public static Project project;

	// Scene
	public static Scene currentScene = new Scene();
	public static SceneHandler sceneHandler;
	public static ScriptLoader scriptLoader;

	// RavTech Components
	public static RavFiles files;
	public static RavSettings settings;
	public static RavInput input;
	public static RavUI ui;

	public RavTech (FileHandleResolver assetResolver, Project project, EngineConfiguration applicationConfig) {
		this(assetResolver, applicationConfig);
		RavTech.project = project;
	}

	public RavTech (FileHandleResolver assetResolver, EngineConfiguration applicationConfig) {
		files = new RavFiles(assetResolver);
		engineConfiguration = applicationConfig;
	}

	@Override
	public void create () {
		Gdx.app.setLogLevel(3);
		if (!isEditor) {
			files.loadAsset("project.json", Project.class);
			files.finishLoading();
			RavTech.project = files.getAsset("project.json");
		}

		// Serializes current Scene for later restore after context loss
		final String serializedCurrentScene = input != null ? files.storeState() : null;
		if (input != null)
			sceneHandler.dispose();
		input = new RavInput();
		ui = new RavUI();
		settings = new RavSettings();
		settings.save();
		sceneHandler = new SceneHandler();
		sceneHandler.load();

		if (serializedCurrentScene != null)
			files.loadState(serializedCurrentScene);
		else if (!isEditor || files.hasAsset(project.startScene)) {
			files.loadAsset(project.startScene, Scene.class);
			files.finishLoading();
			RavTech.currentScene = files.getAsset(project.startScene);
		}

		setScreen(new PlayScreen());
		sceneHandler.paused = true;
		sceneHandler.update(0);
		sceneHandler.paused = false;
		if (Gdx.app.getType() != ApplicationType.WebGL && !isEditor)
			sceneHandler.initScripts();
	}

	@Override
	public void render () {
		if (Gdx.app.getType() == ApplicationType.WebGL && !WebGLScriptManager.areLoaded())
			return;
		else if (!WebGLScriptManager.initialized)
			WebGLScriptManager.initialize();

		for (int i = 0; i < HookApi.onUpdateHooks.size; i++)
			HookApi.onUpdateHooks.get(i).run();

		super.render();

		for (int i = 0; i < HookApi.onRenderHooks.size; i++)
			HookApi.onRenderHooks.get(i).run();

		ui.render();
	}
}
