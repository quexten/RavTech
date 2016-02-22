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
