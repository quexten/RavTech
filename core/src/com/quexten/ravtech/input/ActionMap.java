
package com.quexten.ravtech.input;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectIntMap;

public class ActionMap extends ObjectIntMap<String> implements Serializable {

	@Override
	public void write (Json json) {
		for (ObjectIntMap.Entry<String> entry : entries())
			json.writeValue(String.valueOf(entry.key), entry.value);
	}

	@Override
	public void read (Json json, JsonValue jsonData) {
		JsonValue currentValue = jsonData.child();
		if (currentValue == null)
			return;

		while (currentValue != null) {
			String value = currentValue.toString();
			this.put(value.substring(0, value.lastIndexOf(':')), Integer.valueOf(value.substring(value.lastIndexOf(':') + 2)));
			currentValue = currentValue.next();
		}
	}

}
