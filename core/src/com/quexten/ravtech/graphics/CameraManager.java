
package com.quexten.ravtech.graphics;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class CameraManager {

	Array<Camera> cameras = new Array<Camera>();
	SpriteBatch batch = new SpriteBatch();

	public void render () {
		for (int i = 0; i < cameras.size; i++) {
			batch.setProjectionMatrix(cameras.get(i).combined);
			cameras.get(i).update();
			cameras.get(i).render(batch);
		}
	}

	public Camera createCamera (int width, int height) {
		Camera camera = new Camera(width, height);
		cameras.add(camera);
		return camera;
	}

	public void destroyCamera (Camera camera) {
		camera.dispose();
		cameras.removeValue(camera, true);
	}

	public void dispose () {
		batch.dispose();
		for (int i = 0; i < cameras.size; i++) {
			cameras.get(i).dispose();
		}
	}

}
