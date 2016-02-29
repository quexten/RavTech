/*******************************************************************************
 * Copyright 2014-2016 Bernd Schoolmann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.ravelsoftware.ravtech.dk.packaging;

import java.io.File;

import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.dk.RavTechDK;
import com.ravelsoftware.ravtech.dk.packaging.platforms.AndroidPlatform;
import com.ravelsoftware.ravtech.dk.packaging.platforms.DesktopPlatform;
import com.ravelsoftware.ravtech.dk.packaging.platforms.WebGLPlatform;
import com.ravelsoftware.ravtech.dk.ui.packaging.BuildReporterDialog;

public class Packager {

	public enum TargetPlatform {
		Desktop, Windows, Mac, Linux, Android, iOS, WebGL
	}

	/** Queues up the required steps to package the application for the required target and executes them in a separate GUIWorker
	 * thread so that it doesn't lock the editors GUI.
	 *
	 * @param buildReporterDialog - the dialog to log the progress in
	 * @param targetPlatform - the plattform the app is packaged for (E.g Android / Dekstop / iOS)
	 * @param destinationDir - the destionation, meaning the path the package is saved at */
	public static void dist (BuildReporterDialog buildReporterDialog, TargetPlatform targetPlatform, File destinationDir) {
		PackageStep firstStep = null;
		// Builds the packaging chain
		// Executes the queue in a separate thread
		new PackageWorker(firstStep).run();
	}

	/** Queues up the required steps to run the application for the required target and executes them in a separate GUIWorker
	 * thread so that it doesn't lock the editors GUI.
	 *
	 * @param buildReporterDialog - the dialog to log the progress in
	 * @param platform - the platform to run the app on
	 * @param deviceIdentifier - the device identifier in case there are multiple devices. Null if there is only 1 device */
	public static void run (BuildReporterDialog buildReporterDialog, TargetPlatform platform, String deviceIdentifier) {
		PackageStep firstStep = null;
		RavTechDK.project.save(RavTechDK.projectHandle.child("assets"));
		switch (platform) {
		case Desktop:
		case Windows:
		case Mac:
		case Linux:
			RavTechDK.saveScene(RavTech.files.getAssetHandle(RavTechDK.getCurrentScene()));
			firstStep = new PackBundleStep(buildReporterDialog);
			firstStep.setNextStep(new PackBundleStep(buildReporterDialog))
				.setNextStep(new CopyStep(buildReporterDialog, new File(System.getProperty("user.dir") + "/temp/build.ravpack"),
					new File(System.getProperty("user.dir") + "/builder/android/assets/resourcepack.ravpack")))
				.setNextStep(new PlatformStep(buildReporterDialog, new DesktopPlatform()));
			break;
		case Android:
			RavTechDK.saveScene(RavTech.files.getAssetHandle(RavTechDK.getCurrentScene()));
			firstStep = new PackBundleStep(buildReporterDialog);
			firstStep.setNextStep(new PackBundleStep(buildReporterDialog))
				.setNextStep(new AndroidPushStep(buildReporterDialog, System.getProperty("user.dir") + "/temp/build.ravpack ",
					"/sdcard/Android/obb/com.ravelsoftware.ravtech.android/main.1.com.ravelsoftware.ravtech.android.obb"))
				.setNextStep(new PlatformStep(buildReporterDialog, new AndroidPlatform(deviceIdentifier)));
			break;
		case WebGL:
			RavTechDK.saveScene(RavTech.files.getAssetHandle(RavTechDK.getCurrentScene()));
			firstStep = new CopyDirectoryStep(buildReporterDialog, RavTechDK.projectHandle.child("assets").file(),
				new File(System.getProperty("user.dir") + "/builder/android/assets/"));
			firstStep
				.setNextStep(new CopyDirectoryStep(buildReporterDialog, RavTechDK.projectHandle.child("assets").file(),
					new File(System.getProperty("user.dir") + "/builder/html/war/assets/")))
				.setNextStep(new PlatformStep(buildReporterDialog, new WebGLPlatform()));
			break;
		default:
			break;
		}
		firstStep.run();
		RavTechDK.projectHandle.child("assets").child("project.json").delete();
	}
}
