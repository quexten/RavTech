/*******************************************************************************
 * Copyright 2014-2016 Bernd Schoolmann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.ravelsoftware.ravtech.dk;

import java.awt.Color;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.UIManager;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.ravelsoftware.ravtech.components.GameComponent;
import com.ravelsoftware.ravtech.components.GameObject;
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
        RavTechDK.gizmoHandler.setupGizmos();
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
            inspectorChanged();
        RavTechDK.gizmoHandler.setupGizmos();
    }

    
}
