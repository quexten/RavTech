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
package com.ravelsoftware.ravtech.dk.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.components.GameComponent;
import com.ravelsoftware.ravtech.components.GameObject;
import com.ravelsoftware.ravtech.components.Transform;
import com.ravelsoftware.ravtech.components.gizmos.Gizmo;
import com.ravelsoftware.ravtech.dk.RavTechDK;
import com.ravelsoftware.ravtech.dk.RavTechDKUtil;
import com.ravelsoftware.ravtech.dk.actions.CopyAction;
import com.ravelsoftware.ravtech.dk.actions.PasteAction;
import com.ravelsoftware.ravtech.util.EventType;

public class InputManager implements InputProcessor {

    public Vector2 dragStartPosition = new Vector2();
    public Vector2 dragCurrentPosition = new Vector2();
    Vector2 touchDownCoords = new Vector2();
    public float selectionAlpha;
    private boolean hasToLerp;
    private boolean timerStarted;
    private Vector2 targetPosition;
    private float targetZoom;

    @Override
    public boolean keyDown (int keycode) {
        if (RavTech.input.isKeyPressed(Keys.CONTROL_LEFT) && keycode == Keys.C) new CopyAction().run();
        if (RavTech.input.isKeyPressed(Keys.CONTROL_LEFT) && keycode == Keys.V) new PasteAction().run();
        return false;
    }

    @Override
    public boolean keyTyped (char character) {
        return false;
    }

    @Override
    public boolean keyUp (int keycode) {
        return false;
    }

    @Override
    public boolean mouseMoved (int screenX, int screenY) {
        if (RavTechDKUtil.selectedObjects.size > 0) RavTechDK.gizmoHandler.input(0, EventType.MouseMoved);
        return false;
    }

    @Override
    public boolean scrolled (int amount) {
        /*hasToLerp = true;
        float lastzoom = RavTech.sceneHandler.worldCamera.zoom;
        Vector2 lastposition = new Vector2(RavTech.sceneHandler.worldCamera.position.x,
            RavTech.sceneHandler.worldCamera.position.y);
        targetZoom += amount * RavTech.sceneHandler.worldCamera.zoom * 0.5;
        if (targetZoom < 0) targetZoom = 0.0001f;
        targetPosition = RavTech.input.getWorldPosition();
        targetPosition = targetPosition.add(lastposition.sub(targetPosition).scl(targetZoom / lastzoom));
        if (!timerStarted) {
            Timer.schedule(new Task() {

                @Override
                public void run () {
                    if (hasToLerp) {
                        RavTech.sceneHandler.worldCamera.zoom += 0.16f * (targetZoom - RavTech.sceneHandler.worldCamera.zoom);
                        RavTech.sceneHandler.worldCamera.position.lerp(new Vector3(targetPosition.x, targetPosition.y, 0), 0.16f);
                    }
                    if (Math.abs(targetZoom - RavTech.sceneHandler.worldCamera.zoom) < 0.00001f) hasToLerp = false;
                }
            }, 0, 0.016f);
            timerStarted = true;
        }*/
        return false;
    }

    @Override
    public boolean touchDown (int screenX, int screenY, int pointer, int button) {
       /* RavTechDKUtil.inspectorChanged();
        RavTechDK.gizmoHandler.input(button, EventType.MouseDown);
        Vector3 unprojectedPosition = RavTech.sceneHandler.worldCamera.unproject(new Vector3(screenX, screenY, 0));
        if (button == Buttons.LEFT) {
            selectionAlpha = 0.3f;
            dragStartPosition = new Vector2(unprojectedPosition.x, unprojectedPosition.y);
            dragCurrentPosition = dragStartPosition.cpy();
        } else
            touchDownCoords = new Vector2(unprojectedPosition.x, unprojectedPosition.y);*/
        return false;
    }

    @Override
    public boolean touchDragged (int screenX, int screenY, int pointer) {
        boolean wasConsumed = RavTechDK.gizmoHandler.input(0, EventType.MouseDrag);
        hasToLerp = false;
        if (!wasConsumed)
            RavTechDKUtil.renderSelection = true;
        else {
            RavTechDKUtil.renderSelection = false;
            return false;
        }
        if (Gdx.input.isButtonPressed(Buttons.LEFT) && !wasConsumed) {
            dragCurrentPosition.x = RavTech.input.getWorldPosition().x;
            dragCurrentPosition.y = RavTech.input.getWorldPosition().y;
            Array<GameObject> objects = RavTech.currentScene.getGameObjectsIn(dragStartPosition.x, dragStartPosition.y,
                dragCurrentPosition.x, dragCurrentPosition.y);
            RavTechDKUtil.setSelectedObjects(objects);
        }
        if (Gdx.input.isButtonPressed(Buttons.RIGHT)) {
          /*  int screenCenterWidth = Gdx.graphics.getWidth() / 2;
            int screenCenterHeight = Gdx.graphics.getHeight() / 2;
            int screenDiffX = screenCenterWidth - screenX;
            int screenDiffY = screenCenterHeight + screenY - Gdx.graphics.getHeight();
            RavTech.sceneHandler.worldCamera.position.set(touchDownCoords.x + screenDiffX * RavTech.sceneHandler.worldCamera.zoom,
                touchDownCoords.y + screenDiffY * RavTech.sceneHandler.worldCamera.zoom, 0);
            RavTech.sceneHandler.worldCamera.update();*/
        }
        return false;
    }

    @Override
    public boolean touchUp (int screenX, int screenY, int pointer, int button) {
        RavTechDK.gizmoHandler.input(button, EventType.MouseUp);
        return false;
    }

    public static Transform getTransformAtPoint (Array<? extends GameComponent> objects) {
        Transform transform = null;
        for (int i = 0; i < objects.size; i++)
            if (objects.get(i) instanceof GameObject) {
                Transform localTransform = getTransformAtPoint(((GameObject)objects.get(i)).getComponents());
                if (localTransform != null) {
                    transform = localTransform;
                    break;
                }
            } else {
                Gizmo gizmo = RavTechDK.gizmoHandler.createGizmoFor(objects.get(i));
                if (gizmo != null) {
                    boolean isIn = gizmo.isInBoundingBox(RavTech.input.getWorldPosition());
                    if (isIn) {
                        transform = objects.get(i).getParent().transform;
                        break;
                    }
                }
            }
        return transform;
    }
}
