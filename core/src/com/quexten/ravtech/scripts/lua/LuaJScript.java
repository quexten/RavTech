
package com.quexten.ravtech.scripts.lua;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import com.badlogic.gdx.utils.ObjectMap;
import com.quexten.ravtech.components.GameObject;
import com.quexten.ravtech.scripts.Script;
import com.quexten.ravtech.util.Debug;

public class LuaJScript extends Script {

	LuaJScriptLoader loader;
	
	Globals context;
	
	String script;
	String name;
	
	public LuaJScript (LuaJScriptLoader scriptLoader, String script, String name) {
		this.script = script;
		this.loader = scriptLoader;
		this.name = name;
	}

	public LuaJScript (LuaJScriptLoader scriptLoader, String script, String name, GameObject selfObject) {
		this(scriptLoader, script, name);
	}

	@Override
	public void init () {
		loadChunk(script);
		invokeFunction("init");
	}

	@Override
	public void update () {
		invokeFunction("update");
	}

	@Override
	public void setEnviroment (ObjectMap<String, Object> values) {
		
	}

	@Override
	public boolean isLoaded () {
		return true;
	}

	@Override
	public Object callFunction (String name, Object[] args) {
		LuaValue function = loader.globals.get(name);
		if (function == LuaValue.NIL)
			return LuaValue.NIL;
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
		return loader.globals.get(name);
	}

	void invokeFunction (String name) {
		try {
			loader.globals.get(name).invoke();
		} catch (LuaError luaError) {
			printLuaError(luaError);
			return;
		}
	}
	
	static void printTable (String key, LuaValue value, int layer) {
		if ( layer > 1)
			return;
		for (int i = 0; i < layer; i++)
			System.out.print("  ");
		System.out.println(key + "|" + value.typename() + "|" + value.tojstring());
			
		if (value.istable()) 
			for (int i = 0; i < ((LuaTable)value).keyCount(); i++) {
				String newKey = ((LuaTable)value).keys()[i].tojstring();
				printTable(newKey, ((LuaTable)value).get(newKey), layer + 1);
			}
	}

	void printLuaError (LuaError error) {
		error.printStackTrace();
		if (true)
			return;
		String[] messageParts = error.getMessage().split(":");
		String lineNumberString = messageParts[messageParts.length - 2];
		String message = messageParts[messageParts.length - 1];
		int lineNumber = Integer.parseInt(lineNumberString);

		Debug.logError("Lua", "Script Error in line " + lineNumber + " - " + script.split("\n")[lineNumber - 1] + " - " + message);
	}

	@Override
	public void loadChunk (String source) {
		try {
			LuaValue chunk = loader.globals.load(source);
			loader.initEnvironment(loader.globals);
			chunk.call();
		} catch (LuaError luaError) {
			printLuaError(luaError);
			return;
		}
	}

}
