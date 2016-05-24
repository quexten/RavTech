
package com.quexten.ravtech.dk.packaging.platforms.android;

import java.io.File;

import com.quexten.ravtech.dk.packaging.PackageStep;
import com.quexten.ravtech.dk.ui.packaging.BuildReporterDialog;
import com.quexten.ravtech.dk.ui.packaging.PrinterListener;

public class SignStep extends PackageStep {

	KeyStoreCredentials credentials;

	public SignStep (BuildReporterDialog buildReporterDialog, KeyStoreCredentials credentials) {
		super(buildReporterDialog);
		this.credentials = credentials;
	}

	@Override
	public void run () {
		buildReporterDialog.log("Signing...");
		ApkManager.sign(buildReporterDialog,
			new File(System.getProperty("user.dir") + "/builder/android/build/outputs/apk/android-release-unsigned.apk"),
			credentials.keystoreFile, credentials.keystorePassword, credentials.aliasName, credentials.aliasPassword);
		buildReporterDialog.printerListeners.add(new PrinterListener() {
			@Override
			public void onPrint (String line) {
				if (line.equals("jar signed.")) {
					buildReporterDialog.log("Finished Signing.");
					buildReporterDialog.printerListeners.clear();
					SignStep.this.executeNext();
				}
			}
		});
	}

}
