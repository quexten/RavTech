
package com.quexten.ravtech.scripts;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;
import com.quexten.ravtech.RavTech;
import com.quexten.ravtech.components.ComponentType;
import com.quexten.ravtech.components.GameObject;
import com.quexten.ravtech.components.Rigidbody;
import com.quexten.ravtech.util.Debug;

public abstract class ScriptLoader {

	ObjectMap<String, Object> environment;
	
	public abstract Script createScript (String source, String name, GameObject selfObject);

	private void initEnvironment() {
		ObjectMap<String, Object> values = new ObjectMap<String, Object>();
		values.put("Keys", Keys.class);
		values.put("Input", RavTech.input);
		values.put("Debug", Debug.class);
		values.put("Vector2", Vector2.class);
		values.put("Color", Color.class);
		values.put("Buttons", Buttons.class);
		values.put("GameObject", GameObject.class);
		values.put("ComponentType", ComponentType.class);
		values.put("RavTech", RavTech.class);
		values.put("Settings", RavTech.settings);
		values.put("Graphics", Gdx.graphics);
		values.put("Box2DWorld", RavTech.sceneHandler.box2DWorld);
		values.put("Net", Gdx.net);
		this.environment = values;
	}
	
	public ObjectMap<String, Object> getEnvironment() {
		if(environment == null)
			initEnvironment();
		return environment;
	}
	
}
