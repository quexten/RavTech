package com.ravelsoftware.ravtech.components;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public abstract class Renderer extends GameComponent implements Json.Serializable {

    @Override
    public ComponentType getType () {
        return ComponentType.Renderer;
    }

    public Renderer() {
    }

    public int sortingOrder = 0;
    public String sortingLayerName = "Default";
    public boolean enabled = true;

    @Override
    public abstract void load (@SuppressWarnings("rawtypes") Array<AssetDescriptor> dependencies);

    @Override
    public abstract void finishedLoading ();

    @Override
    public abstract void update ();

    @Override
    public abstract void draw (SpriteBatch batch);

    @Override
    public abstract void dispose ();

    @Override
    public void write (Json json) {
        json.writeValue("sortingLayerName", sortingLayerName);
        json.writeValue("sortingOrder", sortingOrder);
        json.writeValue("enabled", enabled);
    }

    @Override
    public void read (Json json, JsonValue jsonData) {
        if (jsonData.has("sortingLayerName")) this.sortingLayerName = jsonData.getString("sortingLayerName");
        if (jsonData.has("sortingOrder")) this.sortingOrder = jsonData.getInt("sortingOrder");
        if (jsonData.has("enabled")) this.enabled = jsonData.getBoolean("enabled");
    }

    @Override
    public String[] getVariableNames () {
        return null;
    }

    @Override
    public void setVariable (int variableID, Object value) {
    }

    @Override
    public int getVariableId (String variableName) {
        return 0;
    }

    @Override
    public Object getVariable (int variableID) {
        return 0;
    }

    @Override
    public Object[] getValiables () {
        return null;
    }

    @Override
    public String getName () {
        return null;
    }
}
