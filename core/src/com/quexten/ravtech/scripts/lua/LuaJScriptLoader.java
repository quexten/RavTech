
package com.quexten.ravtech.scripts.lua;

import com.quexten.ravtech.components.GameObject;
import com.quexten.ravtech.scripts.Script;
import com.quexten.ravtech.scripts.ScriptLoader;

public class LuaJScriptLoader implements ScriptLoader {

	@Override
	public Script createScript (String source, GameObject selfObject) {
		LuaJScript script = new LuaJScript(source, selfObject);
		return script;
	}

}
