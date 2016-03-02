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

package com.ravelsoftware.ravtech.settings;

import java.util.Iterator;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.ObjectMap;

public class RavSettings {

	ObjectMap<String, Object> values = new ObjectMap<String, Object>();
	private ObjectMap<String, SettingsValueListener> valueListeners = new ObjectMap<String, SettingsValueListener>();
	Preferences preferences;

	public RavSettings () {
		preferences = Gdx.app.getPreferences("RavTech");
		if (preferences.get().size() > 0)
			load();
		else {
			setValue("renderDebug", false);
			setValue("useLights", true);
			setValue("targetFramerate", 60f);
		}
	}

	public void setValue (String key, Object value) {
		if (valueListeners.get(key) != null) valueListeners.get(key).settingChanged(getValue(key), value);
		values.put(key, value);
		if (preferences == null) return;
		if (value instanceof Boolean)
			preferences.putBoolean(key, Boolean.valueOf(String.valueOf(value)));
		else if (value instanceof String)
			preferences.putString(key, String.valueOf(value));
		else if (value instanceof Integer)
			preferences.putInteger(key, Integer.valueOf(String.valueOf(value)));
		else if (value instanceof Float)
			preferences.putFloat(key, Float.valueOf(String.valueOf(value)));
		else if (value instanceof Double) preferences.putFloat(key, Float.valueOf(String.valueOf(value)));
	}

	public Object getValue (String key) {
		return values.get(key);
	}

	public boolean getBoolean (String key) {
		return Boolean.valueOf(values.get(key).toString());
	}

	public String getString (String key) {
		return String.valueOf(values.get(key));
	}

	public int getInt (String key) {
		String value = String.valueOf(values.get(key));
		if (value.endsWith(".0")) value = value.substring(0, value.length() - 2);
		return Integer.valueOf(value);
	}

	public float getFloat (String key) {
		return Float.valueOf(values.get(key).toString());
	}

	public double getDouble (String key) {
		return Double.valueOf(values.get(key).toString());
	}

	public void addValueListener (String value, SettingsValueListener listener) {
		valueListeners.put(value, listener);
	}

	public void save () {
		preferences.flush();
	}

	public void load () {
		Preferences preferences = Gdx.app.getPreferences("RavTech");
		if (preferences == null) return;
		this.preferences = preferences;
		Map<String, ?> preferenceMap = preferences.get();
		Iterator<String> keyIterator = preferenceMap.keySet().iterator();
		while (keyIterator.hasNext()) {
			String key = keyIterator.next();
			setValue(key, preferenceMap.get(key));
		}
	}

	public boolean has (String settingsKey) {
		return values.containsKey(settingsKey);
	}
}
