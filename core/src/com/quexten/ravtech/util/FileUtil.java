package com.quexten.ravtech.util;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

public class FileUtil {
	
	public static Array<FileHandle> getChildrenFiles(FileHandle handle) {
		Array<FileHandle> files = new Array<FileHandle>();
		FileHandle[] children = handle.list();
		for(int i = 0; i < children.length; i++)
			if(children[i].isDirectory())
				files.addAll(getChildrenFiles(children[i]));
			else
				files.add(children[i]);
		return files;
	}
	
}
