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
package com.ravelsoftware.ravtech.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.ravelsoftware.ravtech.dk.ui.packaging.BuildWizard.BuildReporterDialog;

public class Zipper {

    private static final int BUFFER_SIZE = 4096;
    private static String outputZipFile = "";
    private static String sourceFolder = "";
    BuildReporterDialog dialog;
    private List<String> fileList;

    public Zipper(BuildReporterDialog dialog) {
        fileList = new ArrayList<String>();
        this.dialog = dialog;
    }

    void generateFileList (File node) {
        if (node.isFile()) fileList.add(generateZipEntry(node.toString()));
        if (node.isDirectory()) {
            String[] subNote = node.list();
            for (String filename : subNote)
                generateFileList(new File(node, filename));
        }
    }

    private String generateZipEntry (String file) {
        return file.substring(sourceFolder.length() + 1, file.length());
    }

    void genFileListForFolderContents (File node) {
        File[] nodes = node.listFiles();
        for (File file : nodes)
            generateFileList(file);
    }

    public void zipFolder (String SourceFolderPath, String OutputFilePath) {
        outputZipFile = OutputFilePath;
        sourceFolder = SourceFolderPath;
        genFileListForFolderContents(new File(sourceFolder));
        zipIt(outputZipFile);
    }

    void zipIt (String zipFile) {
        byte[] buffer = new byte[1024];
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        try {
            try {
                sourceFolder.substring(sourceFolder.lastIndexOf("\\") + 1, sourceFolder.length());
            } catch (Exception e) {
            }
            fos = new FileOutputStream(zipFile);
            zos = new ZipOutputStream(fos);
            dialog.log("Zipping Resource Bundle");
            FileInputStream in = null;
            for (String file : this.fileList) {
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
        dialog.log("Finished Zipping Resource Bundle");
    }

    private static void extractFile (ZipInputStream in, File outdir, String name) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(outdir, name)));
        int count = -1;
        while ((count = in.read(buffer)) != -1)
            out.write(buffer, 0, count);
        out.close();
    }

    private static void mkdirs (File outdir, String path) {
        File d = new File(outdir, path);
        if (!d.exists()) d.mkdirs();
    }

    private static String dirpart (String name) {
        int s = name.lastIndexOf(File.separatorChar);
        return s == -1 ? null : name.substring(0, s);
    }

    /*** Extract zipfile to outdir with complete directory structure
     *
     * @param zipfile Input .zip file
     * @param outdir Output directory */
    public static void extract (File zipfile, File outdir) {
        System.out.println("zipfile: " + zipfile);
        System.out.println("outdir: " + outdir);
        try {
            ZipInputStream zin = new ZipInputStream(new FileInputStream(zipfile));
            ZipEntry entry;
            String name, dir;
            while ((entry = zin.getNextEntry()) != null) {
                name = entry.getName();
                if (entry.isDirectory()) {
                    mkdirs(outdir, name);
                    continue;
                }
                /*
                 * this part is necessary because file entry can come before directory entry where is file located i.e.:
                 * /foo/foo.txt /foo/
                 */
                dir = dirpart(name);
                if (dir != null) mkdirs(outdir, dir);
                extractFile(zin, outdir, name);
            }
            zin.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
