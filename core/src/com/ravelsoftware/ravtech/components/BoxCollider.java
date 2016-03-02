
package com.ravelsoftware.ravtech.components;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class BoxCollider extends Box2dCollider {

	@Override
	public ComponentType getType () {
		return ComponentType.BoxCollider;
	}

	@Override
	public String getName () {
		return getType().toString();
	}

	public float x, y;
	public float width;
	public float height;
	public float angle;

	public BoxCollider () {
		this.width = 0.01f;
		this.height = 0.01f;
	}

	public BoxCollider (float width, float height) {
		this.width = width;
		this.height = height;
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
	public void dispose () {
	}

	public void setPosition (float x, float y) {
		this.x = x;
		this.y = y;
		apply();
	}

	public Vector2 getPosition () {
		return new Vector2(x, y);
	}

	public void setBounds (float width, float height) {
		width = Math.max(width, 0.01f);
		height = Math.max(height, 0.01f);
		this.width = width;
		this.height = height;
	}

	public Vector2 getBounds () {
		return new Vector2(width, height);
	}

	public void setAngle (float angle) {
		this.angle = angle;
	}

	public float getAngle () {
		return angle;
	}

	@Override
	public void apply () {
		Body body = ((Rigidbody)this.getParent().getComponentByType(ComponentType.Rigidbody)).getBody();
		FixtureDef fixtureDef = new FixtureDef();
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width, height, new Vector2(x, y), (float)Math.toRadians(angle));
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
			return;
		}
		fixture = body.createFixture(fixtureDef);
		fixture.setFilterData(filter);
		UserData userdata = new UserData();
		userdata.component = (Rigidbody)this.getParent().getComponentByType(ComponentType.Rigidbody);
		fixture.setUserData(userdata);
	}

	@Override
	public void write (Json json) {
		super.write(json);
		json.writeValue("x", x);
		json.writeValue("y", y);
		json.writeValue("width", width);
		json.writeValue("height", height);
		json.writeValue("angle", angle);
	}

	@Override
	public void read (Json json, JsonValue jsonData) {
		super.read(json, jsonData);
		x = jsonData.getFloat("x");
		y = jsonData.getFloat("y");
		width = jsonData.getFloat("width");
		height = jsonData.getFloat("height");
		angle = jsonData.getFloat("angle");
		apply();
	}

	@Override
	public void setVariable (int variableID, Object value) {
		if (variableID <= 6) super.setVariable(variableID, value);
		String valueString = String.valueOf(value);
		switch (variableID) {
		case 7:
			setPosition(Float.valueOf(valueString), y);
			break;
		case 8:
			setPosition(x, Float.valueOf(valueString));
			break;
		case 9:
			setBounds(Float.valueOf(valueString), height);
			apply();
			break;
		case 10:
			setBounds(width, Float.valueOf(valueString));
			apply();
			break;
		case 11:
			setAngle(Float.valueOf(valueString));
			break;
		}
	}

	@Override
	public int getVariableId (String variableName) {
		int variableId = super.getVariableId(variableName);
		if (variableId == -1) {
			switch (variableName) {
			case "x":
				return 7;
			case "y":
				return 8;
			case "width":
				return 9;
			case "height":
				return 10;
			case "angle":
				return 11;
			}
			return -1;
		} else
			return variableId;
	}

	@Override
	public Object getVariable (int variableID) {
		if (variableID <= 6) return super.getVariable(variableID);
		switch (variableID) {
		case 7:
			return x;
		case 8:
			return y;
		case 9:
			return width;
		case 10:
			return height;
		case 11:
			return angle;
		}
		return null;
	}

	@Override
	public String[] getVariableNames () {
		return null;
	}

	@Override
	public Object[] getValiables () {
		return new Object[] {density, filter.categoryBits, filter.maskBits, filter.maskBits, friction, isSensor, restitution, x, y,
			width, height, angle};
	}
}
