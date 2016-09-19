
package com.quexten.ravtech.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StreamUtils;

public class ZipUtil {

	private static final int BUFFER_SIZE = 4096;

	private String outputZipFile = "";
	private String sourceFolder = "";
	private List<String> fileList = new ArrayList<String>();

	public ZipUtil () {

	}

	public void zipFolder (String inputPath, String outputPath, Array<String> paths) {
		outputZipFile = outputPath;
		sourceFolder = inputPath;
		genFileListForFolderContents(new File(sourceFolder));

		byte[] buffer = new byte[1024];
		FileOutputStream fos = null;
		ZipOutputStream zos = null;

		try {
			try {
				sourceFolder.substring(sourceFolder.lastIndexOf("\\") + 1, sourceFolder.length());
			} catch (Exception e) {
			}
			fos = new FileOutputStream(new File(outputZipFile));
			zos = new ZipOutputStream(fos);
			FileInputStream in = null;
			for (String file : fileList) {
				String path = file.replace(inputPath, "").replace('\\', '/');
				Debug.log("Path", path);
				if (!paths.contains(path, false))
					continue;
				Debug.log("file", file);
				
				ZipEntry ze = new ZipEntry(file);
				zos.putNextEntry(ze);
				try {
					in = new FileInputStream(sourceFolder + "/" + file);
					int len;
					while ((len = in.read(buffer)) > 0)
						zos.write(buffer, 0, len);
				} finally {
					in.close();
				}
			}
			zos.closeEntry();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				zos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void zipFolder (String inputPath, String outputPath) {
		zipFolder(inputPath, outputPath, null);
	}

	void genFileListForFolderContents (File node) {
		File[] nodes = node.listFiles();
		for (File file : nodes)
			generateFileList(file);
	}

	void generateFileList (File node) {
		if (node.isFile())
			fileList.add(generateZipEntry(node.toString()));
		if (node.isDirectory()) {
			String[] subNote = node.list();
			for (String filename : subNote)
				generateFileList(new File(node, filename));
		}
	}

	private String generateZipEntry (String file) {
		return file.substring(sourceFolder.length() + 1, file.length());
	}

	private static String dirpart (String name) {
		int s = name.lastIndexOf(File.separatorChar);
		return s == -1 ? null : name.substring(0, s);
	}

	/*** Extract zipfile to outdir with complete directory structure
	 * 
	 * @param zipfile Input .zip file
	 * @param outdir Output directory */
	public static void extract (FileHandle zipfile, FileHandle outdir) {
		System.out.println("zipfile: " + zipfile);
		System.out.println("outdir: " + outdir);
		try {
			ZipInputStream zin = new ZipInputStream(zipfile.read());
			ZipEntry entry;
			String name, dir;
			while ((entry = zin.getNextEntry()) != null) {
				name = entry.getName();
				if (entry.isDirectory()) {
					outdir.child(name).mkdirs();
					continue;
				}
				/* this part is necessary because file entry can come before directory entry where is file located i.e.: /foo/foo.txt
				 * /foo/ */
				dir = dirpart(name);
				if (dir != null)
					outdir.child(dir).mkdirs();
				extractFile(zin, outdir, name);
			}
			zin.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void extractFile (ZipInputStream in, FileHandle outdir, String name) throws IOException {
		Debug.log("Extract", outdir.path() + "\\" + name);
		byte[] buffer = new byte[BUFFER_SIZE];
		outdir.child(name).delete();

		StreamUtils.copyStream(in, outdir.child(name).write(true));
	}

	private static void mkdirs (File outdir, String path) {
		File d = new File(outdir, path);
		if (!d.exists())
			d.mkdirs();
	}

}
