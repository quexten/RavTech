
package com.quexten.ravtech.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.quexten.ravtech.RavTech;
import com.quexten.ravtech.graphics.RavCamera;
import com.quexten.ravtech.util.JsonUtil;

public class Camera extends Renderer {

	public RavCamera camera;

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

	@SuppressWarnings("rawtypes")
	@Override
	public void load (Array<AssetDescriptor> dependencies) {

	}

	@Override
	public void finishedLoading () {
		camera = RavTech.sceneHandler.cameraManager.createCamera(1280, 720);
		camera.setRenderToFramebuffer(true);
		camera.setResolution(512, 512);
		camera.zoom = 0.01f;
		camera.setRenderAmbientLightColor(false);
	}

	@Override
	public void update () {
	}

	@Override
	public void draw (SpriteBatch batch) {
		if (camera == null)
			return;
		camera.position.x = this.getParent().transform.getPosition().x;
		camera.position.y = this.getParent().transform.getPosition().y;
		camera.update();
	}

	@Override
	public void dispose () {
	}

	@Override
	public void write (Json json) {
		json.writeValue("layers", camera.getLayers());
		json.writeValue("renderAmbient", camera.getRenderAmbientLightColor());
		json.writeValue("renderToFramebuffer", camera.getRenderToFramebuffer());
		json.writeValue("clearColor", camera.getClearColor());
		json.writeValue("resolutionX", (int)camera.getResolution().x);
		json.writeValue("resolutionY", (int)camera.getResolution().y);
		json.writeValue("zoom", camera.zoom);
		json.writeValue("viewportWidth", camera.viewportWidth);
		json.writeValue("viewportHeight", camera.viewportHeight);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void read (Json json, JsonValue jsonData) {
		String layersString = jsonData.get("layers").toString();
		layersString = layersString.substring(layersString.indexOf('['));

		final String layers = layersString;
		final boolean renderAmbient =  jsonData.has("renderAmbient") ? jsonData.getBoolean("renderAmbient") : true;
		final boolean renderToFramebuffer = jsonData.has("renderToFramebuffer") ? jsonData.getBoolean("renderToFramebuffer") : true;
		final Color clearColor =  jsonData.has("clearColor") ? JsonUtil.readColorFromJson(jsonData, "clearColor") : Color.WHITE.cpy();
		final int resolutionX = jsonData.has("resolutionX") ? jsonData.getInt("resolutionX") : 512;
		final int resolutionY = jsonData.has("resolutionY") ? jsonData.getInt("resolutionY") : 512;
		final float zoom = jsonData.has("zoom") ? jsonData.getFloat("zoom") : 0.05f;
		final float viewportWidth = jsonData.has("viewportWidth") ? jsonData.getFloat("viewportWidth") : 512;
		final float viewportHeight = jsonData.has("viewportHeight") ? jsonData.getFloat("viewportHeight") : 512;

		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run () {
				camera.setLayers(new Json().fromJson(Array.class, layers));
				camera.setRenderAmbientLightColor(renderAmbient);
				camera.setRenderToFramebuffer(renderToFramebuffer);
				camera.setClearColor(clearColor);
				camera.setResolution(resolutionX, resolutionY);
				camera.zoom = zoom;
				camera.viewportWidth = viewportWidth;
				camera.viewportHeight = viewportHeight;
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setVariable (int variableId, Object value) {
		switch (variableId) {
			case 0:
				this.camera.setLayers((Array<String>)value);
				break;
			case 1:
				camera.setRenderAmbientLightColor(Boolean.valueOf(String.valueOf(value)));
				break;
			case 2:
				camera.setRenderToFramebuffer(Boolean.valueOf(String.valueOf(value)));
				break;
			case 3:
				camera.setClearColor((Color) value);
				break;
			case 4:
				int resolutionX = Integer.valueOf(String.valueOf(value).contains(".") ? String.valueOf(value).substring(0,  String.valueOf(value).lastIndexOf('.')) : String.valueOf(value));
				camera.setResolution(resolutionX, (int)camera.getResolution().y);
				break;
			case 5:
				int resolutionY = Integer.valueOf(String.valueOf(value).contains(".") ? String.valueOf(value).substring(0,  String.valueOf(value).lastIndexOf('.')) : String.valueOf(value));
				camera.setResolution((int)camera.getResolution().x, resolutionY);
				break;
			case 6:
				camera.zoom = Float.valueOf(String.valueOf(value));
				break;
			case 7:
				camera.viewportWidth = Float.valueOf(String.valueOf(value));
				break;
			case 8:
				camera.viewportHeight = Float.valueOf(String.valueOf(value));
				break;
		}
	}

	@Override
	public int getVariableId (String variableName) {
		switch (variableName) {
			case "layers":
				return 0;
			case "renderAmbient":
				return 1;
			case "renderToFramebuffer":
				return 2;
			case "clearColor":
				return 3;
			case "resolutionX":
				return 4;
			case "resolutionY":
				return 5;
			case "zoom":
				return 6;
			case "viewportWidth":
				return 7;
			case "viewportHeight":
				return 8;
		}
		return -1;
	}

	@Override
	public Object getVariable (int variableId) {
		return getValiables()[variableId];
	}

	@Override
	public String[] getVariableNames () {
		return new String[] {"layers", "renderAmbient", "renderToFramebuffer", "clearColor", "resolutionX", "resolutionY", "zoom", "viewportWidth", "viewportHeight"};
	}

	@Override
	public Object[] getValiables () {
		return new Object[] {this.camera.getLayers(), camera.getRenderAmbientLightColor(), camera.getRenderToFramebuffer(), camera.getClearColor(), camera.getResolution().x, camera.getResolution().y, camera.zoom,
			camera.viewportWidth, camera.viewportHeight};
	}

}
