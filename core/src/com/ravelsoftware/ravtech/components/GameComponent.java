package com.ravelsoftware.ravtech.components;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ravelsoftware.ravtech.animation.VariableAccessor;

public abstract class GameComponent implements Json.Serializable, VariableAccessor {

    private GameObject parent;

    public abstract ComponentType getType ();

    public abstract String getName ();

    public GameComponent(GameObject parent) {
        setParent(parent);
    }

    public GameComponent() {
    }

    /** Queues up the dependencies */
    public abstract void load (@SuppressWarnings("rawtypes") Array<AssetDescriptor> dependencies);

    /** Called upon loading finished */
    public abstract void finishedLoading ();

    /** Updates the component (Game Logic) */
    public abstract void update ();

    /** Renders the component */
    public abstract void draw (SpriteBatch batch);

    /** Disposes the component */
    public abstract void dispose ();

    /** Sets the parent gameobject of the component
     * @param parent - the new parent */
    public void setParent (GameObject parent) {
        this.parent = parent;
    }

    /** Gets the parent of the component
     * @return The parent of the component */
    public GameObject getParent () {
        return parent;
    }

    /** Checks whether the component is a descendant of the object
     * @param object - the GameObject
     * @return Whether the component is a descendant of the object. */
    public boolean isDescendantOf (GameObject object) {
        GameObject parent = getParent();
        while (parent != null) {
            for (int i = 0; i < parent.getComponents().size; i++)
                if (parent.getComponents().get(i) == parent) return true;
            parent = parent.getParent();
        }
        return false;
    }

    @Override
    public void write (Json json) {
    }

    @Override
    public void read (Json json, JsonValue jsonData) {
    }
}
