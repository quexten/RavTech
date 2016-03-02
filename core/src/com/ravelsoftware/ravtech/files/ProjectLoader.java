/*******************************************************************************
 * Copyright 2014-2016 Bernd Schoolmann
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

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

public class ProjectLoader extends AsynchronousAssetLoader<Project, AssetLoaderParameters<Project>> {

	Project project;

	public ProjectLoader (FileHandleResolver resolver) {
		super(resolver);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Array<AssetDescriptor> getDependencies (String fileName, FileHandle file, AssetLoaderParameters<Project> parameter) {
		project = new Json().fromJson(Project.class, file.readString());
		Array<AssetDescriptor> assetDependencies = new Array<AssetDescriptor>();
		return assetDependencies;
	}

	@Override
	public void loadAsync (AssetManager manager, String fileName, FileHandle file, AssetLoaderParameters<Project> parameter) {
	}

	@Override
	public Project loadSync (AssetManager manager, String fileName, FileHandle file, AssetLoaderParameters<Project> parameter) {
		return project;
	}
}
