
package com.ravelsoftware.ravtech.dk;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.ravelsoftware.ravtech.components.GameComponent;
import com.ravelsoftware.ravtech.components.GameObject;
import com.ravelsoftware.ravtech.history.ChangeManager;
import com.ravelsoftware.ravtech.history.CreateChangeable;
import com.ravelsoftware.ravtech.history.RemoveChangeable;

public class RavTechDKUtil {

	public static GameObject currentDragPreview;
	static File currentScene;
	static boolean inspectorChanged;
	public static boolean renderSelection;
	public static String selectedObject;
	public static Array<GameObject> selectedObjects = new Array<GameObject>();
	static ExecutorService service = Executors.newFixedThreadPool(5);

	public static void addExecutable (Runnable runnable) {
		service.execute(runnable);
	}

	public static void addHistoryComponent (GameComponent toAddComponent) {
		GameObject tempObject = new GameObject();
		tempObject.addComponent(toAddComponent);
		Json json = new Json();
		StringBuilder serialComponentBuilder = new StringBuilder(json.toJson(toAddComponent));
		serialComponentBuilder.insert(1, "componenttype: \"" + toAddComponent.getType() + "\",");
		ChangeManager.addChangeable(new CreateChangeable(RavTechDKUtil.selectedObjects.get(0), "Added: " + toAddComponent.getType(),
			serialComponentBuilder.toString()));
		tempObject.dispose();
		RavTechDKUtil.setSelectedObject(RavTechDKUtil.selectedObjects.get(0));
	}

	public static boolean hasInspectorChanged () {
		return inspectorChanged;
	}

	public static void inspectorChanged () {
		inspectorChanged = true;
	}

	public static void inspectorSynced () {
		inspectorChanged = false;
	}

	public static void removeHistoryComponent (GameComponent toRemoveComponent) {
		GameObject parentObject = toRemoveComponent.getParent();
		GameObject tempObject = new GameObject();
		tempObject.addComponent(toRemoveComponent);
		Json json = new Json();
		StringBuilder serialComponentBuilder = new StringBuilder(json.toJson(toRemoveComponent));
		serialComponentBuilder.insert(1, "componenttype: \"" + toRemoveComponent.getType() + "\",");
		toRemoveComponent.setParent(parentObject);
		ChangeManager.addChangeable(
			new RemoveChangeable(toRemoveComponent, "Removed: " + toRemoveComponent.getType(), serialComponentBuilder.toString()));
		tempObject.getComponents().removeValue(toRemoveComponent, true);
		tempObject.dispose();
	}

	/** Sets the currently selected object
	 * @param object - the object that has been selected */
	public static void setSelectedObject (GameObject object) {
		selectedObjects.clear();
		if (object != null) selectedObjects.add(object);
		inspectorChanged();
		RavTechDK.gizmoHandler.setupGizmos();
	}

	/** Sets the currently selected objects
	 * @param objects - the objects that have been selected */
	public static void setSelectedObjects (Array<GameObject> objects) {
		Array<GameObject> componentCountList = new Array<GameObject>();
		for (int i = 0; i < selectedObjects.size; i++)
			if (!componentCountList.contains(selectedObjects.get(i), true)) componentCountList.add(selectedObjects.get(i));
		int lastObjectCount = componentCountList.size;
		selectedObjects.clear();
		selectedObjects.addAll(objects);
		if (lastObjectCount != selectedObjects.size) inspectorChanged();
		RavTechDK.gizmoHandler.setupGizmos();
	}
}
