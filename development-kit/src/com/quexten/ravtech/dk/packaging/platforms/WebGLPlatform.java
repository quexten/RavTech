
package com.ravelsoftware.ravtech.dk.packaging.platforms;

import com.badlogic.gdx.files.FileHandle;
import com.ravelsoftware.ravtech.dk.ui.packaging.BuildReporterDialog;

public class WebGLPlatform implements Platform {

	@Override
	public boolean build (FileHandle buildPath,
		BuildReporterDialog buildReporterDialog) {
		GradleInvoker.Invoke(buildReporterDialog,
			"html:dist --stacktrace");
		buildReporterDialog.setVisible(true);
		return false;
	}

	@Override
	public boolean run (BuildReporterDialog buildReporterDialog) {
		GradleInvoker.Invoke("--stop");
		GradleInvoker.Invoke(buildReporterDialog,
			"html:superDev --stacktrace");
		buildReporterDialog.setVisible(true);
		return false;
	}
}
