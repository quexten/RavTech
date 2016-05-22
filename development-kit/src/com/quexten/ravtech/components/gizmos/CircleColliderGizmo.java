
package com.quexten.ravtech.components.gizmos;

import com.badlogic.gdx.math.Vector2;
import com.quexten.ravtech.RavTech;
import com.quexten.ravtech.components.CircleCollider;
import com.quexten.ravtech.dk.ui.utils.ColorUtils;
import com.quexten.ravtech.graphics.PolygonShapeRenderer;
import com.quexten.ravtech.history.ChangeManager;
import com.quexten.ravtech.history.ModifyChangeable;
import com.quexten.ravtech.util.EventType;

public class CircleColliderGizmo extends Gizmo<CircleCollider> {

	float oldRadius = -1f;

	public CircleColliderGizmo (CircleCollider circleCollider) {
		super(circleCollider);
		isExclusive = true;
	}

	@Override
	public void draw (PolygonShapeRenderer renderer,
		boolean selected) {
		renderer.setColor(selected ? ColorUtils.getSelectionColor()
			: ColorUtils.getGizmoColor(component));
	}

	@Override
	public float input (float x, float y, int button, int eventtype) {
		switch (eventtype) {
			case EventType.MouseMoved:
				float distanceToMiddle = getMiddleDistance(x, y);
				if (Math.abs(distanceToMiddle
					- component.radius) < RavTech.sceneHandler.worldCamera.zoom
						* 3)
					return distanceToMiddle;
				else
					return -1;
			case EventType.MouseDown:
				oldRadius = component.radius;
				break;
			case EventType.MouseDrag:
				new ModifyChangeable(component, "", "radius", oldRadius,
					getMiddleDistance(x, y)).redo();
				break;
			case EventType.MouseUp:
				ChangeManager.addChangeable(new ModifyChangeable(
					component, "Set Circle Collider Radius: ", "radius",
					oldRadius, getMiddleDistance(x, y)));
				break;
		}
		return -1f;
	}

	@Override
	public boolean isInBoundingBox (Vector2 coord) {
		return false;
	}

	private Vector2 getMiddlePosition () {
		return component.getParent().transform.getPosition()
			.add(component.getPosition()
				.rotate(component.getParent().transform.getRotation()));
	}

	private float getMiddleDistance (float x, float y) {
		return getMiddlePosition().dst(x, y);
	}
}
