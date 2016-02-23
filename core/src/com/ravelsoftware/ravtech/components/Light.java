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
package com.ravelsoftware.ravtech.components;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.util.JsonUtil;

import box2dLight.ConeLight;

public class Light extends Renderer implements Json.Serializable {

    @Override
    public ComponentType getType () {
        return ComponentType.Light;
    }

    @Override
    public String getName () {
        return this.getType().toString();
    }

    public box2dLight.Light light;
    public float angle;
    public float distance;
    public Color color;

    @Override
    public void draw (SpriteBatch batch) {
        light.setPosition(getParent().transform.getPosition().x, getParent().transform.getPosition().y);
        light.setDirection(getParent().transform.getRotation());
        if (!light.getColor().equals(color)) light.setColor(color);
        light.setDistance(distance * Math.abs(getParent().transform.flippedX ? 1 : -1));
        if (light instanceof ConeLight) ((ConeLight)light).setConeDegree(angle);
    }

    @Override
    public void dispose () {
        light.setActive(false);
    }

    public Light() {
        this.color = Color.YELLOW;
        this.distance = 50;
        this.angle = 45;
    }

    @Override
    public void write (Json json) {
        json.writeObjectStart("color");
        json.writeValue("r", light.getColor().r);
        json.writeValue("g", light.getColor().g);
        json.writeValue("b", light.getColor().b);
        json.writeValue("a", light.getColor().a);
        json.writeObjectEnd();
        json.writeValue("distance", light.getDistance());
        json.writeValue("angle", ((ConeLight)light).getConeDegree());
    }

    @Override
    public void read (Json json, JsonValue jsonData) {
        Color color = JsonUtil.readColorFromJson(jsonData, "color");
        this.color = color;
        this.distance = jsonData.getFloat("distance");
        this.angle = jsonData.getFloat("angle");
    }

    @Override
    public String[] getVariableNames () {
        return new String[] {"angle", "distance", "color"};
    }

    @Override
    public void setVariable (int variableID, Object value) {
        switch (variableID) {
            case 0:
                angle = (Float)value;
                break;
            case 1:
                distance = (Float)value;
                break;
            case 2:
                color = (Color)value;
                break;
        }
    }

    @Override
    public int getVariableId (String variableName) {
        if (variableName.equals("angle"))
            return 0;
        else if (variableName.equals("distance"))
            return 1;
        else if (variableName.equals("color"))
            return 2;
        else
            return -1;
    }

    @Override
    public Object getVariable (int variableID) {
        switch (variableID) {
            case 0:
                return angle;
            case 1:
                return distance;
            case 2:
                return color;
        }
        return null;
    }

    @Override
    public Object[] getValiables () {
        return null;
    }

    @Override
    public void load (@SuppressWarnings("rawtypes") Array<AssetDescriptor> dependencies) {
    }

    @Override
    public void finishedLoading () {
        light = new box2dLight.ConeLight(RavTech.sceneHandler.lightHandler, 60, Color.YELLOW, 50,
            getParent().transform.getPosition().x, getParent().transform.getPosition().y, getParent().transform.getRotation(),
            45);
        light.setPosition(0, 0);
        light.setColor(this.color);
        light.setDistance(this.distance);
        ((ConeLight)light).setConeDegree(this.angle);
    }

    @Override
    public void update () {
    }
}
