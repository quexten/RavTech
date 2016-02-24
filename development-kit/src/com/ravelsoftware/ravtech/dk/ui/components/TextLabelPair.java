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
package com.ravelsoftware.ravtech.dk.ui.components;

import java.awt.Font;

import javax.swing.JTextField;

public class TextLabelPair extends SliderComponentPair {

    public TextLabelPair(String tag, String initialValue) {
        super(tag);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        this.pairedComponent = new JTextField(initialValue);
    }
}
