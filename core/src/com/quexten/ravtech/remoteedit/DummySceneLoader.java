package com.quexten.ravtech.remoteedit;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.quexten.ravtech.RavTech;
import com.quexten.ravtech.Scene;

public class DummySceneLoader extends AsynchronousAssetLoader<Scene, AssetLoaderParameters<Scene>> {

	Scene scene;
	String serializedScene;
	
	public DummySceneLoader (String serializedScene) {
		super(RavTech.files.getResolver());
		this.serializedScene = serializedScene;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Array<AssetDescriptor> getDependencies (String fileName, FileHandle file, AssetLoaderParameters<Scene> parameter) {
		scene = new Json().fromJson(Scene.class, serializedScene);
		Array<AssetDescriptor> assetDependencies = new Array<AssetDescriptor>();
		for (int i = 0; i < scene.gameObjects.size; i++)
			scene.gameObjects.get(i).load(assetDependencies);
		return assetDependencies;
	}

	@Override
	public void loadAsync (AssetManager manager, String fileName, FileHandle file, AssetLoaderParameters<Scene> parameter) {
	}

	@Override
	public Scene loadSync (AssetManager manager, String fileName, FileHandle file, AssetLoaderParameters<Scene> parameter) {
		for (int i = 0; i < scene.gameObjects.size; i++)
			scene.gameObjects.get(i).finishedLoading();
		return scene;
	}
}
