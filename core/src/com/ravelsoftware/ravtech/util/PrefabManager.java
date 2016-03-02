
package com.ravelsoftware.ravtech.util;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.components.GameObject;

public class PrefabManager {

	/** Loads the PrefabDescriber if it isn't yet loaded and then makes a GameObject out of it
	 * @param path - the path to the File containing the PrefabDescription
	 * @return the GameObject made out of the Prefab */
	public static GameObject getPrefab (String path) {
		AssetManager assetManager = RavTech.files.getAssetManager();
		if (!assetManager.isLoaded(path)) {
			assetManager.load(path, String.class);
			assetManager.finishLoading();
		}
		String prefabString = assetManager.get(path);
		Debug.startTimer("Prefab");
		GameObject object = makeObject(prefabString);
		Debug.endTimer("Prefab");
		return object;
	}

	/** Makes a String describing the GameObject, usable to create a Prefab
	 * @param object - the GameObject that should be Serialized
	 * @return the String describing the Prefab */
	public static String makePrefab (GameObject object) {
		Json json = new Json();
		json.setOutputType(OutputType.json);
		String temp = json.toJson(object);
		int transformStart = ordinalIndexOf(temp, '{', 1);
		int firstComponentStart = ordinalIndexOf(temp, '{', 2);
		String finalstring = temp.substring(0, transformStart) + temp.substring(firstComponentStart);
		return finalstring;
	}

	/** Makes a GameObject out of the given string
	 * @param string - the string describing the GameObject
	 * @return the GameObject */
	public static GameObject makeObject (String string) {
		Json json = new Json();
		GameObject object = json.fromJson(GameObject.class, string);
		@SuppressWarnings("rawtypes")
		Array<AssetDescriptor> dependencies = new Array<AssetDescriptor>();
		object.load(dependencies);
		for (int i = 0; i < dependencies.size; i++)
			RavTech.files.getAssetManager().load(dependencies.get(i));
		object.finishedLoading();
		return object;
	}

	/** Returns the position of the n-th occurence of the character provided in the given string
	 * @param str - the String that is searched in
	 * @param c - the character that is searched for
	 * @param n - the occurence count of the character
	 * @return - the position of the character */
	private static int ordinalIndexOf (String str, char c, int n) {
		int pos = str.indexOf(c, 0);
		while (n-- > 0 && pos != -1)
			pos = str.indexOf(c, pos + 1);
		return pos;
	}
}
