
package com.quexten.ravtech.dk.packaging;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.quexten.ravtech.dk.ui.packaging.BuildReporterDialog;

public class DeleteDirectoryStep extends PackageStep {

	File dir;

	public DeleteDirectoryStep (BuildReporterDialog buildReporterDialog, File dir) {
		super(buildReporterDialog);
		this.dir = dir;
	}

	@Override
	public void run () {
		buildReporterDialog.log("Delete [" + dir.getAbsolutePath() + "]");
		try {
			FileUtils.deleteDirectory(dir.getAbsoluteFile());
			dir.mkdir();
		} catch (IOException e) {
			buildReporterDialog.logError(e.getMessage());
		}
		buildReporterDialog.log("Delete Done!");
		executeNext();
	}
}
