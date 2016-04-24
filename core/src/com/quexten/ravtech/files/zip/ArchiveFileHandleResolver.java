
package com.quexten.ravtech.files.zip;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;

public class ArchiveFileHandleResolver implements FileHandleResolver {

	FileHandle archiveHandle;

	public ArchiveFileHandleResolver (FileHandle archiveFileHandle) {
		archiveHandle = archiveFileHandle;
	}

	@Override
	public FileHandle resolve (String fileName) {
		return new ArchiveFileHandle(archiveHandle, fileName);
	}
}
