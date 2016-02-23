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
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class CircleCollider extends Box2dCollider implements Json.Serializable {

    @Override
    public ComponentType getType () {
        return ComponentType.CircleCollider;
    }

    @Override
    public String getName () {
        return getType().toString();
    }

    public float x, y;
    public float radius = 1f;

    public CircleCollider() {
    }

    public void setRadius (float radius) {
        this.radius = radius;
        apply();
    }

    public float getRadius () {
        return radius;
    }

    public void setPosition (float x, float y) {
        this.x = x;
        this.y = y;
        apply();
    }

    public Vector2 getPosition () {
        return new Vector2(x, y);
    }

    @Override
    public void apply () {
        Body body = ((Rigidbody)this.getParent().getComponentByType(ComponentType.Rigidbody)).getBody();
        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setPosition(new Vector2(x, y));
        shape.setRadius(this.radius);
        fixtureDef.density = this.density;
        fixtureDef.friction = this.friction;
        fixtureDef.isSensor = this.isSensor;
        fixtureDef.restitution = this.restitution;
        fixtureDef.shape = shape;
        if (fixture != null) {
            dispose();
            fixture = null;
            ((Rigidbody)this.getParent().getComponentByType(ComponentType.Rigidbody)).apply();
            rebuildAll();
            this.getParent().transform.setRotation(this.getParent().transform.getRotation());
            return;
        }
        fixture = body.createFixture(fixtureDef);
        fixture.setFilterData(filter);
        UserData userdata = new UserData();
        userdata.component = (Rigidbody)this.getParent().getComponentByName("Rigidbody");
        fixture.setUserData(userdata);
    }

    @Override
    public void load (@SuppressWarnings("rawtypes") Array<AssetDescriptor> dependencies) {
    }

    @Override
    public void finishedLoading () {
        apply();
    }

    @Override
    public void update () {
    }

    @Override
    public void draw (SpriteBatch batch) {
    }

    @Override
    public void write (Json json) {
        super.write(json);
        json.writeValue("x", x);
        json.writeValue("y", y);
        json.writeValue("radius", radius);
    }

    @Override
    public void read (Json json, JsonValue jsonData) {
        super.read(json, jsonData);
        setRadius(jsonData.getFloat("radius"));
        setPosition(jsonData.getFloat("x"), jsonData.getFloat("y"));
    }

    @Override
    public void setVariable (int variableID, Object value) {
        if (variableID <= 6) super.setVariable(variableID, value);
        String valueString = String.valueOf(value);
        switch (variableID) {
            case 7:
                setPosition(Float.valueOf(valueString), y);
            case 8:
                setPosition(x, Float.valueOf(valueString));
                break;
            case 9:
                setRadius(Float.valueOf(valueString));
        }
    }

    @Override
    public int getVariableId (String variableName) {
        int superId = super.getVariableId(variableName);
        if (superId != -1) return superId;
        switch (variableName) {
            case "x":
                return 7;
            case "y":
                return 8;
            case "radius":
                return 9;
        }
        return -1;
    }

    @Override
    public Object getVariable (int variableID) {
        if (variableID <= 6)
            return super.getVariable(variableID);
        else
            switch (variableID) {
                case 7:
                    return x;
                case 8:
                    return y;
                case 9:
                    return radius;
            }
        return null;
    }

    @Override
    public String[] getVariableNames () {
        return null;
    }

    @Override
    public Object[] getValiables () {
        return new Object[] {density, filter.categoryBits, filter.maskBits, filter.maskBits, friction, isSensor, restitution, x,
            y, radius};
    }
}
