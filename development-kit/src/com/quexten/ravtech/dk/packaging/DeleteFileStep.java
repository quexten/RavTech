
package com.quexten.ravtech.dk.packaging;

import com.badlogic.gdx.files.FileHandle;
import com.quexten.ravtech.dk.ui.packaging.BuildReporterDialog;

public class DeleteFileStep extends PackageStep {

	FileHandle file;

	public DeleteFileStep (BuildReporterDialog buildReporterDialog, FileHandle file) {
		super(buildReporterDialog);
		this.file = file;
	}

	@Override
	public void run () {
		buildReporterDialog.log("Deleting File " + file.path());
		file.delete();
		buildReporterDialog.log("Deleting File " + file.path());
		executeNext();
	}

}
