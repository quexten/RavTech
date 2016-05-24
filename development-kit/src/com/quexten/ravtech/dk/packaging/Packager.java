
package com.quexten.ravtech.dk.packaging;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Keys;
import com.quexten.ravtech.dk.RavTechDK;
import com.quexten.ravtech.dk.packaging.platforms.BuildOptions;
import com.quexten.ravtech.dk.packaging.platforms.Platform;
import com.quexten.ravtech.dk.ui.packaging.BuildReporterDialog;
import com.quexten.ravtech.util.Debug;

public class Packager {

	@SuppressWarnings("rawtypes") private static ObjectMap<String, Platform> platforms = new ObjectMap<String, Platform>();

	@SuppressWarnings("rawtypes")
	public static void registerPlatform (String string, Platform platform) {
		platforms.put(string, platform);
	}

	public static Array<String> getPlatforms () {
		Array<String> tempKeys = new Array<String>();
		Keys<String> keyIterator = platforms.keys();
		while (keyIterator.hasNext)
			tempKeys.add(keyIterator.next());
		return tempKeys;
	}

	/** Queues up the required steps to package the application for the required target and executes them in a separate GUIWorker
	 * thread so that it doesn't lock the editors GUI.
	 *
	 * @param dialog - the dialog to log the progress in
	 * @param targetPlatform - the platform the app is packaged for (E.g Android / Dekstop / iOS)
	 * @param userData
	 * @param destinationDir - the destination, meaning the path the package is saved at */
	@SuppressWarnings("unchecked")
	public static void build (BuildReporterDialog dialog, BuildOptions options) {

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
				currentStep = addCreateAssetBundleStep(dialog, firstStep, options);
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
				currentStep = addCreateAssetBundleStep(dialog, currentStep, options);
			}
			currentStep = currentStep.setNextStep(getWriteConfigStep(dialog, options.isExternal()));
			currentStep = platforms.get(options.targetPlatform).addBuildEngineStep(dialog, currentStep, options);
		}

		if (options.run) {
			currentStep = addRunStep(dialog, currentStep, options);
		}

		PackageStep iterStep = firstStep;
		while (iterStep != null) {
			Debug.log("iterStep", iterStep);
			iterStep = iterStep.nextStep;
		}

		// Executes the queue in a separate thread
		new PackageWorker(firstStep).run();
	}

	private static PackageStep addRunStep (BuildReporterDialog dialog, PackageStep currentStep, BuildOptions options) {
		return currentStep.setNextStep(new RunPlatformStep(dialog, platforms.get(options.targetPlatform), options));
	}

	private static PackageStep addAssetClearStep (BuildReporterDialog dialog) {
		return new DeleteDirectoryStep(dialog, RavTechDK.getLocalFile("builder/android/assets").file());
	}

	private static PackageStep addCopyAssetsStep (BuildReporterDialog dialog, PackageStep currentStep) {
		return currentStep
			.setNextStep(getWriteProjectStep(dialog)).setNextStep(new CopyDirectoryStep(dialog,
				RavTechDK.projectHandle.child("assets"), RavTechDK.getLocalFile("/builder/android/assets/")))
			.setNextStep(getDeleteProjectStep(dialog));
	}

	private static PackageStep addCreateAssetBundleStep (BuildReporterDialog dialog, PackageStep currentStep,
		BuildOptions options) {
		return currentStep.setNextStep(getWriteProjectStep(dialog)).setNextStep(new PackBundleStep(dialog))
			.setNextStep(new CopyStep(dialog, Gdx.files.absolute(System.getProperty("user.dir") + "/temp/build.ravpack"),
				RavTechDK.projectHandle.child("builds").child(options.targetPlatform).child("assets.ravpack")))
			.setNextStep(getDeleteProjectStep(dialog));
	}

	private static CreateFileStep getWriteConfigStep (BuildReporterDialog dialog, boolean external) {
		return new CreateFileStep(dialog, RavTechDK.getLocalFile("builder/android/assets/config.json"),
			("{ \"title\": \"" + RavTechDK.project.appName + "\",\n\"useAssetBundle\": " + String.valueOf(external) + "\n}")
				.getBytes());
	}

	private static PackageStep getWriteProjectStep (BuildReporterDialog dialog) {
		return new PackageStep(dialog) {
			@Override
			public void run () {
				buildReporterDialog.log("Saving Project.");
				RavTechDK.project.save(RavTechDK.projectHandle.child("assets"));
				executeNext();
			}
		};
	}

	private static PackageStep getDeleteProjectStep (BuildReporterDialog dialog) {
		return new PackageStep(dialog) {
			@Override
			public void run () {
				buildReporterDialog.log("Deleting Project.");
				RavTechDK.projectHandle.child("assets").child("project.json").delete();
				executeNext();
			}
		};
	}

	@SuppressWarnings("rawtypes")
	public static Platform getPlatform (String targetPlatform) {
		System.out.println("GetPlatform:" + targetPlatform);
		return platforms.get(targetPlatform);
	}

}
