
package com.ravelsoftware.ravtech.dk.packaging.platforms;

import com.badlogic.gdx.files.FileHandle;
import com.ravelsoftware.ravtech.dk.ui.packaging.BuildReporterDialog;

public class AndroidPlatform implements Platform {

	public String deviceIdentifier = "";

	public AndroidPlatform () {
	}

	public AndroidPlatform (String deviceIdentifier) {
		this.deviceIdentifier = deviceIdentifier;
	}

	@Override
	public boolean build (FileHandle buildPath,
		BuildReporterDialog buildReporterDialog) {
		GradleInvoker.Invoke(buildReporterDialog,
			"assembleRelease --stacktrace");
		buildReporterDialog.setVisible(true);
		return false;
	}

	@Override
	public boolean run (BuildReporterDialog buildReporterDialog) {
		if (deviceIdentifier.length() == 0)
			GradleInvoker.Invoke(buildReporterDialog,
				"android:installDebug android:run --stacktrace");
		else
			GradleInvoker.Invoke(buildReporterDialog,
				"android:installDebug android:run -Pargs="
					+ deviceIdentifier + " --stacktrace");
		return false;
	}
}
