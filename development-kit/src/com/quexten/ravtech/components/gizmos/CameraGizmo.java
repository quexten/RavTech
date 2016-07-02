package com.quexten.ravtech.components.gizmos;

import com.badlogic.gdx.math.Vector2;
import com.quexten.ravtech.components.Camera;
import com.quexten.ravtech.graphics.PolygonShapeRenderer;
import com.quexten.ravtech.graphics.RavCamera;

public class CameraGizmo extends Gizmo<Camera> {

	public CameraGizmo (Camera component) {
		super(component);
	}

	@Override
	public void draw (PolygonShapeRenderer batch, boolean selected) {
		Vector2 position = component.getParent().transform.getPosition();
		RavCamera camera = component.camera;
		batch.box(position.x, position.y, camera.viewportWidth * camera.zoom, camera.viewportHeight * camera.zoom);
	}

	@Override
	public float input (float x, float y, int button, int eventType) {
		return 0;
	}

	@Override
	public boolean isInBoundingBox (Vector2 coord) {
		return false;
	}

}
