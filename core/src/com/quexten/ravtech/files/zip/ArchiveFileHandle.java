
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

	public ArchiveFileHandle (FileHandle zipfilehandle,
		String filePath, boolean init) {
		super(filePath, FileType.Classpath);
		this.zipfilehandle = zipfilehandle;
		if (filePath.startsWith("/"))
			filePath = filePath.substring(1);
		if (init) {
			ZipInputStream stream = new ZipInputStream(
				zipfilehandle.read());
			try {
				ZipEntry entry;
				while ((entry = stream.getNextEntry()) != null)
					if (entry.getName().replace('\\', '/')
						.equals(filePath)) {
						entrystream = stream;
						break;
					}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.filePath = filePath;
	}

	public ArchiveFileHandle (FileHandle zipfilehandle,
		String filePath) {
		this(zipfilehandle, filePath, true);
	}

	@Override
	public InputStream read () {
		return entrystream;
	}

	@Override
	public FileHandle child (String name) {
		return new ArchiveFileHandle(zipfilehandle, filePath + name,
			false);
	}

	@Override
	public FileHandle sibling (String name) {
		return null;
	}

	@Override
	public FileHandle parent () {
		filePath = filePath.replace('\\', '/');
		return new ArchiveFileHandle(zipfilehandle,
			filePath.substring(0, filePath.lastIndexOf('/') + 1), false);
	}

	@Override
	public byte[] readBytes () {
		InputStream input = read();
		try {
			return StreamUtils.copyStreamToByteArray(input, 512);
		} catch (IOException ex) {
			throw new GdxRuntimeException("Error reading file: " + this,
				ex);
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
