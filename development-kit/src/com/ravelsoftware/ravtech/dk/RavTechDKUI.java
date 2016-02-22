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
