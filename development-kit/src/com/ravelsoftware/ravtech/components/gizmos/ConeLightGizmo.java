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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.components.Light;
import com.ravelsoftware.ravtech.util.EventType;
import com.ravelsoftware.ravtech.util.GeometryUtils;

import box2dLight.ConeLight;

public class ConeLightGizmo extends Gizmo {

    Light light;
    ConeLight coneLight;
    boolean raySelected;

    public ConeLightGizmo(Light light) {
        this.light = light;
        coneLight = (ConeLight)light.light;
    }

    @Override
    public void draw (ShapeRenderer renderer, boolean selected) {
        renderer.setColor(selected && !raySelected ? Color.YELLOW : Color.CYAN);
        this.drawCone(renderer);
        renderer.setColor(selected && raySelected ? Color.YELLOW : Color.CYAN);
        this.drawRay(renderer, coneLight.getConeDegree());
        this.drawRay(renderer, -coneLight.getConeDegree());
    }

    private void drawCone (ShapeRenderer renderer) {
        Vector2 origin = new Vector2(light.getParent().transform.getPosition().x, light.getParent().transform.getPosition().y);
        int indexcount = 360;
        float[] indicies = new float[indexcount * 2];
        for (int i = 0; i < indexcount; i++) {
            float offset = coneLight.getConeDegree() * 2 * (i / (indexcount - 1f) - 0.5f);
            Vector2 endpoint = new Vector2(
                origin.x + (float)Math.cos(Math.toRadians(light.getParent().transform.getRotation() + offset))
                    * coneLight.getDistance() * 0.5f,
                origin.y + (float)Math.sin(Math.toRadians(light.getParent().transform.getRotation() + offset))
                    * coneLight.getDistance() * 0.5f);
            indicies[2 * i] = endpoint.x;
            indicies[2 * i + 1] = endpoint.y;
        }
        renderer.polyline(indicies);
    }

    private void drawRay (ShapeRenderer renderer, float degrees) {
        Vector2 origin = new Vector2(light.getParent().transform.getPosition().x, light.getParent().transform.getPosition().y);
        renderer.line(origin, getRayEndpoint(origin, degrees));
    }

    private Vector2 getRayEndpoint (Vector2 origin, float degrees) {
        return new Vector2(
            origin.x + (float)Math.cos(Math.toRadians(light.getParent().transform.getRotation() + degrees))
                * coneLight.getDistance() * 0.5f,
            origin.y + (float)Math.sin(Math.toRadians(light.getParent().transform.getRotation() + degrees))
                * coneLight.getDistance() * 0.5f);
    }

    @Override
    public float input (int button, int eventType) {
        Vector2 origin = new Vector2(light.getParent().transform.getPosition().x, light.getParent().transform.getPosition().y);
        switch (eventType) {
            case EventType.MouseMoved:
                float coneDst = Math.abs(origin.dst(RavTech.input.getWorldPosition()) - coneLight.getDistance() / 2f);
                float rayDst = GeometryUtils.dstFromLine(origin, getRayEndpoint(origin, light.angle),
                    RavTech.input.getWorldPosition());
                float rayDst2 = GeometryUtils.dstFromLine(origin, getRayEndpoint(origin, -light.angle),
                    RavTech.input.getWorldPosition());
                if (rayDst > rayDst2) rayDst = rayDst2;
                if (coneDst < rayDst && coneDst < 1f) {
                    raySelected = false;
                    return coneDst;
                } else if (rayDst < coneDst && rayDst < 1f) {
                    raySelected = true;
                    return rayDst;
                } else
                    return -1f;
            case EventType.MouseDown:
                break;
            case EventType.MouseDrag:
                if (!raySelected)
                    light.setVariable(1, origin.dst(RavTech.input.getWorldPosition()) * 2);
                else {
                    float angle = RavTech.input.getWorldPosition().sub(origin).angle();
                    float rotation = light.getParent().transform.getRotation();
                    angle = angle - rotation;
                    if (angle < 0) angle += 360;
                    angle = angle > 180 ? 180 - Math.abs(angle - 180) : angle;
                    light.setVariable(0, angle);
                }
                break;
            case EventType.MouseUp:
                break;
        }
        return -1f;
    }

    @Override
    public boolean isInBoundingBox (Vector2 coord) {
        return false;
    }
}
