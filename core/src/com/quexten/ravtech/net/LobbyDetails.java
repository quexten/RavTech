
package com.quexten.ravtech.net;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entries;
import com.badlogic.gdx.utils.ObjectMap.Entry;

public class LobbyDetails {

	public Object connectionInformation;
	public ObjectMap<String, Object> values;

	@Override
	public String toString () {
		String returnString = String.valueOf(connectionInformation) + "--LobbyDetails--\n";
		Entries<String, Object> entries = values.iterator();
		while (entries.hasNext) {
			Entry<String, Object> entry = entries.next();
			returnString = returnString + entry.key + "|" + String.valueOf(entry.value) + "\n";
		}
		return returnString;
	}
}
