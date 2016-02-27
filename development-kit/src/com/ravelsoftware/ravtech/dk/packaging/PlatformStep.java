/*******************************************************************************
 * Copyright 2014-2016 Bernd Schoolmann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.ravelsoftware.ravtech.dk.packaging;

import java.io.File;

import com.ravelsoftware.ravtech.dk.packaging.platforms.Platform;
import com.ravelsoftware.ravtech.dk.ui.packaging.BuildReporterDialog;
import com.ravelsoftware.ravtech.dk.ui.packaging.PrinterListener;

public class PlatformStep extends PackageStep {

    File directory;
    Platform platform;
    boolean run;

    public PlatformStep(BuildReporterDialog buildReporterDialog, Platform platform) {
        super(buildReporterDialog);
        this.platform = platform;
        this.run = true;
    }

    public PlatformStep(BuildReporterDialog buildReporterDialog, Platform platform, File directory) {
        this(buildReporterDialog, platform);
        this.run = false;
        this.directory = directory;
    }

    @Override
    public void run () {
        if (run) { // Wether to do a test run or package the app for release
            buildReporterDialog.printerListeners.add(new PrinterListener() {

                public void onPrint (String line) {
                    if (line.equals("BUILD SUCCESSFUL")) PlatformStep.this.executeNext();
                }
            });
            platform.run(this.buildReporterDialog);
        } else
            platform.build(directory, buildReporterDialog);
    }
}
