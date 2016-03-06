
package com.ravelsoftware.ravtech.dk.packaging.platforms;

import java.io.File;

import com.ravelsoftware.ravtech.dk.ui.packaging.BuildReporterDialog;

public class DesktopPlatform implements Platform {

	@Override
	public boolean build (File buildPath, BuildReporterDialog dialog) {
		GradleInvoker.Invoke(dialog, "desktop:build");
		return true;
	}

	@Override
	public boolean run (BuildReporterDialog dialog) {
		GradleInvoker.Invoke(dialog, "desktop:run");
		return true;
	}
}
