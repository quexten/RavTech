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

package com.ravelsoftware.ravtech.files.zip;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.StreamUtils;

public class ArchiveFileHandle extends FileHandle {

	FileHandle zipfilehandle;
	InputStream entrystream;
	String filePath;

	public ArchiveFileHandle (FileHandle zipfilehandle, String filePath, boolean init) {
		super(filePath, FileType.Classpath);
		this.zipfilehandle = zipfilehandle;
		if (filePath.startsWith("/")) filePath = filePath.substring(1);
		if (init) {
			ZipInputStream stream = new ZipInputStream(zipfilehandle.read());
			try {
				ZipEntry entry;
				while ((entry = stream.getNextEntry()) != null) {
					if (entry.getName().replace('\\', '/').equals(filePath)) {
						entrystream = stream;
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.filePath = filePath;
	}

	public ArchiveFileHandle (FileHandle zipfilehandle, String filePath) {
		this(zipfilehandle, filePath, true);
	}

	@Override
	public InputStream read () {
		return entrystream;
	}

	@Override
	public FileHandle child (String name) {
		return new ArchiveFileHandle(zipfilehandle, filePath + name, false);
	}

	@Override
	public FileHandle sibling (String name) {
		return null;
	}

	@Override
	public FileHandle parent () {
		filePath = filePath.replace('\\', '/');
		return new ArchiveFileHandle(zipfilehandle, filePath.substring(0, filePath.lastIndexOf('/') + 1), false);
	}

	@Override
	public byte[] readBytes () {
		InputStream input = read();
		try {
			return StreamUtils.copyStreamToByteArray(input, 512);
		} catch (IOException ex) {
			throw new GdxRuntimeException("Error reading file: " + this, ex);
		} finally {
			StreamUtils.closeQuietly(input);
		}
	}

	@Override
	public String toString () {
		return filePath.replace('\\', '/');
	}

	@Override
	public boolean exists () {
		return entrystream != null;
	}

	@Override
	public long length () {
		return 512;
	}
}
