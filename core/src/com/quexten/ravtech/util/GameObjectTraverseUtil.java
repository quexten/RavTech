
package com.quexten.ravtech.util;

import com.badlogic.gdx.utils.Array;
import com.quexten.ravtech.RavTech;
import com.quexten.ravtech.components.GameComponent;
import com.quexten.ravtech.components.GameObject;

public class GameObjectTraverseUtil {

	public static String pathFromGameComponent (GameComponent object) {
		StringBuilder builder = new StringBuilder();
		while (object != null) {
			builder.insert(0, "/");
			builder.insert(1, object.getName() + "{" + getComponentID(object) + "}");
			object = object.getParent();
		}
		return builder.toString();
	}

	static int getComponentID (GameComponent component) {
		Array<GameComponent> components = null;
		if (component.getParent() != null)
			components = component.getParent().getComponents();
		else {
			components = new Array<GameComponent>();
			for (int i = 0; i < RavTech.currentScene.gameObjects.size; i++)
				components.add(RavTech.currentScene.gameObjects.get(i));
		}
		int i = 0;
		for (int n = 0; n < components.size; n++) {
			GameComponent tempComponent = components.get(n);
			if (tempComponent != component && tempComponent.getName().equals(component.getName()))
				i++;
			else if (tempComponent == component)
				break;
		}
		return i;
	}

	static GameComponent getComponentByID (GameObject parent, String component) {
		Array<GameComponent> components = null;
		if (parent != null)
			components = parent.getComponents();
		else {
			components = new Array<GameComponent>();
			for (int i = 0; i < RavTech.currentScene.gameObjects.size; i++)
				components.add(RavTech.currentScene.gameObjects.get(i));
		}
		int i = 0;
		String num = component;
		num = num.substring(num.indexOf("{") + 1);
		num = num.substring(0, num.indexOf("}"));
		int n = Integer.valueOf(num);
		String name = component.substring(0, component.indexOf("{"));
		for (GameComponent tempComponent : components)
			if (tempComponent.getName().equals(name) && n != i)
				i++;
			else if (tempComponent.getName().equals(name) && n == i)
				return tempComponent;
		return null;
	}

	public static String[] copyOfRange (String[] array, int initialIndex, int endIndex) {
		String[] tempArray = new String[endIndex - initialIndex];
		for (int i = initialIndex; i < endIndex; i++)
			tempArray[i - initialIndex] = array[i];
		return tempArray;
	}

	public static GameComponent gameComponentFromPath (String path) {
		if (path == null)
			return null;
		String[] pathNodes = path.split("/");
		if (pathNodes.length > 1) {
			pathNodes = copyOfRange(pathNodes, 1, pathNodes.length);
			GameComponent currentObject = getComponentByID(null, pathNodes[0]);
			if (pathNodes.length == 1)
				return currentObject;
			for (int i = 1; i < pathNodes.length; i++)
				currentObject = getComponentByID((GameObject)currentObject, pathNodes[i]);
			return currentObject;
		}
		return null;
	}
}
