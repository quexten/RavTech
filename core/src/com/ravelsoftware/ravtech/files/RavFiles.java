
package com.ravelsoftware.ravtech.files;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.Scene;
import com.ravelsoftware.ravtech.components.GameComponent;
import com.ravelsoftware.ravtech.project.Project;
import com.ravelsoftware.ravtech.util.Debug;

public class RavFiles {

	AssetManager assetManager;
	ObjectMap<String, Array<GameComponent>> componentDependencies = new ObjectMap<String, Array<GameComponent>>();

	public RavFiles (FileHandleResolver assetResolver) {
		assetManager = new AssetManager(assetResolver);
		assetManager.setLoader(Scene.class, new SceneLoader(assetResolver));
		assetManager.setLoader(Project.class, new ProjectLoader(assetResolver));
		assetManager.setLoader(String.class, new StringLoader(assetResolver));
		assetManager.setLoader(BitmapFont.class, new BitmapFontLoader(assetManager.getFileHandleResolver()));
	}

	/** @return - The AssetManager */
	public AssetManager getAssetManager () {
		return assetManager;
	}

	/** Gets the filehandle resolver
	 * @return The resolver */
	public FileHandleResolver getResolver () {
		return assetManager.getFileHandleResolver();
	}

	/** Gets filehandle of the specified asset
	 * @param path - the path to the asset
	 * @return The filehandle of the asset */
	public FileHandle getAssetHandle (String path) {
		return getResolver().resolve(path);
	}

	/** Starts loading the file
	 * @param path - the path to the asset
	 * @param assetType - the type of the asset
	 * @param parameters - parameters to pass the loader */
	public <T> void loadAsset (String path, Class<T> assetType, AssetLoaderParameters<T> parameters) {
		assetManager.load(path, assetType, parameters);
	}

	/** Starts loading the file
	 * @param path - the path to the asset
	 * @param assetType - the type of the asset
	 * @param finish - whether to wait for the asset to finish loading */
	public <T> void loadAsset (String path, Class<T> assetType, boolean finish) {
		assetManager.load(path, assetType);
		if (finish) assetManager.finishLoadingAsset(path);
	}

	/** Starts loading the file
	 * @param path - the path to the asset
	 * @param assetType - the type of the asset */
	public <T> void loadAsset (String path, Class<T> assetType) {
		loadAsset(path, assetType, false);
	}

	/** Starts loading all assets specified
	 * @param dependencies - the assets to load */
	@SuppressWarnings("rawtypes")
	public void loadAssets (Array<AssetDescriptor> dependencies) {
		for (AssetDescriptor descriptor : dependencies)
			assetManager.load(descriptor);
	}

	/** Reloads the specified asset
	 * @param fileName - the file name of the asset */
	public void reloadAsset (String path) {
		Class<?> type = this.getAssetManager().getAssetType(path);
		this.getAssetManager().setReferenceCount(path, 1);
		this.getAssetManager().unload(path);
		this.getAssetManager().load(path, type);
		this.finishLoading();
		Array<GameComponent> dependentComponents = this.getDependentComponents(path);
		for (int i = 0; i < dependentComponents.size; i++)
			dependentComponents.get(i).finishedLoading();
	}

	/** Gets the asset
	 * @param path - the path to the asset
	 * @return The asset */
	public <T> T getAsset (String path) {
		return assetManager.get(path);
	}

	/** Checks whether the asset is Loaded
	 * @param filePath - the path to the asset
	 * @return Whether the asset is Loaded */
	public boolean isLoaded (String filePath) {
		return assetManager.isLoaded(filePath);
	}

	/** Finishes loading all queued up assets */
	public void finishLoading () {
		assetManager.finishLoading();
	}

	/** Serializes currently active Scene
	 * @return The Scene as a String. */
	public String storeState () {
		return new Json().toJson(RavTech.currentScene);
	}

	/** Loads the specified scene as the current Scene
	 * @param sceneString - the Scene to load */
	public void loadState (final String sceneString) {
		String scenePath = getAssetManager().getAssetFileName(RavTech.currentScene);
		RavTech.currentScene.dispose();
		getAssetManager().unload(scenePath);
		getAssetManager().setLoader(Scene.class, new SceneLoader(new FileHandleResolver() {
			@Override
			public FileHandle resolve (String fileName) {
				return new FileHandle() {
					@Override
					public String readString () {
						return sceneString;
					}
				};
			}
		}));
		loadAsset(scenePath, Scene.class);
		finishLoading();
		RavTech.currentScene = getAsset(scenePath);
		getAssetManager().setLoader(Scene.class, new SceneLoader(getResolver()));
	}

	public Array<GameComponent> getDependentComponents (String path) {
		if (!componentDependencies.containsKey(path))
			return new Array<GameComponent>();
		else
			return componentDependencies.get(path);
	}

	public void addDependency (String path, GameComponent component) {
		Debug.logDebug("AddDependency", path);
		if (!componentDependencies.containsKey(path)) componentDependencies.put(path, new Array<GameComponent>());
		componentDependencies.get(path).add(component);
	}

	public void removeDependency (String path, GameComponent component) {
		Debug.logDebug("RemoveDependency", path);
		if (componentDependencies.get(path) == null) return;
		componentDependencies.get(path).removeValue(component, true);
		if (componentDependencies.get(path).size == 0) componentDependencies.remove(path);
	}

}
