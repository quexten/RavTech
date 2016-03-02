
package com.ravelsoftware.ravtech.components;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.scripts.Script;
import com.ravelsoftware.ravtech.scripts.ScriptLoaderParameter;

public class ScriptComponent extends GameComponent implements Json.Serializable {

	public Script script;
	public String path;

	@Override
	public ComponentType getType () {
		return ComponentType.ScriptComponent;
	}

	@Override
	public String getName () {
		return "";
	}

	public ScriptComponent () {
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void load (Array<AssetDescriptor> dependencies) {
		dependencies.add(new AssetDescriptor<Script>(path, Script.class, new ScriptLoaderParameter(this.getParent())));
	}

	@Override
	public void finishedLoading () {
		this.script = RavTech.files.getAsset(path);
	}

	@Override
	public void update () {
		this.script.update();
	}

	@Override
	public void draw (SpriteBatch batch) {
	}

	@Override
	public void dispose () {
	}

	@Override
	public void setVariable (int variableID, Object value) {
		if (variableID == 0) this.setScript(String.valueOf(value));
	}

	@Override
	public int getVariableId (String variableName) {
		return variableName.equals("path") ? 0 : -1;
	}

	@Override
	public Object getVariable (int variableID) {
		return variableID == 0 ? this.path : null;
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
	public void write (Json json) {
		super.write(json);
		json.writeValue("path", path);
	}

	@Override
	public void read (Json json, JsonValue jsonData) {
		super.read(json, jsonData);
		this.path = jsonData.getString("path");
	}

	public void setScript (String scriptPath) {
		if (scriptPath.startsWith("/")) scriptPath = scriptPath.substring(1);
		this.path = scriptPath;
		RavTech.files.getAssetManager().unload(path);
		RavTech.files.getAssetManager()
			.load(new AssetDescriptor<Script>(path, Script.class, new ScriptLoaderParameter(this.getParent())));
		RavTech.files.finishLoading();
		this.finishedLoading();
	}
}
