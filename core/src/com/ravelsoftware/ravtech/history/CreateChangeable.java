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

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.components.GameComponent;
import com.ravelsoftware.ravtech.components.GameObject;
import com.ravelsoftware.ravtech.util.GameObjectTraverseUtil;

public class CreateChangeable extends Changeable {

	String gameComponent;
	String componentType;
	String removePath;

	public CreateChangeable () {
		super(null, null);
	}

	/** @param component - the component, the new component is added to
	 * @param changeLabel - the labeling for the history view of the change event
	 * @param gameComponent - the added Component in JSON format */
	public CreateChangeable (GameComponent parent, String changeLabel, String gameComponent) {
		super(parent, changeLabel);
		this.gameComponent = gameComponent;
		redo();
	}

	public void redo () {
		GameComponent parent = GameObjectTraverseUtil.gameComponentFromPath(pathToComponent);
		JsonValue jsonData = new JsonReader().parse(gameComponent);
		Json json = new Json();
		if (parent != null) {
			((GameObject)parent).readValue(json, jsonData);
			removePath = GameObjectTraverseUtil
				.pathFromGameComponent(((GameObject)parent).getComponents().get(((GameObject)parent).getComponents().size - 1));
		} else {
			GameObject object = new GameObject();
			object.readValue(json, jsonData);
			GameObject newObject = (GameObject)object.getComponents().get(1);
			newObject.setParent(null);
			removePath = GameObjectTraverseUtil.pathFromGameComponent(newObject);
			RavTech.currentScene.addGameObject(newObject);
		}
		componentType = jsonData.getString("componenttype");
	}

	public void undo () {
		GameObject component = (GameObject)GameObjectTraverseUtil.gameComponentFromPath(removePath);
		component.destroy();
	}
}
