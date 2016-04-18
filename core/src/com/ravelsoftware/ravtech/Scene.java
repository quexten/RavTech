
package com.ravelsoftware.ravtech;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ravelsoftware.ravtech.components.GameObject;
import com.ravelsoftware.ravtech.graphics.RenderProperties;

public class Scene {

	public Array<GameObject> gameObjects;
	public RenderProperties renderProperties;

	public Scene () {
		gameObjects = new Array<GameObject>();
		renderProperties = new RenderProperties();
	}

	/** Adds new GameObject at the specified coordinates
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return The new GameObject */
	public GameObject addGameObject (float x, float y) {
		GameObject object = new GameObject();
		gameObjects.add(object);
		object.transform.setPosition(x, y);
		return object;
	}

	/** Adds already created GameObject to the Scene
	 * @param object the object to add
	 * @return The added GameObject */
	public GameObject addGameObject (GameObject object) {
		gameObjects.add(object);
		return object;
	}

	/** Gets the GameObject at the specified position within a 0.5 radius
	 * @param x - the x coordinate
	 * @param y - the y coordinate
	 * @return The GameObject or null if no GameObject is close by */
	public GameObject getObjectAt (float x, float y) {
		GameObject temp = null;
		for (GameObject object : gameObjects)
			if (new Vector2(object.transform.getPosition().x, object.transform.getPosition().y).dst(new Vector2(x, y)) < 0.5) {
				temp = object;
				break;
			}
		return temp;
	}

	/** Gets all GameObjects within the specified rectangle area
	 * @param x the left x position
	 * @param y the bottom y position
	 * @param endx the right x position
	 * @param endy the top y position
	 * @return The GameObjects in that area */
	public Array<GameObject> getGameObjectsIn (float x, float y, float endx, float endy) {
		Array<GameObject> objects = new Array<GameObject>();
		float lx = x < endx ? x : endx;
		float ly = y < endy ? y : endy;
		float hx = x > endx ? x : endx;
		float hy = y > endy ? y : endy;
		for (GameObject object : gameObjects) {
			float objectX = object.transform.getPosition().x;
			float objectY = object.transform.getPosition().y;
			if (lx < objectX && objectX < hx && ly < objectY && objectY < hy)
				objects.add(object);
		}
		return objects;
	}

	/** Gets the first GameObject that matches the specified name
	 * @param name the name to search for
	 * @return The GameObject or null if none is found */
	public GameObject getObjectByName (String name) {
		GameObject temp = null;
		for (GameObject object : gameObjects)
			if (name.equals(object.getName())) {
				temp = object;
				break;
			}
		return temp;
	}

	/** Gets all GameObjects that match the specified name
	 * @param name the name to search for
	 * @return The GameObjects or null if none are found */
	public Array<GameObject> getObjectsByName (String name) {
		Array<GameObject> tempComponents = new Array<GameObject>();
		for (GameObject component : gameObjects)
			if (component.getName().equals(name))
				tempComponents.add(component);
		return tempComponents;
	}

	/** Disposes all GameObjects in the Scene */
	public void dispose () {
		for (int i = 0; i < gameObjects.size; i++)
			gameObjects.get(i).dispose();
		gameObjects.clear();
	}
}
