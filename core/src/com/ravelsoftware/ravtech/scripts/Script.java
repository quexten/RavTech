
package com.ravelsoftware.ravtech.scripts;

import com.badlogic.gdx.utils.ObjectMap;

public abstract class Script {

	public abstract void init ();

	public abstract void update ();

	public abstract void setEnviroment (ObjectMap<String, Object> values);

	public abstract boolean isLoaded ();
}
