
package com.ravelsoftware.ravtech.dk.packaging.platforms.android;

import java.io.File;

import com.badlogic.gdx.Gdx;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.dk.shell.Shell;
import com.ravelsoftware.ravtech.dk.ui.packaging.BuildReporterDialog;
import com.ravelsoftware.ravtech.dk.ui.utils.StreamGobbler.Printer;

public class ApkManager {

	public static void align (final BuildReporterDialog buildReporterDialog, File inputFile, File outputFile) {
		String fileSeparator = System.getProperty("file.separator");
		String buildToolVersion = Gdx.files
			.absolute(RavTech.settings.getString("RavTechDK.android.sdk.dir") + fileSeparator + "build-tools").list()[0].name();
		Shell.executeCommand(
			new File(RavTech.settings.getString("RavTechDK.android.sdk.dir") + fileSeparator + "build-tools" + fileSeparator
				+ buildToolVersion + fileSeparator),
			"zipalign" + " -v 4 " + inputFile.getAbsolutePath() + " " + outputFile.getAbsolutePath(), new Printer() {
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

	public static void sign (final BuildReporterDialog buildReporterDialog, File inputFile, File keyStoreFile, String storepass,
		String aliasName, String keypass) {
		String fileSeparator = System.getProperty("file.separator");
		Shell
			.executeCommand(new File(RavTech.settings.getString("RavTechDK.java.jdk.dir") + fileSeparator + "bin" + fileSeparator),
				"jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore " + keyStoreFile.getAbsolutePath() + " "
					+ inputFile.getAbsolutePath() + " " + aliasName + " -keypass " + keypass + " -storepass " + storepass,
			new Printer() {
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

}
