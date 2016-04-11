
package com.ravelsoftware.ravtech.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;

public class ResourceFileHandleResolver implements FileHandleResolver {

	@Override
	public FileHandle resolve (String fileName) {
		fileName = fileName.replace('\\', '/');
		if(fileName.startsWith("resources/")) {
			fileName = fileName.substring("resources/".length());
		}
		return Gdx.files.local("resources").child(fileName);
	}

}
