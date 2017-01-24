
package com.quexten.ravtech.components;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.quexten.ravtech.graphics.Shader;
import com.quexten.ravtech.graphics.SortedRenderer;

public abstract class Renderer extends GameComponent implements Json.Serializable {

	SortedRenderer renderer;
	
	@Override
	public ComponentType getType () {
		return ComponentType.Renderer;
	}

	public Renderer (SortedRenderer renderer) {
		this.renderer = renderer;
		renderer.register(this);
	}

	public int sortingOrder = 0;
	public String sortingLayerName = "Default";
	public boolean enabled = true;
	public Shader shader = new Shader("default");
	public int srcBlendFunction = GL20.GL_ONE;
	public int dstBlendFunction = GL20.GL_ONE_MINUS_SRC_ALPHA;


	@Override
	public abstract void load (@SuppressWarnings("rawtypes") Array<AssetDescriptor> dependencies);

	@Override
	public abstract void finishedLoading ();

	@Override
	public abstract void update ();

	@Override
	public abstract void draw (SpriteBatch batch);

	@Override
	public void dispose () {
		renderer.unregister(this);
	}

	@Override
	public void write (Json json) {
		json.writeValue("sortingLayerName", sortingLayerName);
		json.writeValue("sortingOrder", sortingOrder);
		json.writeValue("enabled", enabled);
		json.writeValue("shader", shader);
		json.writeValue("srcBlendFunction", srcBlendFunction);
		json.writeValue("dstBlendFunction", dstBlendFunction);
	}

	@Override
	public void read (Json json, JsonValue jsonData) {
		if (jsonData.has("sortingLayerName"))
			sortingLayerName = jsonData.getString("sortingLayerName");
		if (jsonData.has("sortingOrder"))
			sortingOrder = jsonData.getInt("sortingOrder");
		if (jsonData.has("enabled"))
			enabled = jsonData.getBoolean("enabled");
		if (jsonData.has("shader")) {
			Shader shader = new Shader("");
			shader.read(json, jsonData.get("shader"));
			this.shader = shader;
		}
		if(jsonData.has("srcBlendFunction"))
			srcBlendFunction = jsonData.getInt("srcBlendFunction");
		if(jsonData.has("dstBlendFunction"))
			srcBlendFunction = jsonData.getInt("dstBlendFunction");
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
