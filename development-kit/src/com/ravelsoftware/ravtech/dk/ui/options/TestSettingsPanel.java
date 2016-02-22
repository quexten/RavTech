package com.ravelsoftware.ravtech.dk.ui.options;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.JComponent;
import javax.swing.JSlider;

import org.fife.ui.OptionsDialogPanel;

public class TestSettingsPanel extends OptionsDialogPanel {

    /**
     *
     */
    private static final long serialVersionUID = -489067306810513388L;
    JSlider slider;

    public TestSettingsPanel() {
        this.setName("TestSettingsPanelName");
        this.setBackground(Color.RED);
        slider = new JSlider(0, 500);
        this.add(slider);
    }

    @Override
    protected void doApplyImpl (Frame owner) {
        owner.setSize(new Dimension(slider.getValue(), (int)owner.getSize().getHeight()));
    }

    @Override
    protected OptionsPanelCheckResult ensureValidInputsImpl () {
        return new OptionsPanelCheckResult(this);
    }

    @Override
    public JComponent getTopJComponent () {
        return (JComponent)this.getComponents()[0];
    }

    @Override
    protected void setValuesImpl (Frame owner) {
        slider.setValue((int)owner.getSize().getWidth());
    }
}
