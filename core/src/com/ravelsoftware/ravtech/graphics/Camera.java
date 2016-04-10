
package com.ravelsoftware.ravtech.graphics;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.util.Debug;

import box2dLight.DynamicLightMap;

public class Camera extends OrthographicCamera {

	int resolutionX;
	int resolutionY;
	FrameBuffer cameraBuffer;
	FrameBuffer cameraPingPongBuffer;
	DynamicLightMap lightMap;
	Array<Shader> shaders = new Array<Shader>();
	String cameraBufferName = "TestCameraBuffer";
	String cameraPingPongBufferName = "TestCameraPingPongBufferName";
	boolean renderToFramebuffer = RavTech.isEditor;
	public static int camId;

	public Camera (int width, int height) {
		super(width, height);
		camId++;
		cameraBufferName = this.cameraBufferName + camId;
		cameraPingPongBufferName = this.cameraPingPongBufferName + camId;
		lightMap = RavTech.sceneHandler.lightHandler.createLightMap(width, height);
		setResolution(width, height);
	}

	public float getRotation () { // converts from -180|180 to 0|359
		float camAngle = -(float)Math.atan2(up.x, up.y) * MathUtils.radiansToDegrees + 180;
		return camAngle;
	}

	public void setRotation (float rotation) {
		rotate(getRotation() - rotation + 180);
		update();
	}

	public void render (SpriteBatch spriteBatch) {
		if (RavTech.settings.getBoolean("useLights")) {
			RavTech.sceneHandler.lightHandler.setLightMap(lightMap);
			RavTech.sceneHandler.lightHandler.setCombinedMatrix(this);
			RavTech.sceneHandler.lightHandler.updateAndRender();
		}

		RavTech.sceneHandler.renderer.render(spriteBatch, this);
		int passes = 0;
		for (int i = 0; i < shaders.size; i++) {
			shaders.get(i).applyPasses(spriteBatch, passes % 2 == 0 ? cameraBuffer : cameraPingPongBuffer,
				passes % 2 == 1 ? cameraBuffer : cameraPingPongBuffer);
			passes += shaders.get(i).getShaderPassCount();
		}
	}

	public void setResolution (int width, int height) {
		Debug.logDebug("SetResolution", width + "|" + height);
		dispose();
		resolutionX = width;
		resolutionY = height;
		lightMap.dispose();
		lightMap = RavTech.sceneHandler.lightHandler.createLightMap(width, height);
		if (RavTech.sceneHandler.shaderManager != null && renderToFramebuffer && width > 0 && height > 0) {

			float downSample = 1f;
			RavTech.sceneHandler.shaderManager.createFB(cameraBufferName, (int)(resolutionX / downSample),
				(int)(resolutionY / downSample));
			RavTech.sceneHandler.shaderManager.createFB(cameraPingPongBufferName, (int)(resolutionX / downSample),
				(int)(resolutionY / downSample));
			cameraBuffer = RavTech.sceneHandler.shaderManager.getFB(cameraBufferName);
			cameraPingPongBuffer = RavTech.sceneHandler.shaderManager.getFB(cameraPingPongBufferName);
			shaders.clear();
		}
	}

	public void dispose () {
		if (RavTech.sceneHandler.shaderManager != null && RavTech.sceneHandler.shaderManager.getFB(cameraBufferName) != null) {
			RavTech.sceneHandler.shaderManager.disposeFB(cameraBufferName);
			RavTech.sceneHandler.shaderManager.disposeFB(cameraPingPongBufferName);
		}
	}

	public Texture getCameraBufferTexture () {
		return this.cameraBuffer.getColorBufferTexture();
	}

	public Vector2 getResolution () {
		return new Vector2(this.resolutionX, this.resolutionY);
	}

	@Override
	public Vector3 unproject (Vector3 screenCoords, float viewportX, float viewportY, float viewportWidth, float viewportHeight) {
		float x = screenCoords.x, y = screenCoords.y;
		x = x - viewportX;
		y = viewportHeight - y - 1;
		y = y - viewportY;
		screenCoords.x = 2 * x / viewportWidth - 1;
		screenCoords.y = 2 * y / viewportHeight - 1;
		screenCoords.z = 2 * screenCoords.z - 1;
		screenCoords.prj(invProjectionView);
		return screenCoords;
	}

	public Vector3 unproject (Vector3 screenCoords) {
		unproject(screenCoords, 0, 0, resolutionX, resolutionY);
		return screenCoords;
	}

	public Vector2 unproject (Vector2 screenCoords) {
		Vector3 unprojection = unproject(new Vector3(screenCoords.x, screenCoords.y, 0));
		return screenCoords.set(unprojection.x, unprojection.y);
	}

}
