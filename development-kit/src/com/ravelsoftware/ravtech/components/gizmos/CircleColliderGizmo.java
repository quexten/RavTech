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

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.components.CircleCollider;
import com.ravelsoftware.ravtech.dk.ui.utils.ColorUtils;
import com.ravelsoftware.ravtech.history.ChangeManager;
import com.ravelsoftware.ravtech.history.ModifyChangeable;
import com.ravelsoftware.ravtech.util.EventType;

public class CircleColliderGizmo extends Gizmo {

    public CircleCollider circleCollider;
    float oldRadius = -1f;

    public CircleColliderGizmo(CircleCollider circleCollider) {
        this.circleCollider = circleCollider;
        this.isExclusive = true;
    }

    @Override
    public void draw (ShapeRenderer renderer, boolean selected) {
        renderer.setColor(selected ? ColorUtils.getSelectionColor() : ColorUtils.getGizmoColor(circleCollider));
        Vector2 middlePosition = this.getMiddlePosition();
        renderer.circle(middlePosition.x, middlePosition.y, circleCollider.radius, 72);
    }

    @Override
    public float input (int button, int eventtype) {
        switch (eventtype) {
            case EventType.MouseMoved:
                float distanceToMiddle = getMiddleDistance();
                if (Math.abs(distanceToMiddle - circleCollider.radius) < RavTech.sceneHandler.worldCamera.zoom * 3)
                    return distanceToMiddle;
                else
                    return -1;
            case EventType.MouseDown:
                oldRadius = circleCollider.radius;
                break;
            case EventType.MouseDrag:
                new ModifyChangeable(circleCollider, "", "radius", oldRadius, getMiddleDistance()).redo();
                break;
            case EventType.MouseUp:
                ChangeManager.addChangeable(new ModifyChangeable(circleCollider, "Set Circle Collider Radius: ", "radius",
                    oldRadius, getMiddleDistance()));
                break;
        }
        return -1f;
    }

    @Override
    public boolean isInBoundingBox (Vector2 coord) {
        return false;
    }

    private Vector2 getMiddlePosition () {
        return circleCollider.getParent().transform.getPosition()
            .add(circleCollider.getPosition().rotate(circleCollider.getParent().transform.getRotation()));
    }

    private float getMiddleDistance () {
        return getMiddlePosition().dst(RavTech.input.getWorldPosition());
    }
}
