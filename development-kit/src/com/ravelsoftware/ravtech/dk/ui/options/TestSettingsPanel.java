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
