package com.ravelsoftware.ravtech.dk.packaging;

import java.io.File;

import com.ravelsoftware.ravtech.dk.RavTechDK;
import com.ravelsoftware.ravtech.dk.ui.packaging.BuildWizard.BuildReporterDialog;
import com.ravelsoftware.ravtech.util.Zipper;

public class PackBundleStep extends PackageStep {

    public PackBundleStep(BuildReporterDialog buildReporterDialog) {
        super(buildReporterDialog);
    }

    @Override
    public void run () {
        Zipper zipper = new Zipper(this.buildReporterDialog);
        new File(System.getProperty("user.dir") + "/temp/").mkdir();
        zipper.zipFolder(RavTechDK.projectHandle.child("assets").path(), System.getProperty("user.dir") + "/temp/build.ravpack");
        this.executeNext();
    }
}
