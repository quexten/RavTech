
package com.ravelsoftware.ravtech.dk.packaging;

import java.io.File;

import com.ravelsoftware.ravtech.dk.packaging.platforms.Platform;
import com.ravelsoftware.ravtech.dk.ui.packaging.BuildReporterDialog;
import com.ravelsoftware.ravtech.dk.ui.packaging.PrinterListener;

public class PlatformStep extends PackageStep {

	File directory;
	Platform platform;
	boolean run;

	public PlatformStep (BuildReporterDialog buildReporterDialog, Platform platform) {
		super(buildReporterDialog);
		this.platform = platform;
		this.run = true;
	}

	public PlatformStep (BuildReporterDialog buildReporterDialog, Platform platform, File directory) {
		this(buildReporterDialog, platform);
		this.run = false;
		this.directory = directory;
	}

	@Override
	public void run () {
		if (run) { // Wether to do a test run or package the app for release
			buildReporterDialog.printerListeners.add(new PrinterListener() {

				public void onPrint (String line) {
					if (line.equals("BUILD SUCCESSFUL")) PlatformStep.this.executeNext();
				}
			});
			platform.run(this.buildReporterDialog);
		} else
			platform.build(directory, buildReporterDialog);
	}
}
