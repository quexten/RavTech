
package com.ravelsoftware.ravtech.dk.packaging;

import java.io.File;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.dk.RavTechDK;
import com.ravelsoftware.ravtech.dk.packaging.platforms.AndroidPlatform;
import com.ravelsoftware.ravtech.dk.packaging.platforms.BuildOptions;
import com.ravelsoftware.ravtech.dk.packaging.platforms.DesktopPlatform;
import com.ravelsoftware.ravtech.dk.packaging.platforms.WebGLPlatform;
import com.ravelsoftware.ravtech.dk.packaging.platforms.BuildOptions.AssetType;
import com.ravelsoftware.ravtech.dk.packaging.platforms.android.AlignStep;
import com.ravelsoftware.ravtech.dk.packaging.platforms.android.KeyStoreCredentials;
import com.ravelsoftware.ravtech.dk.packaging.platforms.android.SignStep;
import com.ravelsoftware.ravtech.dk.ui.packaging.BuildReporterDialog;

public class Packager {

	public enum TargetPlatform {
		Desktop, Windows, Mac, Linux, Android, iOS, WebGL
	}

	/** Queues up the required steps to package the application for the required target and executes them in a separate GUIWorker
	 * thread so that it doesn't lock the editors GUI.
	 *
	 * @param buildReporterDialog - the dialog to log the progress in
	 * @param targetPlatform - the platform the app is packaged for (E.g Android / Dekstop / iOS)
	 * @param userData
	 * @param destinationDir - the destination, meaning the path the package is saved at */
	public static void dist (BuildReporterDialog buildReporterDialog,
		TargetPlatform targetPlatform, Object userData,
		FileHandle destinationDir, BuildOptions options) {
		RavTechDK.getLocalFile("builder/android/assets/config.json")
			.writeString("{ \"title\": \"" + RavTech.project.appName
				+ "\",\n\"useAssetBundle\": "
				+ String.valueOf(
					options.assetType == BuildOptions.AssetType.External)
				+ "\n}", false);
		RavTechDK.saveScene(
			RavTech.files.getAssetHandle(RavTechDK.getCurrentScene()));
		RavTechDK.project.save(RavTechDK.projectHandle.child("assets"));

		PackageStep firstStep;
		if (options.assetType == AssetType.External)
			firstStep = new PackBundleStep(buildReporterDialog);
		else {
			firstStep = new CopyDirectoryStep(buildReporterDialog,
				RavTechDK.projectHandle.child("assets").file(), RavTechDK
					.getLocalFile("/builder/android/assets/").file());
		}

		PackageStep localFirstStep = firstStep;

		// Builds the packaging chain
		switch (targetPlatform) {
			case Android:
				if (options.assetType == AssetType.External)
					localFirstStep = localFirstStep
						.setNextStep(new CopyStep(buildReporterDialog,
							Gdx.files.absolute(System.getProperty("user.dir")
								+ "/temp/build.ravpack"),
							destinationDir.child("extension.obb")));

				localFirstStep
					.setNextStep(
						new ApkPreparationStep(buildReporterDialog))
					.setNextStep(new PlatformStep(buildReporterDialog,
						new AndroidPlatform(),
						destinationDir.child("build.apk")))
					.setNextStep(new SignStep(buildReporterDialog,
						(KeyStoreCredentials)userData))
					.setNextStep(new AlignStep(buildReporterDialog))
					.setNextStep(new CopyStep(buildReporterDialog,
						Gdx.files.absolute(System.getProperty("user.dir")
							+ "/builder/android/build/outputs/apk/android-release-aligned.apk"),
						destinationDir.child("build.apk")))
					.setNextStep(new DeleteFileStep(buildReporterDialog,
						RavTechDK.projectHandle.child("assets")
							.child("project.json")));
				break;
			case Desktop:
				if (options.assetType == AssetType.External)
					localFirstStep = localFirstStep
						.setNextStep(new CopyStep(buildReporterDialog,
							Gdx.files.absolute(System.getProperty("user.dir")
								+ "/temp/build.ravpack"),
							destinationDir.child("assets.ravpack")));

				localFirstStep
					.setNextStep(new PlatformStep(buildReporterDialog,
						new DesktopPlatform(),
						destinationDir.child("build.jar")))
					.setNextStep(new CopyStep(buildReporterDialog,
						Gdx.files.absolute(System.getProperty("user.dir")
							+ "/builder/desktop/build/libs/desktop-1.0.jar"),
						destinationDir.child("build.jar")))
					.setNextStep(new DeleteFileStep(buildReporterDialog,
						RavTechDK.projectHandle.child("assets")
							.child("project.json")));
				break;
			case Linux:
				break;
			case Mac:
				break;
			case WebGL:
				break;
			case Windows:
				break;
			case iOS:
				break;
			default:
				break;
		}
		// Executes the queue in a separate thread
		new PackageWorker(firstStep).run();
	}

	/** Queues up the required steps to run the application for the required target and executes them in a separate GUIWorker
	 * thread so that it doesn't lock the editors GUI.
	 *
	 * @param buildReporterDialog - the dialog to log the progress in
	 * @param platform - the platform to run the app on
	 * @param deviceIdentifier - the device identifier in case there are multiple devices. Null if there is only 1 device */
	public static void run (BuildReporterDialog buildReporterDialog,
		TargetPlatform platform, String deviceIdentifier) {
		PackageStep firstStep = null;
		RavTechDK.project.save(RavTechDK.projectHandle.child("assets"));
		switch (platform) {
			case Desktop:
			case Windows:
			case Mac:
			case Linux:
				RavTechDK.saveScene(RavTech.files
					.getAssetHandle(RavTechDK.getCurrentScene()));
				firstStep = new PackBundleStep(buildReporterDialog);
				firstStep
					.setNextStep(new PackBundleStep(buildReporterDialog))
					.setNextStep(new CopyStep(buildReporterDialog,
						Gdx.files.absolute(System.getProperty("user.dir")
							+ "/temp/build.ravpack"),
						Gdx.files.absolute(System.getProperty("user.dir")
							+ "/builder/android/assets/resourcepack.ravpack")))
					.setNextStep(new PlatformStep(buildReporterDialog,
						new DesktopPlatform()));
				break;
			case Android:
				RavTechDK.saveScene(RavTech.files
					.getAssetHandle(RavTechDK.getCurrentScene()));
				firstStep = new PackBundleStep(buildReporterDialog);
				firstStep
					.setNextStep(new PackBundleStep(buildReporterDialog))
					.setNextStep(
						new ApkPreparationStep(buildReporterDialog))
					.setNextStep(new AndroidPushStep(buildReporterDialog,
						System.getProperty("user.dir")
							+ "/temp/build.ravpack ",
						"/sdcard/Android/obb/" + RavTechDK.project.appId
							+ "/main." + RavTech.project.buildVersion + "."
							+ RavTech.project.appId + ".obb"))
					.setNextStep(new PlatformStep(buildReporterDialog,
						new AndroidPlatform(deviceIdentifier)));
				break;
			case WebGL:
				RavTechDK.saveScene(RavTech.files
					.getAssetHandle(RavTechDK.getCurrentScene()));
				firstStep = new CopyDirectoryStep(buildReporterDialog,
					RavTechDK.projectHandle.child("assets").file(),
					new File(System.getProperty("user.dir")
						+ "/builder/android/assets/"));
				firstStep
					.setNextStep(new CopyDirectoryStep(buildReporterDialog,
						RavTechDK.projectHandle.child("assets").file(),
						new File(System.getProperty("user.dir")
							+ "/builder/html/war/assets/")))
					.setNextStep(new PlatformStep(buildReporterDialog,
						new WebGLPlatform()));
				break;
			default:
				break;
		}
		firstStep.run();
		RavTechDK.projectHandle.child("assets").child("project.json")
			.delete();
	}
}
