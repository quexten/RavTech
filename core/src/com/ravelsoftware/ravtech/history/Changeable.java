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

public class Changeable {

	public boolean isDummy = false;
	public boolean isLocal = true;
	public boolean previousConnected = false;
	public String pathToComponent;
	String changeLabel;

	public Changeable (GameComponent component, String changeLabel) {
		if (component != null) this.pathToComponent = GameObjectTraverseUtil.pathFromGameComponent(component);
		this.changeLabel = changeLabel;
	}

	public void redo () {
	}

	public void undo () {
	}

	public String getChangeLabel () {
		return changeLabel;
	}
}
