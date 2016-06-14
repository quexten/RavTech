
package com.quexten.ravtech.graphics;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class CameraManager {

	Array<RavCamera> cameras = new Array<RavCamera>();
	SpriteBatch batch = new SpriteBatch();

	public void render () {
		for (int i = 0; i < cameras.size; i++) {
			batch.setProjectionMatrix(cameras.get(i).combined);
			cameras.get(i).update();
			cameras.get(i).render(batch);
		}
	}

	public RavCamera createCamera (int width, int height) {
		RavCamera RavCamera = new RavCamera(width, height);
		cameras.add(RavCamera);
		return RavCamera;
	}

	public void destroyCamera (RavCamera RavCamera) {
		RavCamera.dispose();
		cameras.removeValue(RavCamera, true);
	}

	public void dispose () {
		batch.dispose();
		for (int i = 0; i < cameras.size; i++) {
			cameras.get(i).dispose();
		}
	}

}
