
package com.quexten.ravtech.scripts;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.quexten.ravtech.components.GameObject;

public class ScriptLoaderParameter extends AssetLoaderParameters<Script> {

	public GameObject selfObject;

	public ScriptLoaderParameter (GameObject selfObject) {
		this.selfObject = selfObject;
	}
}
