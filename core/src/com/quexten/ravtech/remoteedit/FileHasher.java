
package com.quexten.ravtech.remoteedit;

import java.io.InputStream;
import java.security.MessageDigest;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.ObjectMap;

public class FileHasher {

	public static ObjectMap<String, String> getHashes (FileHandle path) {		
		return getHashes(path, path);
	}

	public static ObjectMap<String, String> getHashes (FileHandle basePath, FileHandle path) {
		ObjectMap<String, String> objectMap = new ObjectMap<String, String>();
		
		for (FileHandle file : path.list()) {
			if (!file.isDirectory())
				objectMap.put(file.path().replace(basePath.path() + "/", ""), getHash(file));
			else
				objectMap.putAll(getHashes(basePath, file));
		}
		
		return objectMap;
	}
	
	public static byte[] createChecksum (FileHandle file) {
		try {
			InputStream fis = file.read();

			byte[] buffer = new byte[1024];
			MessageDigest complete = MessageDigest.getInstance("MD5");
			int numRead;

			do {
				numRead = fis.read(buffer);
				if (numRead > 0) {
					complete.update(buffer, 0, numRead);
				}
			} while (numRead != -1);

			fis.close();
			return complete.digest();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new byte[0];
	}

	public static String getHash (FileHandle file) {
		byte[] b = createChecksum(file);
		String result = "";

		for (int i = 0; i < b.length; i++) {
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}

}
