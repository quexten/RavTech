
package com.quexten.ravtech.dk.packaging.platforms;

import com.quexten.ravtech.dk.packaging.PackageStep;
import com.quexten.ravtech.dk.ui.packaging.BuildReporterDialog;

public class WebGLPlatform extends Platform<WebGLBuildOptions, WebGLBuildOptionsTable> {

	@Override
	public void build (BuildReporterDialog buildReporterDialog, WebGLBuildOptions options) {
		GradleInvoker.Invoke(buildReporterDialog,
			"html:dist --stacktrace");
		buildReporterDialog.setVisible(true);
	}

	@Override
	public void run (BuildReporterDialog buildReporterDialog, WebGLBuildOptions options) {
		GradleInvoker.Invoke("--stop");
		GradleInvoker.Invoke(buildReporterDialog,
			"html:superDev --stacktrace");
		buildReporterDialog.setVisible(true);
	}

	@Override
	public WebGLBuildOptions getOptions () {
		return null;
	}

	@Override
	public PackageStep addBuildEngineStep (BuildReporterDialog dialog,
		PackageStep currentStep, WebGLBuildOptions options) {
		return null;
	}

	@Override
	public WebGLBuildOptionsTable getOptionsTable (
		WebGLBuildOptions options) {
		return null;
	}

	@Override
	public void applyOptions (WebGLBuildOptionsTable optionsTable,
		WebGLBuildOptions options) {		
	}
}
