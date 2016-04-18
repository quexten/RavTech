
package com.ravelsoftware.ravtech.dk.packaging;

import com.ravelsoftware.ravtech.dk.adb.AdbManager;
import com.ravelsoftware.ravtech.dk.ui.packaging.BuildReporterDialog;
import com.ravelsoftware.ravtech.util.Debug;

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
