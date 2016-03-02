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

package com.ravelsoftware.ravtech.scripts.lua;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entries;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.ravelsoftware.ravtech.components.GameObject;
import com.ravelsoftware.ravtech.scripts.Script;

public class LuaJScript extends Script {

	Globals globals;
	LuaValue chunk;
	String script;

	public LuaJScript (String script) {
		this.script = script;
	}

	public LuaJScript (String script, GameObject selfObject) {
		this(script);
		ObjectMap<String, Object> values = new ObjectMap<String, Object>();
		values.put("this", selfObject);
		values.put("Keys", Keys.class);
		values.put("Input", Gdx.input);
		setEnviroment(values);
	}

	@Override
	public void init () {
		chunk = globals.load(script);
		chunk.call();
		globals.get("init").invoke();
	}

	@Override
	public void update () {
		globals.get("update").call();
	}

	@Override
	public void setEnviroment (ObjectMap<String, Object> values) {
		globals = JsePlatform.standardGlobals();
		Entries<String, Object> entries = values.iterator();
		while (entries.hasNext) {
			Entry<String, Object> entry = entries.next();
			globals.set(entry.key, CoerceJavaToLua.coerce(entry.value));
		}
	}

	@Override
	public boolean isLoaded () {
		return true;
	}
}
