
package com.ravelsoftware.ravtech;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.ravelsoftware.ravtech.files.RavFiles;
import com.ravelsoftware.ravtech.input.RavInput;
import com.ravelsoftware.ravtech.project.Project;
import com.ravelsoftware.ravtech.screens.PlayScreen;
import com.ravelsoftware.ravtech.scripts.WebGLScriptManager;
import com.ravelsoftware.ravtech.settings.RavSettings;

public class RavTech extends Game {

	public static Project project;
	public static final int majorVersion = 0;
	public static final int minorVersion = 1;
	public static final int microVersion = 0;
	// Renderers
	public static SpriteBatch spriteBatch;
	public static ShapeRenderer shapeRenderer;
	// Scene
	public static Scene currentScene = new Scene();
	public static SceneHandler sceneHandler;
	// RavTech Components
	public static RavFiles files;
	public static RavSettings settings;
	public static RavInput input;
	public static FPSLogger logger = new FPSLogger();
	public static boolean isEditor;

	public RavTech (FileHandleResolver assetResolver, Project project) {
		files = new RavFiles(assetResolver);
		RavTech.project = project;
	}

	public RavTech (FileHandleResolver assetResolver) {
		files = new RavFiles(assetResolver);
	}

	@Override
	public void create () {
		if (!isEditor) {
			files.loadAsset("project.json", Project.class);
			files.finishLoading();
			RavTech.project = files.getAsset("project.json");
		}
		// Serializes current Scene for later restore after context loss
		final String serializedCurrentScene = input != null ? files.storeState() : null;
		if (input != null) sceneHandler.dispose();
		input = new RavInput();
		settings = new RavSettings();
		settings.save();
		sceneHandler = new SceneHandler();
		sceneHandler.load();
		spriteBatch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setAutoShapeType(true);
		if (serializedCurrentScene != null)
			files.loadState(serializedCurrentScene);
		else {
			files.loadAsset(project.startScene, Scene.class);
			files.finishLoading();
			RavTech.currentScene = files.getAsset(project.startScene);
		}
		this.setScreen(new PlayScreen());
		if (Gdx.app.getType() != ApplicationType.WebGL && !isEditor) sceneHandler.initScripts();
	}

	@Override
	public void render () {
		logger.log();
		if (Gdx.app.getType() == ApplicationType.WebGL && !WebGLScriptManager.areLoaded())
			return;
		else if (!WebGLScriptManager.initialized) WebGLScriptManager.initialize();
		for (int i = 0; i < HookApi.onUpdateHooks.size; i++)
			HookApi.onUpdateHooks.get(i).run();
		super.render();
		for (int i = 0; i < HookApi.onRenderHooks.size; i++)
			HookApi.onRenderHooks.get(i).run();
	}
}
