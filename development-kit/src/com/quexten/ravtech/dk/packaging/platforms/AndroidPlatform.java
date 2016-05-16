
package com.quexten.ravtech.dk.packaging.platforms;

import com.quexten.ravtech.dk.RavTechDK;
import com.quexten.ravtech.dk.adb.AdbManager;
import com.quexten.ravtech.dk.packaging.AndroidPushStep;
import com.quexten.ravtech.dk.ui.packaging.BuildReporterDialog;

public class AndroidPlatform implements Platform {

	@Override
	public void build (BuildReporterDialog buildReporterDialog,
		BuildOptions options) {
		GradleInvoker.Invoke(buildReporterDialog, "assemble"
			+ (options.sign ? "Release" : "Debug") + " --stacktrace");
		buildReporterDialog.setVisible(true);
	}

	@Override
	public void run (BuildReporterDialog buildReporterDialog,
		BuildOptions options) {
		if (!options.skipBuild)
			AdbManager.installBuild(options.deviceId);
		if (options.isExternal())
			new AndroidPushStep(buildReporterDialog,
				RavTechDK.getLocalFile("/temp/build.ravpack").path(),
				"/sdcard/Android/obb/" + RavTechDK.project.appId
					+ "/main." + RavTechDK.project.buildVersion + "."
					+ RavTechDK.project.appId + ".obb").run();

		AdbManager.launchBuild(options.deviceId);
	}
}
