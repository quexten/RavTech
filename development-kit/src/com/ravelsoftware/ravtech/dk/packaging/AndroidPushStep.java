
package com.ravelsoftware.ravtech.dk.packaging;

import com.ravelsoftware.ravtech.dk.adb.AdbManager;
import com.ravelsoftware.ravtech.dk.ui.packaging.BuildReporterDialog;

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
		this.buildReporterDialog.log("Pushing Assets to " + externalPath);
		this.buildReporterDialog.logError(AdbManager.executeAdbCommand("push " + localPath + " " + externalPath));
		this.buildReporterDialog.log("Finished Pushing Assets");
		this.executeNext();
	}
}
