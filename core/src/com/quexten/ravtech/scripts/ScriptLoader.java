
package com.quexten.ravtech.scripts;

import com.quexten.ravtech.components.GameObject;

public interface ScriptLoader {

	public Script createScript (String source, GameObject selfObject);

}
