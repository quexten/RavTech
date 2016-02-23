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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.components.CircleCollider;
import com.ravelsoftware.ravtech.history.ChangeManager;
import com.ravelsoftware.ravtech.history.ModifyChangeable;
import com.ravelsoftware.ravtech.util.EventType;

public class CircleColliderGizmo extends Gizmo {

    public CircleCollider circleCollider;
    private Vector2 colliderPosition;
    private Vector2 grabPosition;
    private boolean isGrabbed;
    private boolean grabbedPosition;
    private float oldRadius;
    private Vector2 oldPosition;

    public CircleColliderGizmo(CircleCollider circleCollider) {
        this.circleCollider = circleCollider;
    }

    @Override
    public void draw (ShapeRenderer renderer, SpriteBatch batch, boolean selected) {
        Vector2 mousePosition = RavTech.input.getWorldPosition();
        renderer.setAutoShapeType(true);
        Gdx.gl.glLineWidth(2.0f);
        if (!isGrabbed) colliderPosition = this.circleCollider.getParent().transform
            .getPosition(new Vector2(circleCollider.x, circleCollider.y));
        renderer.setColor(Color.LIGHT_GRAY);
        renderer.circle(colliderPosition.x, colliderPosition.y, circleCollider.radius, 40);
        renderer.line(colliderPosition, colliderPosition.cpy()
            .add(new Vector2(0, circleCollider.radius).rotate(this.circleCollider.getParent().transform.getRotation() - 90)));
        renderer.set(ShapeType.Filled);
        boolean hoverable = Gdx.input.isButtonPressed(Buttons.LEFT) && isGrabbed || !Gdx.input.isButtonPressed(Buttons.LEFT);
        renderer.setColor(
            isGrabbed || mousePosition.dst(colliderPosition.x + circleCollider.radius, colliderPosition.y) < 1 && hoverable
                ? Color.YELLOW : Color.GRAY);
        renderer.circle(colliderPosition.x + circleCollider.radius, colliderPosition.y, circleCollider.radius * 0.05f, 20);
        renderer.setColor(Color.LIGHT_GRAY);
        renderer.setColor(
            isGrabbed || mousePosition.dst(colliderPosition.x, colliderPosition.y + circleCollider.radius) < 1 && hoverable
                ? Color.YELLOW : Color.GRAY);
        renderer.circle(colliderPosition.x, colliderPosition.y + circleCollider.radius, circleCollider.radius * 0.05f, 20);
        renderer.setColor(
            isGrabbed || mousePosition.dst(colliderPosition.x - circleCollider.radius, colliderPosition.y) < 1 && hoverable
                ? Color.YELLOW : Color.GRAY);
        renderer.circle(colliderPosition.x - circleCollider.radius, colliderPosition.y, circleCollider.radius * 0.05f, 20);
        renderer.setColor(
            isGrabbed || mousePosition.dst(colliderPosition.x, colliderPosition.y - circleCollider.radius) < 1 && hoverable
                ? Color.YELLOW : Color.GRAY);
        renderer.circle(colliderPosition.x, colliderPosition.y - circleCollider.radius, circleCollider.radius * 0.05f, 20);
        renderer.setColor(Color.GRAY);
        renderer.set(ShapeType.Line);
        Gdx.gl.glLineWidth(1.0f);
        renderer.end();
        renderer.begin(ShapeType.Line);
        renderer.setColor(
            isGrabbed || mousePosition.dst(colliderPosition.x, colliderPosition.y) < 1 && hoverable ? Color.YELLOW : Color.GRAY);
        float rotation = this.circleCollider.getParent().transform.getRotation();
        renderer.line(colliderPosition.cpy().add(new Vector2(0.5f, 0).rotate(rotation)),
            colliderPosition.cpy().add(new Vector2(-0.5f, 0).rotate(rotation)));
        renderer.line(colliderPosition.cpy().add(new Vector2(0, 0.5f).rotate(rotation)),
            colliderPosition.cpy().add(new Vector2(0, -0.5f).rotate(rotation)));
    }

    @Override
    public float input (int button, int eventtype) {
        switch (eventtype) {
            case EventType.MouseDown:
                grabPosition = this.circleCollider.getParent().transform.getPosition().sub(RavTech.input.getWorldPosition());
                Vector2 mousePosition = RavTech.input.getWorldPosition();
                if (mousePosition.dst(colliderPosition.x + circleCollider.radius, colliderPosition.y) < 1
                    || mousePosition.dst(colliderPosition.x - circleCollider.radius, colliderPosition.y) < 1
                    || mousePosition.dst(colliderPosition.x, colliderPosition.y + circleCollider.radius) < 1
                    || mousePosition.dst(colliderPosition.x, colliderPosition.y - circleCollider.radius) < 1) {
                    oldRadius = this.circleCollider.radius;
                    this.isGrabbed = true;
                    this.grabbedPosition = false;
                } else if (mousePosition.dst(colliderPosition.x, colliderPosition.y) < 1) {
                    oldPosition = colliderPosition.cpy();
                    this.isGrabbed = true;
                    this.grabbedPosition = true;
                }
                break;
            case EventType.MouseDrag:
                if (!isGrabbed) return -1f;
                Vector2 mouseposition = RavTech.input.getWorldPosition();
                if (grabPosition != null) if (!grabbedPosition)
                    circleCollider.radius = mouseposition.dst(colliderPosition);
                else
                    colliderPosition = mouseposition;
                break;
            case EventType.MouseUp:
                if (!this.isGrabbed) return -1f;
                this.isGrabbed = false;
                if (grabPosition != null) if (!grabbedPosition)
                    ChangeManager.addChangeable(new ModifyChangeable(this.circleCollider,
                        "Set Circle Collider circleCollider.radius", "circleCollider.radius", oldRadius, circleCollider.radius));
                else {
                    Vector2 mousePos = RavTech.input.getWorldPosition();
                    Vector2 newPosition = new Vector2(mousePos.x - this.circleCollider.getParent().transform.getPosition().x,
                        mousePos.y - this.circleCollider.getParent().transform.getPosition().y);
                    newPosition.rotate(-this.circleCollider.getParent().transform.getRotation());
                    ChangeManager.addChangeable(
                        new ModifyChangeable(this.circleCollider, "Set Circle Collider X", "x", oldPosition.x, newPosition.x));
                    ChangeManager.addChangeable(
                        new ModifyChangeable(this.circleCollider, "Set Circle Collider Y", "y", oldPosition.y, newPosition.y));
                }
                break;
        }
        return -1f;
    }

    @Override
    public boolean isInBoundingBox (Vector2 coord) {
        return false;
    }
}
