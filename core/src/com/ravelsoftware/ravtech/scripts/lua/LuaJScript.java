
package com.ravelsoftware.ravtech.scripts.lua;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entries;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.components.ComponentType;
import com.ravelsoftware.ravtech.components.GameObject;
import com.ravelsoftware.ravtech.scripts.Script;
import com.ravelsoftware.ravtech.util.Debug;

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
		values.put("Input", RavTech.input);
		values.put("Debug", Debug.class);
		values.put("Vector2", Vector2.class);
		values.put("Color", Color.class);
		values.put("Buttons", Buttons.class);
		values.put("GameObject", GameObject.class);
		values.put("ComponentType", ComponentType.class);
		values.put("RavTech", RavTech.class);
		values.put("Settings", RavTech.settings);
		values.put("Camera", RavTech.sceneHandler.worldCamera);
		setEnviroment(values);
	}

	@Override
	public void init () {
		loadChunk(this.script);
		invokeFunction("init");
	}

	@Override
	public void update () {
		invokeFunction("update");
	}

	@Override
	public void setEnviroment (ObjectMap<String, Object> values) {
		globals = JsePlatform.standardGlobals();
		Entries<String, Object> entries = values.iterator();
		while (entries.hasNext) {
			Entry<String, Object> entry = entries.next();
			globals.set(entry.key, entry.value instanceof LuaValue ? (LuaValue)entry.value : CoerceJavaToLua.coerce(entry.value));
		}
	}

	@Override
	public boolean isLoaded () {
		return true;
	}

	@Override
	public Object callFunction (String name, Object[] args) {
		LuaValue function = globals.get(name);
		if (function == LuaValue.NIL) return LuaValue.NIL;
		LuaValue returnValue = null;
		switch (args.length) {
		case 0:
			returnValue = function.call();
			break;
		case 1:
			returnValue = function.call(CoerceJavaToLua.coerce(args[0]));
			break;
		case 2:
			returnValue = function.call(CoerceJavaToLua.coerce(args[0]), CoerceJavaToLua.coerce(args[1]));
			break;
		case 3:
			returnValue = function.call(CoerceJavaToLua.coerce(args[0]), CoerceJavaToLua.coerce(args[1]),
				CoerceJavaToLua.coerce(args[2]));
			break;
		}
		return returnValue;
	}

	@Override
	public Object getVariable (String name) {
		return globals.get(name);
	}

	void invokeFunction (String name) {
		try {
			globals.get(name).invoke();
		} catch (LuaError luaError) {
			printLuaError(luaError);
			return;
		}
	}

	void printLuaError (LuaError error) {
		String[] messageParts = error.getMessage().split(":");
		String lineNumberString = messageParts[messageParts.length - 2];
		String message = messageParts[messageParts.length - 1];
		int lineNumber = Integer.parseInt(lineNumberString);

		Debug.logError("Lua", "Script Error in line " + lineNumber + " - " + script.split("\n")[lineNumber - 1] + " - " + message);
	}

	public void loadChunk (String source) {
		try {
			chunk = globals.load(source);
			chunk.call();
		} catch (LuaError luaError) {
			printLuaError(luaError);
			return;
		}
	}

}
