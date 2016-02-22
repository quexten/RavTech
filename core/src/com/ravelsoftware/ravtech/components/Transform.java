package com.ravelsoftware.ravtech.components;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ravelsoftware.ravtech.animation.VariableAccessor;

public class Transform extends GameComponent implements Json.Serializable, VariableAccessor {

    @Override
    public ComponentType getType () {
        return ComponentType.Transform;
    }

    @Override
    public String getName () {
        return getType().toString();
    }

    private Vector2 position = new Vector2();
    private Vector2 absolutePosition = new Vector2();
    public boolean flippedX;
    public boolean flippedY;
    float rotation;

    public Transform(GameObject parent) {
        this.setParent(parent);
    }

    public Transform(GameObject parent, float x, float y, float rot) {
        this(parent);
        this.setLocalPosition(x, y);
        this.setLocalRotation(rot);
    }

    @Override
    public void load (@SuppressWarnings("rawtypes") Array<AssetDescriptor> dependencies) {
    }

    @Override
    public void finishedLoading () {
    }

    @Override
    public void update () {
    }

    @Override
    public void draw (SpriteBatch batch) {
        this.updatePosition();
    }

    @Override
    public void dispose () {
    }

    private void updatePosition () {
        absolutePosition = (getParent().getParent() != null ? getParent().getParent().transform.getPosition() : new Vector2())
            .add(getParent().getParent() != null
                ? getLocalPosition().cpy()
                    .scl(new Vector2(getParent().getParent().transform.flippedY ? -1 : 1,
                        getParent().getParent().transform.isFlippedX() ? -1 : 1))
                .mul(new Matrix3().setToRotation(new Vector3(0, 0, 1f), -getParent().getParent().transform.getRotation()))
                : getLocalPosition().cpy());
        Array<GameComponent> components = getParent().getComponentsByType(ComponentType.GameObject);
        for (int i = 0; i < components.size; i++)
            ((GameObject)components.get(i)).transform.updatePosition();
    }

    /** Sets the absolute position of the transform.
     * @param x - the x component of the position
     * @param y - the y component of the position */
    public void setPosition (float x, float y) {
        if (getParent().getParent() != null) {
            Vector2 vec = new Vector2(x, y).sub(getParent().getParent().transform.getPosition())
                .mul(new Matrix3().setToRotation(new Vector3(0, 0, 1f), getParent().getParent().transform.getRotation()));
            setLocalPosition(vec);
        } else
            setLocalPosition(x, y);
    }

    /** Sets the absolute position of the transform.
     * @param position - the position */
    public void setPosition (Vector2 position) {
        this.setPosition(position.x, position.y);
    }

    /** Calculates the absolute position of the transform.
     * @return - the position */
    public Vector2 getPosition () {
        return absolutePosition.cpy();
    }

    /** Sets the local position of the transform.
     * @param x - the x component of the position
     * @param y - the y component of the position */
    public void setLocalPosition (float x, float y) {
        this.position.set(x, y);
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
        float rotation = getParent().getParent() != null
            ? getParent().getParent().transform.getRotation()
                + (!getParent().getParent().transform.isFlippedX() ? getLocalRotation() : -getLocalRotation())
            : getLocalRotation();
        return !flippedX ? rotation : 180 - rotation;
    }

    /** Sets the local rotation of the transform.
     * @param rotation - the rotation */
    public void setLocalRotation (float rotation) {
        this.rotation = rotation;
        for (int i = 0; i < getParent().getComponents().size; i++) {
            GameComponent component = getParent().getComponents().get(i);
            if (component instanceof Rigidbody) ((Rigidbody)component).getBody()
                .setTransform(((Rigidbody)component).getBody().getPosition(), (float)Math.toRadians(rotation));
        }
    }

    /** Calculates the local rotation of the transform.
     * @return - the local rotation of the transform */
    public float getLocalRotation () {
        return rotation;
    }

    public void setFlipped (boolean flippedX, boolean flippedY) {
        setFlippedX(flippedX);
        setFlippedY(flippedY);
    }

    public void setFlippedX (boolean flipped) {
        flippedX = flipped;
        setLocalPosition(position.x, position.y);
    }

    public void setFlippedY (boolean flipped) {
        flippedY = flipped;
        setLocalPosition(position.x, position.y);
    }

    public boolean isFlippedX () {
        return this.getParent().getParent() == null ? flippedX : this.getParent().getParent().transform.isFlippedX() != flippedX;
    }

    public boolean isFlippedY () {
        return flippedY;
    }

    /** rotates the transform towards the given point
     * @param target - the point the transform is rotated towards */
    public void rotateTo (Vector2 toPoint) {
        Vector2 temp = getPosition().sub(toPoint);
        this.setRotation((float)Math.toDegrees(Math.atan2(temp.y, temp.x)) + 180);
    }

    /** gets absolute position derrived from coordinates in local coordinate system from transform
     * @param localPosition - the position in the local coordinate system
     * @return - the absolute position of the specified point */
    public Vector2 getPosition (Vector2 localPosition) {
        return localPosition.rotate(this.getRotation()).add(this.getPosition());
    }

    @Override
    public void write (Json json) {
        json.writeValue("x", position.x);
        json.writeValue("y", position.y);
        json.writeValue("rotation", rotation);
        json.writeValue("flippedX", flippedX);
        json.writeValue("flippedY", flippedY);
    }

    @Override
    public void read (Json json, JsonValue jsonData) {
        this.setLocalPosition(jsonData.has("x") ? jsonData.getFloat("x") : 0, jsonData.has("y") ? jsonData.getFloat("y") : 0);
        this.setLocalRotation(
            jsonData.has("rot") ? jsonData.getFloat("rot") : jsonData.has("rotation") ? jsonData.getFloat("rotation") : 0);
        setFlippedX(jsonData.has("flippedX") ? jsonData.getBoolean("flippedX") : false);
        setFlippedY(jsonData.has("flippedY") ? jsonData.getBoolean("flippedY") : false);
    }

    @Override
    public String[] getVariableNames () {
        return new String[] {"x", "y", "rotation", "flippedX", "flippedY"};
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
            setFlippedX(Boolean.valueOf(String.valueOf(value)));
        else if (variableId == 4) setFlippedY(Boolean.valueOf(String.valueOf(value)));
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
            return flippedX;
        else if (variableId == 4)
            return flippedY;
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
        else if (variableName.equals("flippedX"))
            return 3;
        else if (variableName.equals("flippedY"))
            return 4;
        else
            return -1;
    }

    @Override
    public Object[] getValiables () {
        return null;
    }
}
