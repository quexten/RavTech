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
import java.io.File;

import javax.swing.JFileChooser;

import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.dk.ui.components.FileChooserTextField;

public class SettingsFileChooserTextField extends FileChooserTextField implements SettingsComponent {

    private static final long serialVersionUID = -3939089507424335441L;
    String settingsKey;

    public SettingsFileChooserTextField(String settingsKey, String labelText, File selectedPath, Component parent) {
        super(labelText, selectedPath, parent, JFileChooser.DIRECTORIES_ONLY);
        this.settingsKey = settingsKey;
    }

    @Override
    public void load () {
        field.setText(RavTech.settings.getString(settingsKey));
    }

    @Override
    public void save () {
        RavTech.settings.setValue(settingsKey, field.getText().replaceAll("\\", "/"));
    }
}
