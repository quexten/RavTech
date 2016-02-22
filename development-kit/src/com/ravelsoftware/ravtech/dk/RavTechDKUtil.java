package com.ravelsoftware.ravtech.dk;

import java.awt.Color;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.UIManager;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entries;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.ravelsoftware.ravtech.components.BoxCollider;
import com.ravelsoftware.ravtech.components.CircleCollider;
import com.ravelsoftware.ravtech.components.GameComponent;
import com.ravelsoftware.ravtech.components.GameObject;
import com.ravelsoftware.ravtech.components.Light;
import com.ravelsoftware.ravtech.components.PolygonCollider;
import com.ravelsoftware.ravtech.components.SpriteRenderer;
import com.ravelsoftware.ravtech.components.Transform;
import com.ravelsoftware.ravtech.components.gizmos.BoxColliderGizmo;
import com.ravelsoftware.ravtech.components.gizmos.CircleColliderGizmo;
import com.ravelsoftware.ravtech.components.gizmos.ConeLightGizmo;
import com.ravelsoftware.ravtech.components.gizmos.Gizmo;
import com.ravelsoftware.ravtech.components.gizmos.PolygonColliderGizmo;
import com.ravelsoftware.ravtech.components.gizmos.SpriteRendererGizmo;
import com.ravelsoftware.ravtech.components.gizmos.TransformGizmo;
import com.ravelsoftware.ravtech.dk.ui.editor.Inspector.InspectableType;
import com.ravelsoftware.ravtech.dk.ui.utils.ColorUtils;
import com.ravelsoftware.ravtech.history.ChangeManager;
import com.ravelsoftware.ravtech.history.CreateChangeable;
import com.ravelsoftware.ravtech.history.RemoveChangeable;

public class RavTechDKUtil {

    public static GameObject currentDragPreview;
    static File currentScene;
    public static InspectableType inspectableType = InspectableType.GameComponents;
    static boolean inspectorChanged;
    public static boolean renderSelection;
    public static String selectedObject;
    public static ObjectMap<GameComponent, Gizmo> selectedObjectGizmoMap = new ObjectMap<GameComponent, Gizmo>();
    public static Gizmo closestGizmo;
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
        ChangeManager.addChangeable(new CreateChangeable(RavTechDKUtil.selectedObjects.get(0),
            "Added: " + toAddComponent.getType(), serialComponentBuilder.toString()));
        tempObject.dispose();
        RavTechDKUtil.setSelectedObject(RavTechDKUtil.selectedObjects.get(0));
    }

    public static Color getAccentColor () {
        return UIManager.getColor("Ravtech.foreground");
    }

    public static com.badlogic.gdx.graphics.Color getGdxAccentColor () {
        return ColorUtils.swingToGdx(getAccentColor());
    }

    public static Gizmo getGizmoFor (GameComponent component) {
        Entries<GameComponent, Gizmo> iterator = selectedObjectGizmoMap.iterator();
        while (iterator.hasNext()) {
            Entry<GameComponent, Gizmo> entry = iterator.next();
            if (entry.key == component) return entry.value;
        }
        return null;
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
        ChangeManager.addChangeable(new RemoveChangeable(toRemoveComponent, "Removed: " + toRemoveComponent.getType(),
            serialComponentBuilder.toString()));
        tempObject.getComponents().removeValue(toRemoveComponent, true);
        tempObject.dispose();
    }

    /** sets the currently selected object
     *
     * @param object - the object that has been selected */
    public static void setSelectedObject (GameObject object) {
        selectedObjects.clear();
        if (object != null) selectedObjects.add(object);
        inspectorChanged();
        setupGizmos();
    }

    /** sets the currently selected objects
     *
     * @param objects - the objects that have been selected */
    public static void setSelectedObjects (Array<GameObject> objects) {
        Array<GameObject> componentCountList = new Array<GameObject>();
        for (int i = 0; i < selectedObjects.size; i++)
            if (!componentCountList.contains(selectedObjects.get(i), true)) componentCountList.add(selectedObjects.get(i));
        int lastObjectCount = componentCountList.size;
        selectedObjects.clear();
        selectedObjects.addAll(objects);
        if (lastObjectCount != selectedObjects.size)
            // Debug.log("Last", lastObjectCount + " | " +
            // (selectedObjects.size));
            inspectorChanged();
        setupGizmos();
    }

    static void setupGizmos () {
        selectedObjectGizmoMap.clear();
        for (int i = 0; i < selectedObjects.size; i++) {
            GameObject selectedObject = selectedObjects.get(i);
            if (selectedObject != null) for (int n = 0; n < selectedObject.getComponents().size; n++) {
                GameComponent iteratedComponent = selectedObject.getComponents().get(n);
                Gizmo gizmo = null;
                gizmo = createGizmoFor(iteratedComponent);
                if (gizmo != null) selectedObjectGizmoMap.put(iteratedComponent, gizmo);
            }
        }
    }

    public static Gizmo createGizmoFor (GameComponent component) {
        Class<? extends GameComponent> iteratedComponentClass = component.getClass();
        Gizmo gizmo = null;
        if (iteratedComponentClass.equals(Transform.class))
            gizmo = new TransformGizmo((Transform)component);
        else if (iteratedComponentClass.equals(BoxCollider.class))
            gizmo = new BoxColliderGizmo((BoxCollider)component);
        else if (iteratedComponentClass.equals(CircleCollider.class))
            gizmo = new CircleColliderGizmo((CircleCollider)component);
        else if (iteratedComponentClass.equals(Light.class))
            gizmo = new ConeLightGizmo((Light)component);
        else if (iteratedComponentClass.equals(PolygonCollider.class))
            gizmo = new PolygonColliderGizmo((PolygonCollider)component);
        else if (iteratedComponentClass.equals(SpriteRenderer.class)) gizmo = new SpriteRendererGizmo((SpriteRenderer)component);
        return gizmo;
    }
}
