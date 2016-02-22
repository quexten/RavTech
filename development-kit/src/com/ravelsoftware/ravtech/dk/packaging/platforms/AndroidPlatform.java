package com.ravelsoftware.ravtech.dk.packaging.platforms;

import java.io.File;

import com.ravelsoftware.ravtech.dk.RavTechDK;
import com.ravelsoftware.ravtech.dk.ui.packaging.BuildWizard.BuildReporterDialog;

public class AndroidPlatform implements Platform {

    public String deviceIdentifier = "";

    public AndroidPlatform() {
    }

    public AndroidPlatform(String deviceIdentifier) {
        this.deviceIdentifier = deviceIdentifier;
    }

    @Override
    public boolean build (File buildPath, BuildReporterDialog buildReporterDialog) {
        GradleInvoker.Invoke(buildReporterDialog, "assembleRelease --stacktrace");
        buildReporterDialog.setVisible(true);
        return false;
    }

    @Override
    public boolean run (BuildReporterDialog buildReporterDialog) {
        if (deviceIdentifier.length() == 0)
            GradleInvoker.Invoke(RavTechDK.ui.buildWizard.buildReporterDialog, "android:installDebug android:run --stacktrace");
        else
            GradleInvoker.Invoke(RavTechDK.ui.buildWizard.buildReporterDialog,
                "android:installDebug android:run -Pargs=" + deviceIdentifier + " --stacktrace");
        return false;
    }
}
