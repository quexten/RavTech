
package com.ravelsoftware.ravtech.dk.packaging;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import com.ravelsoftware.ravtech.dk.ui.packaging.BuildReporterDialog;

public class CopyStep extends PackageStep {

	File dstDir;
	File srcDir;

	public CopyStep (BuildReporterDialog buildReporterDialog, File srcDir, File dstDir) {
		super(buildReporterDialog);
		this.srcDir = srcDir;
		this.dstDir = dstDir;
	}

	@Override
	public void run () {
		buildReporterDialog.log("Copy from [" + srcDir.getAbsolutePath() + "] to [" + dstDir.getAbsolutePath() + "]");
		try {
			Files.copy(srcDir.toPath(), dstDir.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			buildReporterDialog.logError(e.getMessage());
		}
		buildReporterDialog.log("Copy Done!");
		this.executeNext();
	}
}
