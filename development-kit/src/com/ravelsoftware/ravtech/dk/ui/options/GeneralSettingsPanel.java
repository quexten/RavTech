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
package com.ravelsoftware.ravtech.dk.ui.options;

import java.awt.Component;
import java.awt.Frame;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JComponent;

import org.fife.ui.OptionsDialogPanel;

import com.badlogic.gdx.utils.Array;
import com.ravelsoftware.ravtech.RavTech;

public class GeneralSettingsPanel extends OptionsDialogPanel {

    private static final long serialVersionUID = 6941606304207590055L;
    Array<SettingsComponent> settingsComponents = new Array<SettingsComponent>();

    public GeneralSettingsPanel() {
        this.setName("General");
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        addComponent(new SettingsFileChooserTextField("RavTechDK.android.sdk.dir", "Android SDK: ", new File(""), this));
        addComponent(new SettingsFileChooserTextField("RavTechDK.java.jdk.dir", "Java JDK: ", new File(""), this));
        for (SettingsComponent component : settingsComponents)
            component.load();
    }

    void addComponent (Component component) {
        if (component instanceof SettingsComponent) settingsComponents.add((SettingsComponent)component);
        this.add(component);
    }

    @Override
    protected void doApplyImpl (Frame owner) {
        for (SettingsComponent component : settingsComponents)
            component.save();
        RavTech.settings.save();
    }

    @Override
    protected OptionsPanelCheckResult ensureValidInputsImpl () {
        return null;
    }

    @Override
    public JComponent getTopJComponent () {
        return null;
    }

    @Override
    protected void setValuesImpl (Frame owner) {
    }
}
