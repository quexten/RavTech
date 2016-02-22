package com.ravelsoftware.ravtech.dk.packaging;

import com.ravelsoftware.ravtech.dk.ui.packaging.BuildWizard.BuildReporterDialog;

public abstract class PackageStep implements Runnable {

    BuildReporterDialog buildReporterDialog;
    PackageStep nextStep;

    public PackageStep(BuildReporterDialog buildReporterDialog) {
        this.buildReporterDialog = buildReporterDialog;
    }

    public void executeNext () {
        if (nextStep != null) nextStep.run();
    }

    public PackageStep setNextStep (PackageStep nextStep) {
        this.nextStep = nextStep;
        return nextStep;
    }
}
