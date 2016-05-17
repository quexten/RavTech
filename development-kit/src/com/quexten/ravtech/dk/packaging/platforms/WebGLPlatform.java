
package com.quexten.ravtech.dk.packaging.platforms;

import com.quexten.ravtech.dk.packaging.PackageStep;
import com.quexten.ravtech.dk.ui.packaging.BuildReporterDialog;

public class WebGLPlatform extends Platform {

	@Override
	public void build (BuildReporterDialog buildReporterDialog, BuildOptions options) {
		GradleInvoker.Invoke(buildReporterDialog,
			"html:dist --stacktrace");
		buildReporterDialog.setVisible(true);
	}

	@Override
	public void run (BuildReporterDialog buildReporterDialog, BuildOptions options) {
		GradleInvoker.Invoke("--stop");
		GradleInvoker.Invoke(buildReporterDialog,
			"html:superDev --stacktrace");
		buildReporterDialog.setVisible(true);
	}

	@Override
	public BuildOptions getOptions () {
		return null;
	}

	@Override
	public PackageStep addBuildEngineStep (BuildReporterDialog dialog,
		PackageStep currentStep, BuildOptions options) {
		return null;
	}
}
