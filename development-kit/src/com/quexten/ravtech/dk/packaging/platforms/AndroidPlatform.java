
package com.quexten.ravtech.dk.packaging.platforms;

import com.badlogic.gdx.Gdx;
import com.quexten.ravtech.dk.RavTechDK;
import com.quexten.ravtech.dk.adb.AdbManager;
import com.quexten.ravtech.dk.packaging.AndroidPushStep;
import com.quexten.ravtech.dk.packaging.ApkPreparationStep;
import com.quexten.ravtech.dk.packaging.BuildPlatformStep;
import com.quexten.ravtech.dk.packaging.CopyStep;
import com.quexten.ravtech.dk.packaging.PackageStep;
import com.quexten.ravtech.dk.packaging.platforms.BuildOptions.AssetType;
import com.quexten.ravtech.dk.packaging.platforms.android.AlignStep;
import com.quexten.ravtech.dk.packaging.platforms.android.SignStep;
import com.quexten.ravtech.dk.ui.packaging.BuildReporterDialog;

public class AndroidPlatform extends Platform {

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

	@Override
	public BuildOptions getOptions () {
		return new BuildOptions(AssetType.Internal, false);
	}

	@Override
	public PackageStep addBuildEngineStep (BuildReporterDialog dialog,
		PackageStep currentStep, BuildOptions options) {
		PackageStep tempStep = null;

		tempStep = currentStep
			.setNextStep(new ApkPreparationStep(dialog))
			.setNextStep(new BuildPlatformStep(dialog,
				new AndroidPlatform(), options))
			.setNextStep(new AlignStep(dialog));

		if (options.sign)
			tempStep = tempStep
				.setNextStep(new SignStep(dialog, options.credentials));

		final String releaseFile = "android-release-aligned";
		final String debugFile = "android-debug";

		return tempStep.setNextStep(new CopyStep(dialog,
			Gdx.files.absolute(System.getProperty("user.dir")
				+ "/builder/android/build/outputs/apk/"
				+ (options.sign ? releaseFile : debugFile) + ".apk"),
			RavTechDK.getLocalFile("builds/android")
				.child("build.apk")));
	}
}
