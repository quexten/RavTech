
package com.quexten.ravtech.dk.packaging;

import com.quexten.ravtech.dk.ui.packaging.BuildReporterDialog;

public abstract class PackageStep implements Runnable {

	protected BuildReporterDialog buildReporterDialog;
	PackageStep nextStep;

	public PackageStep (BuildReporterDialog buildReporterDialog) {
		this.buildReporterDialog = buildReporterDialog;
	}

	public void executeNext () {
		if (nextStep != null)
			nextStep.run();
	}

	public PackageStep setNextStep (PackageStep nextStep) {
		this.nextStep = nextStep;
		return nextStep;
	}
}
