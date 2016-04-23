
package com.ravelsoftware.ravtech.dk.packaging;

import com.badlogic.gdx.files.FileHandle;
import com.ravelsoftware.ravtech.dk.packaging.platforms.Platform;
import com.ravelsoftware.ravtech.dk.ui.packaging.BuildReporterDialog;
import com.ravelsoftware.ravtech.dk.ui.packaging.PrinterListener;

public class PlatformStep extends PackageStep {

	FileHandle directory;
	Platform platform;
	boolean run;

	public PlatformStep (BuildReporterDialog buildReporterDialog,
		Platform platform) {
		super(buildReporterDialog);
		this.platform = platform;
		run = true;
	}

	public PlatformStep (BuildReporterDialog buildReporterDialog,
		Platform platform, FileHandle destinationDir) {
		this(buildReporterDialog, platform);
		run = false;
		directory = destinationDir;
	}

	@Override
	public void run () {
		buildReporterDialog.printerListeners.add(new PrinterListener() {
			@Override
			public void onPrint (String line) {
				if (line.equals("BUILD SUCCESSFUL"))
					PlatformStep.this.executeNext();
			}
		});
		if (run)
			platform.run(buildReporterDialog);
		else
			platform.build(directory, buildReporterDialog);
	}
}
