package com.quexten.ravtech.files;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;

/**
 * FallbackFileHandleResolver is a filehandle resolver
 * that wraps a child filehandle and tries to use it.
 * When no file is present in that filehandle
 * the second filehandle is used as a fallback.
 * @author Quexten
 */
public class FallbackFileHandleResolver implements FileHandleResolver {

	private FileHandle handle;
	private FileHandle fallback;

	public FallbackFileHandleResolver (FileHandle handle, FileHandle fallback) {
		this.handle = handle;
		this.fallback = fallback;
	}

	@Override
	public FileHandle resolve (String fileName) {
		return handle.child(fileName).exists() ? handle.child(fileName) : fallback.child(fileName);
	}
	
}
