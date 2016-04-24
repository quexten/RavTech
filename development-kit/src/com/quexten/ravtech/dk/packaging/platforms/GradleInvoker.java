
package com.quexten.ravtech.dk.packaging.platforms;

import java.io.File;

import com.quexten.ravtech.dk.shell.Shell;
import com.quexten.ravtech.dk.ui.packaging.BuildReporterDialog;
import com.quexten.ravtech.dk.ui.utils.StreamGobbler.Printer;

public class GradleInvoker {

	public static void Invoke (
		final BuildReporterDialog buildReporterDialog, String command) {
		String fileSeparator = System.getProperty("file.separator");
		Shell.executeCommand(
			new File(System.getProperty("user.dir") + fileSeparator
				+ "builder" + fileSeparator),
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
		Shell.executeCommand(
			new File(System.getProperty("user.dir") + fileSeparator
				+ "builder" + fileSeparator),
			"gradlew.bat " + command + " --stacktrace");
	}
}
