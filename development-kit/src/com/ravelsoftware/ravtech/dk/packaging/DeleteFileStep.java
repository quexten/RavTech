
package com.ravelsoftware.ravtech.dk.packaging;

import com.badlogic.gdx.files.FileHandle;
import com.ravelsoftware.ravtech.dk.ui.packaging.BuildReporterDialog;

public class DeleteFileStep extends PackageStep {

	FileHandle file;

	public DeleteFileStep (BuildReporterDialog buildReporterDialog, FileHandle file) {
		super(buildReporterDialog);
		this.file = file;
	}

	@Override
	public void run () {
		this.buildReporterDialog.log("Deleting File " + file.path());
		file.delete();
		this.buildReporterDialog.log("Deleting File " + file.path());
		this.executeNext();
	}

}
