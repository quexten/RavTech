
package com.ravelsoftware.ravtech.components;

import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public abstract class Box2dCollider extends GameComponent implements Json.Serializable {

	float density = 1;
	Filter filter;
	float friction = 1;
	boolean isSensor = false;
	float restitution = 0.0f;
	Fixture fixture;
	// Gizmovariables
	public boolean canEdit = false;

	public Box2dCollider () {
		filter = new Filter();
	}

	/** adds the Collider to the rigidbody */
	public abstract void apply ();

	public void rebuildAll () {
		for (int i = 0; i < getParent().getComponents().size; i++) {
			GameComponent component = getParent().getComponents().get(i);
			if (component instanceof Box2dCollider) {
				Box2dCollider collider = (Box2dCollider)component;
				collider.fixture = null;
				// if (collider instanceof PolygonCollider) ((PolygonCollider)collider).fixtures.clear();
				collider.apply();
			}
		}
	}

	@Override
	public void dispose () {
		UserData userData = new UserData();
		userData.isFlaggedForDelete = true;
		if (fixture != null)
			fixture.setUserData(userData);
	}

	public float getDensity () {
		return density;
	}

	public float getFriction () {
		return friction;
	}

	public boolean isSensor () {
		return isSensor;
	}

	public float getRestitution () {
		return restitution;
	}

	@Override
	public void write (Json json) {
		json.writeValue("density", density);
		json.writeObjectStart("filter");
		json.writeValue("categoryBits", filter.categoryBits);
		json.writeValue("maskBits", filter.maskBits);
		json.writeValue("groupIndex", filter.groupIndex);
		json.writeObjectEnd();
		json.writeValue("friction", friction);
		json.writeValue("isSensor", isSensor);
		json.writeValue("restitution", restitution);
	}

	@Override
	public void read (Json json, JsonValue jsonData) {
		density = jsonData.getFloat("density");
		if (filter != null)
			filter = new Filter();
		if (jsonData.has("filter")) {
			JsonValue colordata = jsonData.getChild("filter");
			boolean hasnext = true;
			while (hasnext) {
				if (colordata.name().equals("categoryBits"))
					filter.categoryBits = colordata.asShort();
				else if (colordata.name().equals("maskBits"))
					filter.maskBits = colordata.asShort();
				else if (colordata.name().equals("groupIndex"))
					filter.groupIndex = colordata.asShort();
				colordata = colordata.next();
				hasnext = colordata != null;
			}
		}
		friction = jsonData.getFloat("friction");
		isSensor = jsonData.getBoolean("isSensor");
		restitution = jsonData.getFloat("restitution");
	}

	@Override
	public void setVariable (int variableID, Object value) {
		String valueString = String.valueOf(value);
		switch (variableID) {
		case 0:
			density = Float.valueOf(valueString);
			break;
		case 1:
			filter.categoryBits = Short.valueOf(valueString);
			break;
		case 2:
			filter.maskBits = Short.valueOf(valueString);
			break;
		case 3:
			filter.groupIndex = Short.valueOf(valueString);
			break;
		case 4:
			friction = Float.valueOf(valueString);
			break;
		case 5:
			isSensor = Boolean.valueOf(valueString);
			break;
		case 6:
			restitution = Float.valueOf(valueString);
			break;
		}
	}

	@Override
	public int getVariableId (String variableName) {
		switch (variableName) {
		case "density":
			return 0;
		case "categoryBits":
			return 1;
		case "maskBits":
			return 2;
		case "groupIndex":
			return 3;
		case "friction":
			return 4;
		case "isSensor":
			return 5;
		case "restitution":
			return 6;
		}
		return -1;
	}

	@Override
	public Object getVariable (int variableID) {
		return getValiables()[variableID];
	}

	@Override
	public String[] getVariableNames () {
		return new String[] {"density", "categoryBits", "maskBits", "groupIndex", "friction", "isSensor", "restitution"};
	}

	@Override
	public Object[] getValiables () {
		return new Object[] {density, filter.categoryBits, filter.maskBits, filter.maskBits, friction, isSensor, restitution};
	}
}
