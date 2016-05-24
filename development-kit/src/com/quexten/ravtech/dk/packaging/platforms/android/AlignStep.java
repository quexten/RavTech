
package com.quexten.ravtech.dk.packaging.platforms.android;

import java.io.File;

import com.quexten.ravtech.dk.packaging.PackageStep;
import com.quexten.ravtech.dk.ui.packaging.BuildReporterDialog;
import com.quexten.ravtech.dk.ui.packaging.PrinterListener;

public class AlignStep extends PackageStep {

	public AlignStep (BuildReporterDialog buildReporterDialog) {
		super(buildReporterDialog);
	}

	@Override
	public void run () {
		buildReporterDialog.log("Aligning...");
		new File(System.getProperty("user.dir") + "/builder/android/build/outputs/apk/android-release-aligned.apk").delete();
		ApkManager.align(buildReporterDialog,
			new File(System.getProperty("user.dir") + "/builder/android/build/outputs/apk/android-release-unsigned.apk"),
			new File(System.getProperty("user.dir") + "/builder/android/build/outputs/apk/android-release-aligned.apk"));
		buildReporterDialog.printerListeners.add(new PrinterListener() {
			@Override
			public void onPrint (String line) {
				if (line.equals("Verification succesful")) {
					buildReporterDialog.log("Finished Aligning.");
					buildReporterDialog.printerListeners.clear();
					AlignStep.this.executeNext();
				}
			}
		});
	}

}
