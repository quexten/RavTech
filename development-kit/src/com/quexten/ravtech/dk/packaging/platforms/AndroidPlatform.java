
package com.quexten.ravtech.dk.packaging.platforms;

import com.badlogic.gdx.files.FileHandle;
import com.quexten.ravtech.dk.ui.packaging.BuildReporterDialog;

public class AndroidPlatform implements Platform {

	public String deviceIdentifier = "";
	private boolean skipInstall;

	public AndroidPlatform () {
	}

	public AndroidPlatform (String deviceIdentifier) {
		this.deviceIdentifier = deviceIdentifier;
	}

	public AndroidPlatform (boolean skipInstall) {
		this();
		this.skipInstall = skipInstall;
	}

	public AndroidPlatform (String deviceIdentifier,
		boolean skipInstall) {
		this(deviceIdentifier);
		this.skipInstall = skipInstall;
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
				(this.skipInstall ? "" : "android:installDebug ") + "android:run --stacktrace");
		else
			GradleInvoker.Invoke(buildReporterDialog,
				(this.skipInstall ? "" : "android:installDebug ") + "android:run -Pargs="
					+ deviceIdentifier + " --stacktrace");
		return false;
	}
}
