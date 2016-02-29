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

package com.ravelsoftware.ravtech.dk.packaging.platforms;

import java.io.File;

import com.ravelsoftware.ravtech.dk.ui.packaging.BuildReporterDialog;

public class AndroidPlatform implements Platform {

	public String deviceIdentifier = "";

	public AndroidPlatform () {
	}

	public AndroidPlatform (String deviceIdentifier) {
		this.deviceIdentifier = deviceIdentifier;
	}

	@Override
	public boolean build (File buildPath, BuildReporterDialog buildReporterDialog) {
		GradleInvoker.Invoke(buildReporterDialog, "assembleRelease --stacktrace");
		buildReporterDialog.setVisible(true);
		return false;
	}

	@Override
	public boolean run (BuildReporterDialog buildReporterDialog) {
		/*
		 * if (deviceIdentifier.length() == 0) GradleInvoker.Invoke(RavTechDK.ui.buildWizard.buildReporterDialog,
		 * "android:installDebug android:run --stacktrace"); else GradleInvoker.Invoke(RavTechDK.ui.buildWizard.buildReporterDialog,
		 * "android:installDebug android:run -Pargs=" + deviceIdentifier + " --stacktrace");
		 */
		return false;
	}
}
