package com.ravelsoftware.ravtech.scripts;

import com.ravelsoftware.ravtech.components.GameObject;

public interface ScriptLoader {
	
	public Script createScript(String source, GameObject selfObject);
	
}
