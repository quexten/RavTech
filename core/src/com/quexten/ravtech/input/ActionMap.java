
package com.quexten.ravtech.input;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class ActionMap {

	ObjectMap<String, Integer> actionMap = new ObjectMap<String, Integer>();
	Array<String> deviceTypes = new Array<String>();

	public ActionMap (Array<String> devices) {
		this.deviceTypes.addAll(devices);
	}

	public ActionMap (String... devices) {
		this.deviceTypes.addAll(devices);
	}

	public void setMapping (String id, int mappedId) {
		actionMap.put(id, mappedId);
	}

	public int getId (String id) {
		return actionMap.get(id);
	}

	public boolean isFor (InputDevice[] devices) {
		boolean isSameLength = devices.length == this.deviceTypes.size;
		if (!isSameLength)
			return false;
		boolean isSame = true;
		for (int i = 0; i < devices.length; i++) {
			if (!this.deviceTypes.get(i).equals(devices[i].getType()))
				isSame = false;
		}
		return isSame;
	}

}
