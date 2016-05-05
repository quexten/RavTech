
package com.quexten.ravtech.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.quexten.ravtech.RavTech;

public class Rigidbody extends GameComponent
	implements Json.Serializable {

	@Override
	public ComponentType getType () {
		return ComponentType.Rigidbody;
	}

	public String getName () {
		return getType().toString();
	}

	Body body;

	public Rigidbody () {
		body = RavTech.sceneHandler.box2DWorld
			.createBody(new BodyDef());
		body.setUserData(new UserData());
	}

	@Override
	public void load (
		@SuppressWarnings("rawtypes") Array<AssetDescriptor> dependencies) {
		apply();
	}

	@Override
	public void finishedLoading () {
	}

	@Override
	public void update () {
		if (body.getType() != BodyType.StaticBody) {
			getParent().transform.setPosition(body.getPosition().x,
				body.getPosition().y);
			getParent().transform
				.setRotation((float)Math.toDegrees(body.getAngle()));
		}
	}

	@Override
	public void draw (SpriteBatch batch) {
	}

	@Override
	public void dispose () {
	}

	public void apply () {
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(getParent().transform.getPosition());
		bodyDef.angle = (float)Math
			.toRadians(getParent().transform.getRotation());
		if (body != null) {
			bodyDef.angularDamping = body.getAngularDamping();
			bodyDef.bullet = body.isBullet();
			bodyDef.fixedRotation = false;
			bodyDef.gravityScale = body.getGravityScale();
			bodyDef.linearDamping = body.getLinearDamping();
			bodyDef.type = getBody().getType();
			((UserData)body.getUserData()).isFlaggedForDelete = true;
		}
		body = RavTech.sceneHandler.box2DWorld.createBody(bodyDef);
		body.setUserData(new UserData());
	}

	public void onCollisionEnter (final Fixture other,
		final Contact contact) {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run () {
				if (getParent().getComponentByType(
					ComponentType.ScriptComponent) != null)
					((ScriptComponent)getParent()
						.getComponentByType(ComponentType.ScriptComponent))
							.callFunction("onCollisionEnter",
								new Object[] {other, contact});
			}
		});
	}

	public void onCollisionExit (final Fixture other,
		final Contact contact) {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run () {
				if (getParent().getComponentByType(
					ComponentType.ScriptComponent) != null)
					((ScriptComponent)getParent()
						.getComponentByType(ComponentType.ScriptComponent))
							.callFunction("onCollisionExit",
								new Object[] {other, contact});
			}
		});
	}

	public Body getBody () {
		return body;
	}

	@Override
	public void write (Json json) {
		super.write(json);
		String[] variables = getVariableNames();
		for (int i = 0; i < variables.length; i++)
			json.writeValue(variables[i], getVariable(i));
	}

	@Override
	public void read (Json json, JsonValue jsonData) {
		super.read(json, jsonData);
		String[] variables = getVariableNames();
		for (int i = 0; i < variables.length; i++)
			if (jsonData.has(variables[i]))
				setVariable(i, jsonData.getString(variables[i]));
	}

	@Override
	public void setVariable (int variableID, Object value) {
		switch (variableID) {
			case 0:
				getBody().setAngularDamping(
					Float.valueOf(String.valueOf(value)));
				break;
			case 1:
				getBody()
					.setBullet(Boolean.valueOf(String.valueOf(value)));
				break;
			case 2:
				getBody().setFixedRotation(
					Boolean.valueOf(String.valueOf(value)));
				break;
			case 3:
				getBody()
					.setGravityScale(Float.valueOf(String.valueOf(value)));
				break;
			case 4:
				getBody().setLinearDamping(
					Float.valueOf(String.valueOf(value)));
				break;
			case 5:
				getBody().setType(
					value.equals("StaticBody") || value.equals("Static")
						? BodyType.StaticBody
						: value.equals("DynamicBody")
							|| value.equals("Dynamic")
								? BodyType.DynamicBody
								: value.equals("KinematicBody")
									|| value.equals("Kinematic")
										? BodyType.KinematicBody
										: (BodyType)value);
		}
	}

	@Override
	public int getVariableId (String variableName) {
		switch (variableName) {
			case "angularDamping":
				return 0;
			case "bullet":
				return 1;
			case "fixedRotation":
				return 2;
			case "gravityScale":
				return 3;
			case "linearDamping":
				return 4;
			case "bodyType":
				return 5;
		}
		return -1;
	}

	@Override
	public Object getVariable (int variableID) {
		switch (variableID) {
			case 0:
				return body.getAngularDamping();
			case 1:
				return body.isBullet();
			case 2:
				return body.isFixedRotation();
			case 3:
				return body.getGravityScale();
			case 4:
				return body.getLinearDamping();
			case 5:
				return body.getType();
		}
		return null;
	}

	@Override
	public String[] getVariableNames () {
		return new String[] {"angularDamping", "bullet",
			"fixedRotation", "gravityScale", "linearDamping",
			"bodyType"};
	}

	@Override
	public Object[] getValiables () {
		return null;
	}
}
