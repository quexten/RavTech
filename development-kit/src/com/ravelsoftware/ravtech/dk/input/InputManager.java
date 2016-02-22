package com.ravelsoftware.ravtech.dk.input;

import java.awt.Cursor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap.Values;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.components.GameComponent;
import com.ravelsoftware.ravtech.components.GameObject;
import com.ravelsoftware.ravtech.components.Transform;
import com.ravelsoftware.ravtech.components.gizmos.Gizmo;
import com.ravelsoftware.ravtech.components.gizmos.TransformGizmo;
import com.ravelsoftware.ravtech.dk.RavTechDK;
import com.ravelsoftware.ravtech.dk.RavTechDKUtil;
import com.ravelsoftware.ravtech.dk.actions.CopyAction;
import com.ravelsoftware.ravtech.dk.actions.PasteAction;
import com.ravelsoftware.ravtech.history.ChangeManager;
import com.ravelsoftware.ravtech.history.CreateChangeable;
import com.ravelsoftware.ravtech.util.EventType;

import net.java.games.input.Component;

public class InputManager implements InputProcessor {

    public Vector2 dragStartPosition = new Vector2();
    public Vector2 dragCurrentPosition = new Vector2();
    Vector2 touchDownCoords = new Vector2();
    public float selectionAlpha;
    private boolean hasToLerp;
    private boolean timerStarted;
    private Vector2 targetPosition;
    private float targetZoom;
    private Gizmo draggedGizmo;
    boolean dragged = false;

    public void checkComponents (Component[] components) {
        for (int i = 0; i < components.length; i++) {
            Component component = components[i];
            component.getIdentifier();
            // Buttons
            if (component.getPollData() > 0) {
            }
        }
    }

    @Override
    public boolean keyDown (int keycode) {
        if (RavTech.input.isKeyPressed(Keys.CONTROL_LEFT) && keycode == Keys.C) new CopyAction().run();
        if (RavTech.input.isKeyPressed(Keys.CONTROL_LEFT) && keycode == Keys.V) new PasteAction().run();
        if (keycode == Keys.F2) GamepadObserver.pollControllers();
        
        if (keycode == Keys.TAB) {
            CreateChangeable changeable = new CreateChangeable(null, "Added GameObject",
                "{\"componentType\":\"GameObject\",\"name\":\"DEFAULT\",\"components\":[{\"componentType\":\"Transform\",\"x\":"
                    + RavTech.input.getWorldPosition().x + ",\"y\":" + RavTech.input.getWorldPosition().y
                    + ",\"rotation\":0,\"scale\":1}]}");
            ChangeManager.addChangeable(changeable);
        }
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
        if (RavTechDKUtil.selectedObjects.size > 0) {
            Values<Gizmo> values = RavTechDKUtil.selectedObjectGizmoMap.values();
            Gizmo closestGizmo = null;
            float closestDst = Float.MAX_VALUE;
            while (values.hasNext) {
                Gizmo giz = values.next();
                float gizDst = giz.input(0, EventType.MouseMoved);
                if (gizDst > 0 && gizDst < closestDst
                    && Math.abs(gizDst - closestDst) > 0.1f * 1 / 0.05f * RavTech.sceneHandler.worldCamera.zoom) {
                    closestDst = gizDst;
                    closestGizmo = giz;
                }
            }
            RavTechDK.ui.ravtechDKFrame
                .setCursor(Cursor.getPredefinedCursor(closestGizmo == null ? Cursor.DEFAULT_CURSOR : Cursor.MOVE_CURSOR));
            RavTechDKUtil.closestGizmo = closestGizmo;
        }
        return false;
    }

    public Transform getTransformAtPoint (Array<? extends GameComponent> objects) {
        Transform transform = null;
        for (int i = 0; i < objects.size; i++)
            if (objects.get(i) instanceof GameObject) {
                Transform localTransform = this.getTransformAtPoint(((GameObject)objects.get(i)).getComponents());
                if (localTransform != null) {
                    transform = localTransform;
                    break;
                }
            } else {
                Gizmo gizmo = RavTechDKUtil.createGizmoFor(objects.get(i));
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

    @Override
    public boolean scrolled (int amount) {
        hasToLerp = true;
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
        }
        return false;
    }

    @Override
    public boolean touchDown (int screenX, int screenY, int pointer, int button) {
        this.dragged = false;
        Vector3 unprojectedPosition = RavTech.sceneHandler.worldCamera.unproject(new Vector3(screenX, screenY, 0));
        if (button == Buttons.LEFT) {
            selectionAlpha = 0.3f;
            dragStartPosition = new Vector2(unprojectedPosition.x, unprojectedPosition.y);
            dragCurrentPosition = dragStartPosition.cpy();
        } else
            touchDownCoords = new Vector2(unprojectedPosition.x, unprojectedPosition.y);
        this.draggedGizmo = RavTechDKUtil.closestGizmo;
        if (this.draggedGizmo != null)
            this.draggedGizmo.input(0, EventType.MouseDown);
        else {
            Transform transform = this.getTransformAtPoint(RavTech.currentScene.gameObjects);
            if (transform != null) {
                Array<GameObject> objects = new Array<GameObject>();
                objects.add(transform.getParent());
                RavTechDKUtil.setSelectedObjects(objects);
                if (button == Buttons.LEFT) {
                    this.draggedGizmo = RavTechDKUtil.getGizmoFor(transform);
                    ((TransformGizmo)this.draggedGizmo).moveGrab = true;
                    this.draggedGizmo.input(0, EventType.MouseDown);
                    ((TransformGizmo)this.draggedGizmo).moveGrab = false;
                } else {
                }
            } else {
                RavTechDKUtil.selectedObjectGizmoMap.clear();
                RavTechDKUtil.selectedObjects.clear();
            }
        }
        return false;
    }

    @Override
    public boolean touchDragged (int screenX, int screenY, int pointer) {
        this.dragged = true;
        hasToLerp = false;
        boolean wasConsumed = this.draggedGizmo != null;
        RavTechDK.ui.ravtechDKFrame
            .setCursor(Cursor.getPredefinedCursor(!wasConsumed ? Cursor.DEFAULT_CURSOR : Cursor.MOVE_CURSOR));
        if (this.draggedGizmo != null) this.draggedGizmo.input(0, EventType.MouseDrag);
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
            int screenCenterWidth = Gdx.graphics.getWidth() / 2;
            int screenCenterHeight = Gdx.graphics.getHeight() / 2;
            int screenDiffX = screenCenterWidth - screenX;
            int screenDiffY = screenCenterHeight + screenY - Gdx.graphics.getHeight();
            RavTech.sceneHandler.worldCamera.position.set(touchDownCoords.x + screenDiffX * RavTech.sceneHandler.worldCamera.zoom,
                touchDownCoords.y + screenDiffY * RavTech.sceneHandler.worldCamera.zoom, 0);
            RavTech.sceneHandler.worldCamera.update();
        }
        return false;
    }

    @Override
    public boolean touchUp (int screenX, int screenY, int pointer, int button) {
        if (this.draggedGizmo != null) {
            RavTechDKUtil.closestGizmo = null;
            draggedGizmo.input(0, EventType.MouseUp);
            draggedGizmo = null;
        }
        return false;
    }
}
