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

import javax.swing.JComponent;
import javax.swing.JLabel;

public class SliderComponentPair {

    public float changeFactor = 0.05f;
    public float initialValue;
    public JLabel nameLabel;
    public JComponent pairedComponent;
    int screenPassBuffer = 0; // buffer for when mouse moves
                              // off
                              // screen, back to other side
    public int xVal;

    public SliderComponentPair(String tag) {
        nameLabel = new JLabel(tag);
    }
}
