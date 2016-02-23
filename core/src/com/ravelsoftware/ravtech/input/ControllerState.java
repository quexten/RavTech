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
package com.ravelsoftware.ravtech.input;

import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.utils.IntFloatMap;
import com.badlogic.gdx.utils.IntMap;

public class ControllerState {

    public IntFloatMap axisValues = new IntFloatMap();
    public IntMap<Boolean> lastButtonValues = new IntMap<Boolean>();
    public IntMap<Boolean> buttonValues = new IntMap<Boolean>();
    public PovDirection povDirection = PovDirection.center;

    public ControllerState() {
        for (int i = 0; i < 20; i++) {
            lastButtonValues.put(i, false);
            buttonValues.put(i, false);
        }
    }

    public void update () {
        for (int i = 0; i < buttonValues.size; i++)
            lastButtonValues.put(i, buttonValues.get(i));
    }
}
