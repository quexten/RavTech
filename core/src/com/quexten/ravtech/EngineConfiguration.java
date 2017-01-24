
package com.quexten.ravtech;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;

public class EngineConfiguration {

	public String title;
	public FileHandleResolver assetResolver = new InternalFileHandleResolver();
	public boolean remoteEdit;

}
