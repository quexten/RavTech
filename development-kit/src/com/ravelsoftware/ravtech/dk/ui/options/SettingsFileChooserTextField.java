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
