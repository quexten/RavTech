
package com.quexten.ravtech.dk.packaging;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.quexten.ravtech.dk.ui.packaging.BuildReporterDialog;

public class CopyDirectoryStep extends PackageStep {

	File dstDir;
	File srcDir;

	public CopyDirectoryStep (BuildReporterDialog buildReporterDialog,
		File srcDir, File dstDir) {
		super(buildReporterDialog);
		this.srcDir = srcDir;
		this.dstDir = dstDir;
	}

	@Override
	public void run () {
		buildReporterDialog.log("Copy from [" + srcDir.getAbsolutePath()
			+ "] to [" + dstDir.getAbsolutePath() + "]");
		try {
			FileUtils.copyDirectory(srcDir, dstDir);
		} catch (IOException e) {
			buildReporterDialog.logError(e.getMessage());
		}
		buildReporterDialog.log("Copy Done!");
		executeNext();
	}
}
