
package com.quexten.ravtech.dk.packaging;

import com.quexten.ravtech.dk.adb.AdbManager;
import com.quexten.ravtech.dk.ui.packaging.BuildReporterDialog;
import com.quexten.ravtech.util.Debug;

public class AndroidPushStep extends PackageStep {

	String localPath;
	String externalPath;

	public AndroidPushStep (BuildReporterDialog buildReporterDialog, String localPath, String externalPath) {
		super(buildReporterDialog);
		this.localPath = localPath;
		this.externalPath = externalPath;
	}

	@Override
	public void run () {
		buildReporterDialog.log("Pushing Assets to " + externalPath);
		Debug.logError("Error", AdbManager.executeAdbCommand("push " + localPath + " " + externalPath));
		buildReporterDialog.log("Finished Pushing Assets");
		executeNext();
	}
}
