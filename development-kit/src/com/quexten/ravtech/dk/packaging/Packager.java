
package com.quexten.ravtech.dk.packaging;

import com.badlogic.gdx.Gdx;
import com.quexten.ravtech.dk.RavTechDK;
import com.quexten.ravtech.dk.packaging.platforms.AndroidPlatform;
import com.quexten.ravtech.dk.packaging.platforms.BuildOptions;
import com.quexten.ravtech.dk.packaging.platforms.DesktopPlatform;
import com.quexten.ravtech.dk.packaging.platforms.Platform;
import com.quexten.ravtech.dk.packaging.platforms.android.AlignStep;
import com.quexten.ravtech.dk.packaging.platforms.android.SignStep;
import com.quexten.ravtech.dk.ui.packaging.BuildReporterDialog;
import com.quexten.ravtech.util.Debug;

public class Packager {

	public enum TargetPlatform {
		Desktop, Windows, Mac, Linux, Android, iOS, WebGL
	}

	/** Queues up the required steps to package the application for the required target and executes them in a separate GUIWorker
	 * thread so that it doesn't lock the editors GUI.
	 *
	 * @param dialog - the dialog to log the progress in
	 * @param targetPlatform - the platform the app is packaged for (E.g Android / Dekstop / iOS)
	 * @param userData
	 * @param destinationDir - the destination, meaning the path the package is saved at */
	public static void build (BuildReporterDialog dialog,
		TargetPlatform targetPlatform, BuildOptions options) {

		PackageStep firstStep = null;
		PackageStep currentStep = null;

		if (options.isExternal()) {
			if (options.skipBuild) {
				firstStep = new PackageStep(dialog) {
					@Override
					public void run () {
						executeNext();
					}
				};
				currentStep = addCreateAssetBundleStep(dialog, firstStep,
					targetPlatform);
			} else
				firstStep = addAssetClearStep(dialog);
		} else
			firstStep = addAssetClearStep(dialog);

		// Build Engine
		if (currentStep == null) {
			currentStep = firstStep;
			if (!options.isExternal()) {
				currentStep = addCopyAssetsStep(dialog, currentStep);
			} else {
				currentStep = addCreateAssetBundleStep(dialog,
					currentStep, targetPlatform);
			}
			currentStep = currentStep.setNextStep(
				getWriteConfigStep(dialog, options.isExternal()));
			currentStep = addBuildEngineStep(dialog, currentStep, targetPlatform,
				options);
		}
		
		if(options.run) {
			currentStep = addRunStep(dialog, currentStep, targetPlatform, options);
		}
		
		PackageStep iterStep = firstStep;
		while(iterStep != null) {
			Debug.log("iterStep", iterStep);
			iterStep = iterStep.nextStep;
		}
		
		// Executes the queue in a separate thread
		new PackageWorker(firstStep).run();
	}

	private static PackageStep addRunStep (BuildReporterDialog dialog,
		PackageStep currentStep, TargetPlatform targetPlatform,
		BuildOptions options) {
		return currentStep.setNextStep(new RunPlatformStep(dialog, getPlatform(targetPlatform), options));
	}
	
	private static Platform getPlatform(TargetPlatform platform) {
		switch(platform) {
			case Android:
				return new AndroidPlatform();
			case Desktop:
				return new DesktopPlatform();
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
		return null;
	}
	
	private static PackageStep addAssetClearStep (
		BuildReporterDialog dialog) {
		return new DeleteDirectoryStep(dialog,
			RavTechDK.getLocalFile("builder/android/assets").file());
	}

	private static PackageStep addCopyAssetsStep (
		BuildReporterDialog dialog, PackageStep currentStep) {
		return currentStep.setNextStep(getWriteProjectStep(dialog))
			.setNextStep(new CopyDirectoryStep(dialog,
				RavTechDK.projectHandle.child("assets"),
				RavTechDK.getLocalFile("/builder/android/assets/")))
			.setNextStep(getDeleteProjectStep(dialog));
	}

	private static PackageStep addCreateAssetBundleStep (
		BuildReporterDialog dialog, PackageStep currentStep,
		TargetPlatform targetPlatform) {
		return currentStep.setNextStep(getWriteProjectStep(dialog))
			.setNextStep(new PackBundleStep(dialog))
			.setNextStep(new CopyStep(dialog,
				Gdx.files.absolute(System.getProperty("user.dir")
					+ "/temp/build.ravpack"),
				RavTechDK.projectHandle.child("builds")
					.child(targetPlatform.toString().toLowerCase())
					.child("assets.ravpack")))
			.setNextStep(getDeleteProjectStep(dialog));
	}

	private static CreateFileStep getWriteConfigStep (
		BuildReporterDialog dialog, boolean external) {
		return new CreateFileStep(dialog,
			RavTechDK.getLocalFile("builder/android/assets/config.json"),
			("{ \"title\": \"" + RavTechDK.project.appName
				+ "\",\n\"useAssetBundle\": " + String.valueOf(external)
				+ "\n}").getBytes());
	}

	private static PackageStep getWriteProjectStep (
		BuildReporterDialog dialog) {
		return new PackageStep(dialog) {
			@Override
			public void run () {
				buildReporterDialog.log("Saving Project.");
				RavTechDK.project
					.save(RavTechDK.projectHandle.child("assets"));
				executeNext();
			}
		};
	}

	private static PackageStep getDeleteProjectStep (
		BuildReporterDialog dialog) {
		return new PackageStep(dialog) {
			@Override
			public void run () {
				buildReporterDialog.log("Deleting Project.");
				RavTechDK.projectHandle.child("assets")
					.child("project.json").delete();
				executeNext();
			}
		};
	}

	@SuppressWarnings("incomplete-switch")
	private static PackageStep addBuildEngineStep (
		BuildReporterDialog dialog, PackageStep currentStep,
		TargetPlatform platform, BuildOptions options) {
		PackageStep tempStep = null;

		switch (platform) {
			case Desktop:
				tempStep = currentStep.setNextStep(
					new BuildPlatformStep(dialog, new DesktopPlatform(), options));
				return tempStep.setNextStep(new CopyStep(dialog,
					RavTechDK.getLocalFile(
						"builder/desktop/build/libs/desktop-1.0.jar"),
					RavTechDK.projectHandle.child("builds")
						.child("desktop").child("build.jar")));
			case Android:
				tempStep = currentStep
					.setNextStep(new ApkPreparationStep(dialog))
					.setNextStep(new BuildPlatformStep(dialog,
						new AndroidPlatform(), options))
					.setNextStep(new AlignStep(dialog));
				
				if (options.sign)
					tempStep = tempStep.setNextStep(
						new SignStep(dialog, options.credentials));

				final String releaseFile = "android-release-aligned";
				final String debugFile = "android-debug";
				
				return tempStep.setNextStep(new CopyStep(dialog,
					Gdx.files.absolute(System.getProperty("user.dir")
						+ "/builder/android/build/outputs/apk/" + (options.sign ? releaseFile : debugFile) + ".apk"),
					RavTechDK.getLocalFile("builds/android")
						.child("build.apk")));
		}
		return null;
	}
}
