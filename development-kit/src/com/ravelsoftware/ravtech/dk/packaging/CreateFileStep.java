
package com.ravelsoftware.ravtech.dk.packaging;

import java.io.File;

import com.badlogic.gdx.Gdx;
import com.ravelsoftware.ravtech.dk.ui.packaging.BuildReporterDialog;

public class CreateFileStep extends PackageStep {

	byte[] data;
	File destinationFile;

	public CreateFileStep (BuildReporterDialog buildReporterDialog, File file, byte[] data) {
		super(buildReporterDialog);
		this.destinationFile = file;
		this.data = data;
	}

	@Override
	public void run () {
		this.buildReporterDialog.log("Creating File " + destinationFile.getAbsolutePath());
		Gdx.files.absolute(destinationFile.getAbsolutePath()).writeBytes(data, false);
		this.buildReporterDialog.log("Created File " + destinationFile.getAbsolutePath());
		this.executeNext();
	}

}
