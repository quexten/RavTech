
package com.ravelsoftware.ravtech.scripts;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.ravelsoftware.ravtech.components.GameObject;

public class ScriptLoaderParameter extends AssetLoaderParameters<Script> {

	public GameObject selfObject;

	public ScriptLoaderParameter (GameObject selfObject) {
		this.selfObject = selfObject;
	}
}
