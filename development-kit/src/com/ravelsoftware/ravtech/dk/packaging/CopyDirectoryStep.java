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
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.ravelsoftware.ravtech.dk.ui.packaging.BuildWizard.BuildReporterDialog;

public class CopyDirectoryStep extends PackageStep {

    File dstDir;
    File srcDir;

    public CopyDirectoryStep(BuildReporterDialog buildReporterDialog, File srcDir, File dstDir) {
        super(buildReporterDialog);
        this.srcDir = srcDir;
        this.dstDir = dstDir;
    }

    @Override
    public void run () {
        buildReporterDialog.log("Copy from [" + srcDir.getAbsolutePath() + "] to [" + dstDir.getAbsolutePath() + "]");
        try {
            FileUtils.copyDirectory(srcDir, dstDir);
        } catch (IOException e) {
            buildReporterDialog.logError(e.getMessage());
        }
        buildReporterDialog.log("Copy Done!");
        this.executeNext();
    }
}
