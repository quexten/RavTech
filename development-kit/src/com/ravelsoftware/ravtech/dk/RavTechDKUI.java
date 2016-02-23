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
package com.ravelsoftware.ravtech.dk;

import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.dk.ui.animation.CurveEditor;
import com.ravelsoftware.ravtech.dk.ui.editor.RavTechDKFrame;
import com.ravelsoftware.ravtech.dk.ui.packaging.BuildWizard;

public class RavTechDKUI {

    public RavTechDKFrame ravtechDKFrame;
    public BuildWizard buildWizard;
    public CurveEditor curveEditor;

    public RavTechDKUI(RavTech ravtech) {
        ravtechDKFrame = new RavTechDKFrame(ravtech);
        buildWizard = new BuildWizard(ravtechDKFrame);
        curveEditor = new CurveEditor();
    }
}
