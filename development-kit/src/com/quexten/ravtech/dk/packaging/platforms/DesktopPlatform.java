
package com.quexten.ravtech.dk.packaging.platforms;

import com.badlogic.gdx.files.FileHandle;
import com.quexten.ravtech.dk.ui.packaging.BuildReporterDialog;

public class DesktopPlatform implements Platform {

	@Override
	public boolean build (FileHandle buildPath,
		BuildReporterDialog dialog) {
		GradleInvoker.Invoke(dialog, "desktop:dist");
		return true;
	}

	@Override
	public boolean run (BuildReporterDialog dialog) {
		GradleInvoker.Invoke(dialog, "desktop:run");
		return true;
	}
}
