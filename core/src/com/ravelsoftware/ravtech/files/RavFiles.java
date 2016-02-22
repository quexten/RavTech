package com.ravelsoftware.ravtech.files;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.Scene;
import com.ravelsoftware.ravtech.project.Project;

public class RavFiles {

    AssetManager assetManager;

    public RavFiles(FileHandleResolver assetResolver) {
        assetManager = new AssetManager(assetResolver);
        assetManager.setLoader(Scene.class, new SceneLoader(assetResolver));
        assetManager.setLoader(Project.class, new ProjectLoader(assetResolver));
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
        getAssetManager().clear();
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
        loadAsset("loadState", Scene.class);
        finishLoading();
        RavTech.currentScene = getAsset("loadState");
        getAssetManager().setLoader(Scene.class, new SceneLoader(getResolver()));
    }
}
