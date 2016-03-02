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

package com.ravelsoftware.ravtech.components;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public abstract class Renderer extends GameComponent implements Json.Serializable {

	@Override
	public ComponentType getType () {
		return ComponentType.Renderer;
	}

	public Renderer () {
	}

	public int sortingOrder = 0;
	public String sortingLayerName = "Default";
	public boolean enabled = true;

	@Override
	public abstract void load (@SuppressWarnings("rawtypes") Array<AssetDescriptor> dependencies);

	@Override
	public abstract void finishedLoading ();

	@Override
	public abstract void update ();

	@Override
	public abstract void draw (SpriteBatch batch);

	@Override
	public abstract void dispose ();

	@Override
	public void write (Json json) {
		json.writeValue("sortingLayerName", sortingLayerName);
		json.writeValue("sortingOrder", sortingOrder);
		json.writeValue("enabled", enabled);
	}

	@Override
	public void read (Json json, JsonValue jsonData) {
		if (jsonData.has("sortingLayerName")) this.sortingLayerName = jsonData.getString("sortingLayerName");
		if (jsonData.has("sortingOrder")) this.sortingOrder = jsonData.getInt("sortingOrder");
		if (jsonData.has("enabled")) this.enabled = jsonData.getBoolean("enabled");
	}

	@Override
	public String[] getVariableNames () {
		return null;
	}

	@Override
	public void setVariable (int variableID, Object value) {
	}

	@Override
	public int getVariableId (String variableName) {
		return 0;
	}

	@Override
	public Object getVariable (int variableID) {
		return 0;
	}

	@Override
	public Object[] getValiables () {
		return null;
	}

	@Override
	public String getName () {
		return null;
	}
}
