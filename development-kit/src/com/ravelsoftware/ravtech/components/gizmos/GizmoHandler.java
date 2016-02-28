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
package com.ravelsoftware.ravtech.components.gizmos;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entries;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.ObjectMap.Values;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.components.BoxCollider;
import com.ravelsoftware.ravtech.components.CircleCollider;
import com.ravelsoftware.ravtech.components.GameComponent;
import com.ravelsoftware.ravtech.components.GameObject;
import com.ravelsoftware.ravtech.components.Light;
import com.ravelsoftware.ravtech.components.PolygonCollider;
import com.ravelsoftware.ravtech.components.SpriteRenderer;
import com.ravelsoftware.ravtech.components.Transform;
import com.ravelsoftware.ravtech.dk.RavTechDKUtil;
import com.ravelsoftware.ravtech.dk.input.InputManager;
import com.ravelsoftware.ravtech.util.EventType;

public class GizmoHandler {

    ObjectMap<GameComponent, Gizmo> selectedObjectGizmoMap = new ObjectMap<GameComponent, Gizmo>();
    Gizmo draggedGizmo;
    Gizmo closestGizmo;
    Gizmo exclusiveGizmo;

    /** Renders the currently active gizmos
     * @param renderer - the shaperenderer to render the gizmos with */
    public void render (ShapeRenderer renderer) {
        if (exclusiveGizmo == null)
            for (int i = 0; i < RavTechDKUtil.selectedObjects.size; i++) {
                GameObject object = RavTechDKUtil.selectedObjects.get(i);
                if (object != null) for (int n = 0; n < object.getComponents().size; n++) {
                    Gizmo gizmo = getGizmoFor(object.getComponents().get(n));
                    if (gizmo != null) gizmo.draw(renderer, gizmo == closestGizmo);
                }
            }
        else
            exclusiveGizmo.draw(renderer, exclusiveGizmo == closestGizmo);
    }

    /** Inputs the given input to all gizmos and returns whether the input has been consumed
     * @param button - the button that the mouse action is performed with
     * @param eventType - the type of mouse event performed
     * @return - Whether the event has been consumed */
    public boolean input (int button, int eventType) {
        switch (eventType) {
            case EventType.MouseMoved:
                if (exclusiveGizmo == null) {
                    Values<Gizmo> values = selectedObjectGizmoMap.values();
                    Gizmo closestGizmo = null;
                    float closestDst = Float.MAX_VALUE;
                    while (values.hasNext) {
                        Gizmo giz = values.next();
                        float gizDst = giz.input(0, EventType.MouseMoved);
                        if (gizDst > 0 && gizDst < closestDst
                            && Math.abs(gizDst - closestDst) > 0.1f * 1 / 0.05f * RavTech.sceneHandler.worldCamera.zoom
                            && !giz.isExclusive) {
                            closestDst = gizDst;
                            closestGizmo = giz;
                        }
                    }
                    this.closestGizmo = closestGizmo;
                } else
                    closestGizmo = exclusiveGizmo.input(0, EventType.MouseMoved) > 0f ? exclusiveGizmo : null;
                return closestGizmo != null;
            case EventType.MouseDown:
                this.draggedGizmo = closestGizmo;
                if (this.draggedGizmo != null)
                    this.draggedGizmo.input(0, EventType.MouseDown);
                else {
                    Transform transform = InputManager.getTransformAtPoint(RavTech.currentScene.gameObjects);
                    if (transform != null) {
                        Array<GameObject> objects = new Array<GameObject>();
                        objects.add(transform.getParent());
                        RavTechDKUtil.setSelectedObjects(objects);
                        if (button == Buttons.LEFT) {
                            this.draggedGizmo = getGizmoFor(transform);
                            ((TransformGizmo)this.draggedGizmo).moveGrab = true;
                            this.draggedGizmo.input(0, EventType.MouseDown);
                            ((TransformGizmo)this.draggedGizmo).moveGrab = false;
                        } else {
                        }
                    } else {
                        selectedObjectGizmoMap.clear();
                        RavTechDKUtil.selectedObjects.clear();
                        setExclusiveGizmo(null);
                    }
                }
                return true;
            case EventType.MouseUp:
                if (this.draggedGizmo != null) {
                    closestGizmo = null;
                    draggedGizmo.input(0, EventType.MouseUp);
                    draggedGizmo = null;
                    RavTechDKUtil.renderSelection = false;
                }
                return true;
            case EventType.MouseDrag:
                if (this.draggedGizmo != null) this.draggedGizmo.input(button, EventType.MouseDrag);
                return this.draggedGizmo != null;
        }
        return false;
    }

    /** Gets the gizmo for a specified component in the list of currently selected GameObjects */
    public Gizmo getGizmoFor (GameComponent component) {
        Entries<GameComponent, Gizmo> iterator = selectedObjectGizmoMap.iterator();
        while (iterator.hasNext()) {
            Entry<GameComponent, Gizmo> entry = iterator.next();
            if (entry.key == component) return entry.value;
        }
        return null;
    }

    /** Sets up gizmos for the currently selected list of objects */
    public void setupGizmos () {
        selectedObjectGizmoMap.clear();
        for (int i = 0; i < RavTechDKUtil.selectedObjects.size; i++) {
            GameObject selectedObject = RavTechDKUtil.selectedObjects.get(i);
            if (selectedObject != null) for (int n = 0; n < selectedObject.getComponents().size; n++) {
                GameComponent iteratedComponent = selectedObject.getComponents().get(n);
                Gizmo gizmo = null;
                gizmo = createGizmoFor(iteratedComponent);
                if (gizmo != null) selectedObjectGizmoMap.put(iteratedComponent, gizmo);
            }
        }
    }

    /** Creates gizmo for a specified component
     * @param component - the component to create a gizmo for
     * @return Returns the created gizmo */
    public Gizmo createGizmoFor (GameComponent component) {
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

    /** Sets exclusivity of input and rendering for a specified component, unset if the specified component is null
     * @param component - the component to grant exclusivity to */
    public void setExclusiveGizmo (GameComponent component) {
        if (component != null)
            exclusiveGizmo = getGizmoFor(component);
        else
            exclusiveGizmo = null;
    }
}