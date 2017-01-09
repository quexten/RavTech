
package com.quexten.ravtech.components;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.quexten.ravtech.RavTech;
import com.quexten.ravtech.util.Debug;

public class AudioEmitter extends GameComponent implements Json.Serializable {

	@Override
	public ComponentType getType () {
		return ComponentType.AudioEmitter;
	}

	@Override
	public String getName () {
		return getType().toString();
	}

	public String filePath;
	public boolean isMusic = false;
	public boolean playOnCreate = false;
	public boolean loop = false;
	public long id;

	public AudioEmitter () {
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void load (Array<AssetDescriptor> dependencies) {
		AssetDescriptor<Sound> assetDescriptor = new AssetDescriptor<Sound>(filePath, Sound.class);
		Debug.log("LoadSound", filePath);
		dependencies.add(assetDescriptor);
	}

	@Override
	public void finishedLoading () {
	}

	@Override
	public void update () {
		if (id == 0L && playOnCreate) {
			play();
			playOnCreate = false;
		}
	}

	@Override
	public void draw (SpriteBatch batch) {
	}

	@Override
	public void dispose () {
		if (RavTech.files.isLoaded(filePath)) {
			Sound sound = (Sound)RavTech.files.getAsset(filePath, Sound.class);
			sound.stop(id);
		}
	}

	public void setClip (String path) {
		path = path.replaceAll("\\\\", "/");
		filePath = path;
		RavTech.files.loadAsset(filePath, Sound.class);
		RavTech.files.finishLoading();
	}

	public void setPitch (float pitch) {
		((Sound)RavTech.files.getAsset(filePath, Sound.class)).setPitch(id, pitch);
	}

	public void play () {
		id = ((Sound)RavTech.files.getAsset(filePath, Sound.class)).play();
	}

	@Override
	public void write (Json json) {
		json.writeValue("path", filePath);
		json.writeValue("isMusic", isMusic);
		json.writeValue("playOnCreate", playOnCreate);
		json.writeValue("loop", loop);
	}

	@Override
	public void read (Json json, JsonValue jsonData) {
		filePath = jsonData.getString("path");
		isMusic = false;
		loop = jsonData.getBoolean("loop");
		playOnCreate = jsonData.getBoolean("playOnCreate");
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
}
