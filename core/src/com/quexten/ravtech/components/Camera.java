
package com.quexten.ravtech.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.quexten.ravtech.RavTech;
import com.quexten.ravtech.graphics.RavCamera;
import com.quexten.ravtech.util.Debug;

public class Camera extends Renderer {

	RavCamera camera;
	Array<String> layers = new Array<String>();
	
	@Override
	public ComponentType getType () {
		return ComponentType.Camera;
	}

	@Override
	public String getName () {
		return getType().toString();
	}

	public Camera () {		
	}

	@Override
	public void load (Array<AssetDescriptor> dependencies) {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				camera = RavTech.sceneHandler.cameraManager.createCamera(1280, 720);
				camera.renderToFramebuffer = true;
				camera.setResolution(512,512);
				camera.zoom = 0.01f;
				camera.renderAmbient = false;
			}
		});		
	}

	@Override
	public void finishedLoading () {
	}

	@Override
	public void update () {
	}

	@Override
	public void draw (SpriteBatch batch) {
		if(camera == null)
			return;
		camera.position.x = this.getParent().transform.getPosition().x;
		camera.position.y = this.getParent().transform.getPosition().y;
		camera.update();
	}

	@Override
	public void dispose () {
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
	public void write (Json json) {
		json.writeValue("layers", layers);
	}

	@Override
	public void read (Json json, JsonValue jsonData) {
		Debug.log("JsonData", jsonData);
		String layersString = jsonData.get("layers").toString();
		layersString = layersString.substring(layersString.indexOf('['));
		this.layers.addAll(json.fromJson(Array.class, layersString));
	}
	
}
