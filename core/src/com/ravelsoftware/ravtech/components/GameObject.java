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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap.Entries;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.animation.Animation;
import com.ravelsoftware.ravtech.util.PrefabManager;

public class GameObject extends GameComponent implements Json.Serializable {

	@Override
	public ComponentType getType () {
		return ComponentType.GameObject;
	}

	@Override
	public String getName () {
		return this.name;
	}

	String name = "";
	public Transform transform;
	private Array<GameComponent> components;
	boolean isPrefab;
	String prefabPath;

	public GameObject () {
		this(0, 0);
	}

	public GameObject (float x, float y) {
		this.components = new Array<GameComponent>();
		Transform transformcomponent = new Transform(this, x, y, 0);
		this.addComponent(transformcomponent);
		this.transform = transformcomponent;
	}

	@Override
	public void load (@SuppressWarnings("rawtypes") Array<AssetDescriptor> dependencies) {
		for (int i = 0; i < components.size; i++)
			components.get(i).load(dependencies);
	}

	@Override
	public void finishedLoading () {
		for (int i = 0; i < components.size; i++)
			components.get(i).finishedLoading();
	}

	@Override
	public void update () {
		for (int i = 0; i < components.size; i++)
			components.get(i).update();
	}

	@Override
	public void draw (SpriteBatch batch) {
		for (int i = 0; i < components.size; i++) {
			GameComponent component = components.get(i);
			if (!(component instanceof Renderer)) component.draw(batch);
		}
	}

	@Override
	public void dispose () {
		for (GameComponent component : components)
			component.dispose();
		components.clear();
	}

	public void destroy () {
		dispose();
		if (getParent() != null)
			this.getParent().components.removeValue(this, true);
		else
			RavTech.currentScene.gameObjects.removeValue(this, true);
	}

	@Override
	public void write (Json json) {
		json.writeValue("name", name);
		json.writeArrayStart("components");
		for (int i = 0; i < components.size; i++) {
			GameComponent component = components.get(i);
			json.writeObjectStart();
			json.writeValue("componenttype", component.getType());
			component.write(json);
			json.writeObjectEnd();
		}
		json.writeArrayEnd();
	}

	@Override
	public void read (Json json, JsonValue jsonData) {
		this.name = jsonData.getString("name");
		for (int i = 0; i < jsonData.get("components").size; i++)
			readValue(json, jsonData.get("components").get(i));
	}

	public void readValue (Json json, JsonValue currententry) {
		String classname = currententry.get("componentType").asString();
		GameComponent component = null;
		if (classname.equals("Transform")) {
			this.transform.read(json, currententry);
			return;
		}
		if (classname.equals("SpriteRenderer"))
			component = new SpriteRenderer(currententry.getString("texture"), currententry.getFloat("width"),
				currententry.getFloat("height"));
		else if (classname.equals("Rigidbody"))
			component = new Rigidbody();
		else if (classname.equals("BoxCollider"))
			component = new BoxCollider();
		else if (classname.equals("AudioEmitter"))
			component = new AudioEmitter();
		else if (classname.equals("Light"))
			component = new Light();
		else if (classname.equals("ScriptComponent"))
			component = new ScriptComponent();
		else if (classname.equals("GameObject")) component = new GameObject();
		if (!classname.equals("Transform")) {
			this.addComponent(component);
			component.read(json, currententry);
		}
	}

	public static GameObject Instantiate (String prefabpath, Vector2 position, float rotation) {
		return Instantiate(prefabpath, position, rotation, true);
	}

	/** Creates an instance of the given prefab
	 * @param prefabpath - path to the prefab in the project
	 * @param position - position of the Instantiated prefab
	 * @param rotation - rotation of the Instantiated prefab
	 * @return the instance of the prefab */
	public static GameObject Instantiate (String prefabPath, Vector2 position, float rotation, boolean initScripts) {
		GameObject object = PrefabManager.getPrefab(prefabPath);
		RavTech.currentScene.addGameObject(object);
		object.transform.setPosition(position.x, position.y);
		object.transform.setRotation(rotation);
		if (!RavTech.sceneHandler.paused && initScripts) RavTech.sceneHandler.initScripts(object.getComponents());
		return null;
	}

	/** Adds the GameComponent and sets the GameObject as parent.
	 * @param component - the component to add
	 * @return The GameObjects for chaining. */
	public GameObject addComponent (GameComponent component) {
		components.add(component);
		component.setParent(this);
		return this;
	}

	public Array<GameComponent> getComponents () {
		return components;
	}

	public GameComponent getComponentByName (String name) {
		GameComponent tempcomp = null;
		for (GameComponent component : components)
			if (component.getName().equals(name)) {
				tempcomp = component;
				break;
			}
		return tempcomp;
	}

	public Array<GameComponent> getComponentsByName (String name) {
		Array<GameComponent> tempComponents = new Array<GameComponent>();
		for (GameComponent component : components)
			if (component.getName().equals(name)) tempComponents.add(component);
		return tempComponents;
	}

	public GameComponent getComponentByType (ComponentType rigidbody) {
		GameComponent tempcomp = null;
		for (GameComponent component : components)
			if (component.getType().equals(rigidbody)) {
				tempcomp = component;
				break;
			}
		return tempcomp;
	}

	public Array<GameComponent> getComponentsByType (ComponentType type) {
		Array<GameComponent> tempComponents = new Array<GameComponent>();
		for (GameComponent component : components)
			if (component.getType().equals(type)) tempComponents.add(component);
		return tempComponents;
	}

	public Array<GameObject> getGameObjectsInChildren () {
		Array<GameObject> components = new Array<GameObject>();
		for (GameComponent component : this.components)
			if (component.getType().equals(this.getType())) components.add((GameObject)component);
		return components;
	}

	/** @param string - type of component to return
	 * @return all components of the specified type in the gameobject or any of it's children. */
	public Array<GameComponent> getComponentsInChildren (String string) {
		Array<GameComponent> components = new Array<GameComponent>();
		for (int i = 0; i < this.components.size; i++) {
			GameComponent component = this.components.get(i);
			if (component.getType().equals(string) || string.equals("Renderer") && component instanceof Renderer)
				components.add(component);
			else if (component.getType().equals(this.getType()))
				components.addAll(((GameObject)component).getComponentsInChildren(string));
		}
		return components;
	}

	public Array<GameComponent> getComponentsInChildren (ComponentType... types) {
		Array<GameComponent> components = new Array<GameComponent>();
		for (int n = 0; n < this.components.size; n++) {
			GameComponent component = this.components.get(n);
			for (int i = 0; i < types.length; i++)
				if (component.getType().equals(types[i]) || types[i].equals(ComponentType.Renderer) && component instanceof Renderer)
					components.add(component);
			if (component.getType().equals(this.getType()))
				components.addAll(((GameObject)component).getComponentsInChildren(types));
		}
		return components;
	}

	public void removeComponent (GameComponent component) {
		Animator animator = (Animator)this.getComponentByType(ComponentType.Animator);
		if (component instanceof GameObject) if (animator != null) {
			Entries<String, Animation> iter = animator.animations.iterator();
			while (iter.hasNext()) {
				Entry<String, Animation> next = iter.next();
				for (int i = 0; i < next.value.timelines.size; i++)
					if (next.value.timelines.get(i).component.isDescendantOf((GameObject)component))
						next.value.timelines.removeIndex(i);
			}
		}
		component.dispose();
		components.removeValue(component, true);
	}

	public static GameObject find (String name) {
		return RavTech.currentScene.getObjectByName(name);
	}

	public static Array<GameObject> findAll (String name) {
		return RavTech.currentScene.getObjectsByName(name);
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
}
