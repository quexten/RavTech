package com.quexten.ravtech.dk.packaging;

import com.quexten.ravtech.dk.packaging.platforms.BuildOptions;
import com.quexten.ravtech.dk.packaging.platforms.Platform;
import com.quexten.ravtech.dk.ui.packaging.BuildReporterDialog;

public class RunPlatformStep extends PackageStep {

	Platform platform;
	BuildOptions options;
	
	public RunPlatformStep (BuildReporterDialog buildReporterDialog,
		Platform platform, BuildOptions options) {
		super(buildReporterDialog);
		this.platform = platform;
		this.options = options;
	}

	@Override
	public void run () {
		platform.run(buildReporterDialog, options);
	}
}
