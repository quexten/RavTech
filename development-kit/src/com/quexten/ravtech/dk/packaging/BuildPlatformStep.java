
package com.quexten.ravtech.dk.packaging;

import com.quexten.ravtech.dk.packaging.platforms.BuildOptions;
import com.quexten.ravtech.dk.packaging.platforms.Platform;
import com.quexten.ravtech.dk.ui.packaging.BuildReporterDialog;
import com.quexten.ravtech.dk.ui.packaging.PrinterListener;

public class BuildPlatformStep extends PackageStep {

	Platform platform;
	BuildOptions options;

	public BuildPlatformStep (BuildReporterDialog buildReporterDialog,
		Platform platform, BuildOptions options) {
		super(buildReporterDialog);
		this.platform = platform;
		this.options = options;
	}

	@Override
	public void run () {
		buildReporterDialog.printerListeners.add(new PrinterListener() {
			@Override
			public void onPrint (String line) {
				if (line.equals("BUILD SUCCESSFUL"))
					BuildPlatformStep.this.executeNext();
			}
		});
		platform.build(buildReporterDialog, options);
	}
}
