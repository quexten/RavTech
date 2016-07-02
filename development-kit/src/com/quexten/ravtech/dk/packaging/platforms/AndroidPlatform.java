
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

public class AndroidPlatform extends Platform<AndroidBuildOptions, AndroidBuildOptionsTable> {

	@Override
	public void build (BuildReporterDialog buildReporterDialog, AndroidBuildOptions options) {
		GradleInvoker.Invoke(buildReporterDialog, "assemble" + (options.sign ? "Release" : "Debug") + " --stacktrace");
		buildReporterDialog.setVisible(true);
	}

	@Override
	public void run (BuildReporterDialog buildReporterDialog, AndroidBuildOptions options) {
		if (!options.skipBuild)
			AdbManager.installBuild(options.deviceId);
		if (options.isExternal())
			new AndroidPushStep(buildReporterDialog, RavTechDK.getLocalFile("/temp/build.ravpack").path(), "/sdcard/Android/obb/"
				+ RavTechDK.project.appId + "/main." + RavTechDK.project.buildVersion + "." + RavTechDK.project.appId + ".obb").run();

		AdbManager.launchBuild(options.deviceId);
	}

	@Override
	public AndroidBuildOptions getOptions () {
		return new AndroidBuildOptions(AssetType.Internal);
	}

	@Override
	public PackageStep addBuildEngineStep (BuildReporterDialog dialog, PackageStep currentStep, AndroidBuildOptions options) {
		PackageStep tempStep = null;
		
		BuildPlatformStep buildPlatformStep = new BuildPlatformStep(dialog, new AndroidPlatform(), options);
		if(options.sign)
			buildPlatformStep.setNextStep(new AlignStep(dialog));
		tempStep = currentStep.setNextStep(new ApkPreparationStep(dialog))
			.setNextStep(buildPlatformStep);

		if (options.sign)
			tempStep = tempStep.setNextStep(new SignStep(dialog, options.credentials));

		final String releaseFile = "android-release-aligned";
		final String debugFile = "android-debug";

		return tempStep
			.setNextStep(new CopyStep(dialog,
				Gdx.files.absolute(System.getProperty("user.dir") + "/builder/android/build/outputs/apk/"
					+ (options.sign ? releaseFile : debugFile) + ".apk"),
			RavTechDK.getLocalFile("builds/android").child("build.apk")));
	}

	@Override
	public AndroidBuildOptionsTable getOptionsTable (AndroidBuildOptions options) {
		return new AndroidBuildOptionsTable(options);
	}

	@Override
	public void applyOptions (AndroidBuildOptionsTable optionsTable, AndroidBuildOptions options) {
		options.sign = optionsTable.signBox.isChecked();
	}

}
