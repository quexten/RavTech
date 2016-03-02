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

package com.ravelsoftware.ravtech.scripts;

import com.badlogic.gdx.utils.Array;

public class WebGLScriptManager {

	static Array<Script> scripts = new Array<Script>();
	public static boolean initialized;

	public static boolean areLoaded () {
		boolean loaded = true;
		for (int i = 0; i < scripts.size; i++)
			if (!scripts.get(i).isLoaded()) {
				loaded = false;
				break;
			}
		return loaded;
	}

	public static void initialize () {
		initialized = true;
		for (int i = 0; i < scripts.size; i++)
			scripts.get(i).init();
	}

	public static void registerScript (Script script) {
		scripts.add(script);
	}
}
