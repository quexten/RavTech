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
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class TextButtonPair extends SliderComponentPair {

    public TextButtonPair(String tag, String text, ActionListener listener) {
        super(tag);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        JButton button = new JButton(text);
        button.addActionListener(listener);
        this.pairedComponent = button;
    }
}
