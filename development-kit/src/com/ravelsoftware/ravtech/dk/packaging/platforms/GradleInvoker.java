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

import com.ravelsoftware.ravtech.dk.shell.Shell;
import com.ravelsoftware.ravtech.dk.ui.packaging.BuildReporterDialog;
import com.ravelsoftware.ravtech.dk.ui.utils.StreamGobbler.Printer;

public class GradleInvoker {

	public static void Invoke (final BuildReporterDialog buildReporterDialog, String command) {
		String fileSeparator = System.getProperty("file.separator");
		Shell.executeCommand(new File(System.getProperty("user.dir") + fileSeparator + "builder" + fileSeparator),
			"gradlew.bat " + command + " --stacktrace", new Printer() {

				@Override
				public void run () {
					buildReporterDialog.log(line);
				}
			}, new Printer() {

				@Override
				public void run () {
					buildReporterDialog.logError(line);
				}
			});
	}

	public static void Invoke (final String command) {
		String fileSeparator = System.getProperty("file.separator");
		Shell.executeCommand(new File(System.getProperty("user.dir") + fileSeparator + "builder" + fileSeparator),
			"gradlew.bat " + command + " --stacktrace");
	}
}
