
package com.quexten.ravtech.dk.packaging;

import com.badlogic.gdx.files.FileHandle;
import com.quexten.ravtech.dk.ui.packaging.BuildReporterDialog;

public class CreateFileStep extends PackageStep {

	byte[] data;
	FileHandle fileHandle;

	FileHandle destinationFile;

	public CreateFileStep (BuildReporterDialog buildReporterDialog,
		FileHandle file, byte[] data) {
		super(buildReporterDialog);
		this.destinationFile = file;
		this.data = data;
	}

	public CreateFileStep (BuildReporterDialog buildReporterDialog,
		FileHandle file, FileHandle fileHandle) {
		super(buildReporterDialog);
		this.destinationFile = file;
		this.fileHandle = fileHandle;
	}

	@Override
	public void run () {
		buildReporterDialog.log("Creating File " + destinationFile);

		if (fileHandle == null)
			destinationFile.writeBytes(data, false);
		else
			destinationFile.write(fileHandle.read(), false);

		buildReporterDialog
			.log("Created File " + destinationFile.path());
		executeNext();
	}

}
