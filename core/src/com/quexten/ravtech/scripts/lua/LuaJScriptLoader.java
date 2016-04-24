
package com.ravelsoftware.ravtech.scripts.lua;

import com.ravelsoftware.ravtech.components.GameObject;
import com.ravelsoftware.ravtech.scripts.Script;
import com.ravelsoftware.ravtech.scripts.ScriptLoader;

public class LuaJScriptLoader implements ScriptLoader {

	@Override
	public Script createScript (String source, GameObject selfObject) {
		LuaJScript script = new LuaJScript(source, selfObject);
		return script;
	}

}
