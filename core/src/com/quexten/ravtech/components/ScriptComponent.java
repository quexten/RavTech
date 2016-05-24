
package com.quexten.ravtech.components;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.quexten.ravtech.RavTech;
import com.quexten.ravtech.scripts.Script;

public class ScriptComponent extends GameComponent implements Json.Serializable {

	public Script script;
	public String scriptSource;
	public String path;

	@Override
	public ComponentType getType () {
		return ComponentType.ScriptComponent;
	}

	@Override
	public String getName () {
		return path != null && !path.isEmpty() ? path.substring(path.lastIndexOf('/') > -1 ? path.lastIndexOf('/') + 1 : 0)
			: "Empty Script Component";
	}

	public ScriptComponent () {
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void load (Array<AssetDescriptor> dependencies) {
		dependencies.add(new AssetDescriptor<String>(path, String.class));
	}

	@Override
	public void finishedLoading () {
		if (RavTech.files.getAssetManager().isLoaded(path))
			scriptSource = RavTech.files.getAsset(path);
		script = RavTech.scriptLoader.createScript(scriptSource, getParent());
	}

	@Override
	public void update () {
		script.update();
	}

	@Override
	public void draw (SpriteBatch batch) {
	}

	@Override
	public void dispose () {
		RavTech.files.removeDependency(path, this);
	}

	@Override
	public void setVariable (int variableID, Object value) {
		if (variableID == 0)
			setScript(String.valueOf(value));
	}

	@Override
	public int getVariableId (String variableName) {
		return variableName.equals("path") ? 0 : -1;
	}

	@Override
	public Object getVariable (int variableID) {
		return variableID == 0 ? path : null;
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
		path = jsonData.getString("path");
	}

	public void setScript (String scriptPath) {
		RavTech.files.addDependency(scriptPath, this);
		if (scriptPath.startsWith("/"))
			scriptPath = scriptPath.substring(1);
		path = scriptPath;
		if (RavTech.files.getAssetManager().isLoaded(path))
			RavTech.files.getAssetManager().unload(path);
		RavTech.files.getAssetManager().load(new AssetDescriptor<String>(path, String.class, new AssetLoaderParameters<String>()));
		RavTech.files.finishLoading();
		finishedLoading();
	}

	public void callFunction (String name, Object[] args) {
		try {
			script.callFunction(name, args);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public Object getVariable (String name) {
		return script.getVariable(name);
	}
}
