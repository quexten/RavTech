
package com.quexten.ravtech.graphics;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class CameraManager {

	int camId = 0;
	
	public Array<RavCamera> cameras = new Array<RavCamera>();
	SpriteBatch batch = new SpriteBatch();

	public void render () {
		for (int i = 0; i < cameras.size; i++) {
			batch.setProjectionMatrix(cameras.get(i).combined);
			cameras.get(i).update();
			cameras.get(i).render(batch);
		}
	}
	
	public RavCamera createCamera (int id, int width, int height) {
		RavCamera camera = new RavCamera(id, width, height);
		cameras.add(camera);
		return camera;
	}

	public RavCamera createCamera (int width, int height) {
		camId++;
		return createCamera(camId, width, height);
	}
	
	public void destroyCamera (RavCamera camera) {
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
