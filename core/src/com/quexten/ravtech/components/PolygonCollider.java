
package com.quexten.ravtech.components;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class PolygonCollider extends Box2dCollider {

	@Override
	public ComponentType getType () {
		return ComponentType.PolygonCollider;
	}

	public Array<Vector2> vertecies = new Array<Vector2>();
	Array<Fixture> fixtures = new Array<Fixture>();

	public PolygonCollider () {
	}

	@Override
	public void apply () {
		Body body = ((Rigidbody)getParent()
			.getComponentByType(ComponentType.Rigidbody)).getBody();
		FixtureDef fixtureDef = new FixtureDef();
		PolygonShape shape = new PolygonShape();
		fixture.setDensity(1);
		fixture.setDensity(density);
		shape.set((Vector2[])vertecies.toArray(Vector2.class));
		fixtureDef.density = 4;
		fixtureDef.friction = friction;
		fixtureDef.isSensor = isSensor;
		fixtureDef.restitution = restitution;
		fixtureDef.shape = shape;
		if (fixtures.size > 0) {
			dispose();
			fixtures.clear();
			((Rigidbody)getParent()
				.getComponentByType(ComponentType.Rigidbody)).apply();
			rebuildAll();
			return;
		}
		B2DSeparator.separate(body, fixtureDef,
			(Vector2[])vertecies.toArray(Vector2.class), this);
		fixture = body.createFixture(fixtureDef);
		fixture.setFilterData(filter);
	}

	public void setVertecies (Vector2[] vertecies) {
		this.vertecies.clear();
		for (int i = 0; i < vertecies.length; i++)
			this.vertecies.add(vertecies[i]);
		apply();
	}

	@Override
	public void dispose () {
		super.dispose();
		UserData userData = new UserData();
		userData.isFlaggedForDelete = true;
		if (fixtures.size > 0)
			fixtures.get(0).setUserData(userData);
	}

	@Override
	public void write (Json json) {
		super.write(json);
		json.writeValue("vertecies", vertecies);
	}

	@Override
	public void read (Json json, JsonValue jsonData) {
		super.read(json, jsonData);
		vertecies.clear();
		JsonValue next = jsonData.get("vertecies").child;
		while (next != null) {
			// this.overrideVariables.put(next.name, next.asString());
			vertecies
				.add(new Vector2(next.has("x") ? next.getFloat("x") : 0,
					next.has("y") ? next.getFloat("y") : 0));
			next = next.next();
		}
		apply();
	}

	@Override
	public void setVariable (int variableID, Object value) {
		if (variableID <= 6)
			super.setVariable(variableID, value);
	}

	@Override
	public int getVariableId (String variableName) {
		return -1;
	}

	@Override
	public Object getVariable (int variableID) {
		return null;
	}

	@Override
	public String[] getVariableNames () {
		return null;
	}

	@Override
	public Object[] getValiables () {
		return null;
	}

	@Override
	public String getName () {
		return getType().toString();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void load (Array<AssetDescriptor> dependencies) {
	}

	@Override
	public void finishedLoading () {
	}

	@Override
	public void update () {
	}

	@Override
	public void draw (SpriteBatch batch) {
	}
}
