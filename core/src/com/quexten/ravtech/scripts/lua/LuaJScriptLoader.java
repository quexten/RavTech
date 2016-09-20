
package com.quexten.ravtech.scripts.lua;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entries;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.quexten.ravtech.components.GameObject;
import com.quexten.ravtech.scripts.Script;
import com.quexten.ravtech.scripts.ScriptLoader;

public class LuaJScriptLoader extends ScriptLoader {
	
	Globals globals; 
		
	public LuaJScriptLoader() {
		globals = JsePlatform.standardGlobals();		
	}
	
	public void initEnvironment(Globals globals) {
		ObjectMap<String, Object> environment = this.getEnvironment();
		Entries<String, Object> entries = environment.iterator();
		while (entries.hasNext) {
			Entry<String, Object> entry = entries.next();
			globals.set(entry.key, entry.value instanceof LuaValue ? (LuaValue)entry.value : CoerceJavaToLua.coerce(entry.value));
		}
	}
	
	@Override
	public Script createScript (String source, String name, GameObject selfObject) {
		LuaJScript script = new LuaJScript(this, source, name, selfObject);
		return script;
	}

}
