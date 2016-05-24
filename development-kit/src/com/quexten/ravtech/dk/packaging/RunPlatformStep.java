
package com.quexten.ravtech.dk.packaging;

import com.quexten.ravtech.dk.packaging.platforms.BuildOptions;
import com.quexten.ravtech.dk.packaging.platforms.Platform;
import com.quexten.ravtech.dk.ui.packaging.BuildReporterDialog;

public class RunPlatformStep extends PackageStep {

	@SuppressWarnings("rawtypes") Platform platform;
	BuildOptions options;

	@SuppressWarnings("rawtypes")
	public RunPlatformStep (BuildReporterDialog buildReporterDialog, Platform platform, BuildOptions options) {
		super(buildReporterDialog);
		this.platform = platform;
		this.options = options;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run () {
		platform.run(buildReporterDialog, options);
	}
}
