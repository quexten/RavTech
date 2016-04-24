
package com.ravelsoftware.ravtech.files;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.ravelsoftware.ravtech.project.Project;

public class ProjectLoader extends
	AsynchronousAssetLoader<Project, AssetLoaderParameters<Project>> {

	Project project;

	public ProjectLoader (FileHandleResolver resolver) {
		super(resolver);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Array<AssetDescriptor> getDependencies (String fileName,
		FileHandle file, AssetLoaderParameters<Project> parameter) {
		project = new Json().fromJson(Project.class, file.readString());
		Array<AssetDescriptor> assetDependencies = new Array<AssetDescriptor>();
		return assetDependencies;
	}

	@Override
	public void loadAsync (AssetManager manager, String fileName,
		FileHandle file, AssetLoaderParameters<Project> parameter) {
	}

	@Override
	public Project loadSync (AssetManager manager, String fileName,
		FileHandle file, AssetLoaderParameters<Project> parameter) {
		return project;
	}
}
