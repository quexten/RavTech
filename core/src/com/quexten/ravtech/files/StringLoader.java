
package com.quexten.ravtech.files;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

public class StringLoader extends AsynchronousAssetLoader<String, AssetLoaderParameters<String>> {

	public StringLoader (FileHandleResolver resolver) {
		super(resolver);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Array<AssetDescriptor> getDependencies (String fileName, FileHandle file, AssetLoaderParameters<String> parameter) {
		return new Array<AssetDescriptor>();
	}

	@Override
	public void loadAsync (AssetManager manager, String fileName, FileHandle file, AssetLoaderParameters<String> parameter) {
	}

	@Override
	public String loadSync (AssetManager manager, String fileName, FileHandle file, AssetLoaderParameters<String> parameter) {
		return file.readString();
	}
}
