
package com.ravelsoftware.ravtech.dk.packaging;

import com.badlogic.gdx.files.FileHandle;
import com.ravelsoftware.ravtech.dk.ui.packaging.BuildReporterDialog;

public class CopyStep extends PackageStep {

	FileHandle dstDir;
	FileHandle srcDir;

	public CopyStep (BuildReporterDialog buildReporterDialog,
		FileHandle srcDir, FileHandle dstDir) {
		super(buildReporterDialog);
		this.srcDir = srcDir;
		this.dstDir = dstDir;
	}

	@Override
	public void run () {
		buildReporterDialog.log("Copy from [" + srcDir.path() + "] to ["
			+ dstDir.path() + "]");
		srcDir.copyTo(dstDir);
		buildReporterDialog.log("Copy Done!");
		executeNext();
	}
}
