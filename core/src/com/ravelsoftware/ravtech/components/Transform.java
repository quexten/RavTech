
package com.ravelsoftware.ravtech.components;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ravelsoftware.ravtech.animation.VariableAccessor;

public class Transform extends GameComponent implements Json.Serializable, VariableAccessor {

	private Vector2 position = new Vector2();
	private Vector2 scale = new Vector2(1, 1);
	private float rotation;
	private Affine2 affine = new Affine2();
	private Affine2 invertedAffine = new Affine2();

	private Vector2 tempPosition = new Vector2();

	@Override
	public ComponentType getType () {
		return ComponentType.Transform;
	}

	@Override
	public String getName () {
		return getType().toString();
	}

	public Transform (GameObject parent) {
		setParent(parent);
	}

	public Transform (GameObject parent, float x, float y, float rot) {
		this(parent);
		setLocalPosition(x, y);
		setLocalRotation(rot);
	}

	@Override
	public void load (@SuppressWarnings("rawtypes") Array<AssetDescriptor> dependencies) {
	}

	@Override
	public void finishedLoading () {
		this.updatePosition();
	}

	@Override
	public void update () {
	}

	@Override
	public void draw (SpriteBatch batch) {
		updatePosition();
	}

	@Override
	public void dispose () {
	}

	private void updatePosition () {
		affine.setToTrnRotScl(this.position, this.rotation, this.scale);
		if (getParent().getParent() != null)
			affine.preMul(getParent().getParent().transform.affine);

		if (this.scale.x != 0 && this.scale.y != 0)
			invertedAffine.setToTrnRotScl(this.position, this.rotation, this.scale).inv();

		if (getParent().getParent() != null)
			invertedAffine.preMul(getParent().getParent().transform.invertedAffine);

		Array<GameComponent> components = getParent().getComponentsByType(ComponentType.GameObject);
		for (int i = 0; i < components.size; i++)
			((GameObject)components.get(i)).transform.updatePosition();
	}

	/** Sets the absolute position of the transform.
	 * @param x - the x component of the position
	 * @param y - the y component of the position */
	public void setPosition (float x, float y) {
		if (getParent().getParent() != null) {
			tempPosition.set(x, y);
			getParent().getParent().transform.invertedAffine.applyTo(tempPosition);
			setLocalPosition(tempPosition);
		} else
			setLocalPosition(x, y);
	}

	/** Sets the absolute position of the transform.
	 * @param position - the position */
	public void setPosition (Vector2 position) {
		setPosition(position.x, position.y);
	}

	/** Calculates the absolute position of the transform.
	 * @return - the position */
	public Vector2 getPosition () {
		if (getParent().getParent() != null) {
			tempPosition.set(position);
			getParent().getParent().transform.affine.applyTo(tempPosition);
			return tempPosition;
		} else
			return position;
	}

	/** Sets the local position of the transform.
	 * @param x - the x component of the position
	 * @param y - the y component of the position */
	public void setLocalPosition (float x, float y) {
		position.set(x, y);
		updatePosition();
		for (GameComponent component : getParent().getComponentsInChildren(ComponentType.Rigidbody))
			((Rigidbody)component).getBody().setTransform(component.getParent().transform.getPosition(),
				((Rigidbody)component).getBody().getAngle());
	}

	/** Sets the local position of the transform.
	 * @param position - the position */
	public void setLocalPosition (Vector2 position) {
		setLocalPosition(position.x, position.y);
	}

	/** Calculates the local position of the transform.
	 * @return - the position */
	public Vector2 getLocalPosition () {
		return position;
	}

	/** Sets the absolute rotation of the transform.
	 * @param rotation - the rotation */
	public void setRotation (float rotation) {
		setLocalRotation(rotation - (getParent().getParent() != null ? getParent().getParent().transform.getRotation() : 0));
	}

	/** Calculates the absolute rotation of the transform.
	 * @return - the absolute Rotation */
	public float getRotation () {
		float rotation = getParent().getParent() != null ? getParent().getParent().transform.getRotation() + getLocalRotation()
			: getLocalRotation();
		return rotation;
	}

	/** Sets the local rotation of the transform.
	 * @param rotation - the rotation */
	public void setLocalRotation (float rotation) {
		this.rotation = rotation;
		updatePosition();
		for (int i = 0; i < getParent().getComponents().size; i++) {
			GameComponent component = getParent().getComponents().get(i);
			if (component instanceof Rigidbody)
				((Rigidbody)component).getBody().setTransform(((Rigidbody)component).getBody().getPosition(),
					(float)Math.toRadians(rotation));
		}
	}

	/** Calculates the local rotation of the transform.
	 * @return - the local rotation of the transform */
	public float getLocalRotation () {
		return rotation;
	}

	/** Sets the local scale
	 * @param x - the scale along the x axis
	 * @param y - the scale along the y axis */
	void setLocalScale (float x, float y) {
		scale.set(x, y);
		updatePosition();
	}

	/** gets the Local Scale */
	Vector2 getLocalScale () {
		return scale;
	}

	/** rotates the transform towards the given point
	 * @param target - the point the transform is rotated towards */
	public void rotateTo (Vector2 toPoint) {
		Vector2 temp = getPosition().sub(toPoint);
		setRotation((float)Math.toDegrees(Math.atan2(temp.y, temp.x)) + 180);
	}

	/** gets absolute position derrived from coordinates in local coordinate system from transform
	 * @param localPosition - the position in the local coordinate system
	 * @return - the absolute position of the specified point */
	public Vector2 getPosition (Vector2 localPosition) {
		return localPosition.rotate(getRotation()).add(this.getPosition());
	}

	@Override
	public void write (Json json) {
		json.writeValue("x", position.x);
		json.writeValue("y", position.y);
		json.writeValue("rotation", rotation);
		json.writeValue("scaleX", scale.x);
		json.writeValue("scaleY", scale.y);
	}

	@Override
	public void read (Json json, JsonValue jsonData) {
		this.setLocalPosition(jsonData.has("x") ? jsonData.getFloat("x") : 0, jsonData.has("y") ? jsonData.getFloat("y") : 0);
		setLocalRotation(
			jsonData.has("rotation") ? jsonData.getFloat("rotation") : jsonData.has("rotation") ? jsonData.getFloat("rotation") : 0);
		setLocalScale(jsonData.has("scaleX") ? jsonData.getFloat("scaleX") : 1,
			jsonData.has("scaleY") ? jsonData.getFloat("scaleY") : 1);
	}

	@Override
	public String[] getVariableNames () {
		return new String[] {"x", "y", "rotation", "scaleX", "scaleY"};
	}

	@Override
	public void setVariable (int variableId, Object value) {
		if (variableId == 0)
			setLocalPosition(Float.valueOf(value.toString()), position.y);
		else if (variableId == 1)
			setLocalPosition(position.x, Float.valueOf(value.toString()));
		else if (variableId == 2)
			setLocalRotation((Float)value);
		else if (variableId == 3)
			setLocalScale((Float)value, scale.y);
		else if (variableId == 4)
			setLocalScale(scale.x, ((Float)value));
	}

	@Override
	public Object getVariable (int variableId) {
		if (variableId == 0)
			return position.x;
		else if (variableId == 1)
			return position.y;
		else if (variableId == 2)
			return rotation;
		else if (variableId == 3)
			return scale.x;
		else if (variableId == 4)
			return scale.y;
		else
			return 0;
	}

	@Override
	public int getVariableId (String variableName) {
		if (variableName.equals("x"))
			return 0;
		else if (variableName.equals("y"))
			return 1;
		else if (variableName.equals("rotation"))
			return 2;
		else if (variableName.equals("scaleX"))
			return 3;
		else if (variableName.equals("scaleY"))
			return 4;
		else
			return -1;
	}

	@Override
	public Object[] getValiables () {
		return null;
	}
}
