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
package com.ravelsoftware.ravtech.history;

import com.ravelsoftware.ravtech.components.GameComponent;
import com.ravelsoftware.ravtech.util.GameObjectTraverseUtil;

public class ModifyChangeable extends Changeable {

    public int variableID;
    public Object newValue;
    Object oldValue;

    public ModifyChangeable() {
        super(null, null);
    }

    public ModifyChangeable(GameComponent component, String changeLabel, String variableName, Object oldValue, Object newValue) {
        super(component, changeLabel);
        this.variableID = component.getVariableId(variableName);
        this.newValue = newValue;
        this.oldValue = oldValue;
        redo();
    }

    @Override
    public void redo () {
        GameObjectTraverseUtil.gameComponentFromPath(pathToComponent).setVariable(variableID, newValue);
    }

    @Override
    public void undo () {
        GameObjectTraverseUtil.gameComponentFromPath(pathToComponent).setVariable(variableID, oldValue);
    }

    @Override
    public String getChangeLabel () {
        return changeLabel + newValue;
    }
}
