
package com.ravelsoftware.ravtech.components.gizmos;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.components.CircleCollider;
import com.ravelsoftware.ravtech.dk.ui.utils.ColorUtils;
import com.ravelsoftware.ravtech.history.ChangeManager;
import com.ravelsoftware.ravtech.history.ModifyChangeable;
import com.ravelsoftware.ravtech.util.EventType;

public class CircleColliderGizmo extends Gizmo {

	public CircleCollider circleCollider;
	float oldRadius = -1f;

	public CircleColliderGizmo (CircleCollider circleCollider) {
		this.circleCollider = circleCollider;
		isExclusive = true;
	}

	@Override
	public void draw (ShapeRenderer renderer, boolean selected) {
		renderer.setColor(selected ? ColorUtils.getSelectionColor() : ColorUtils.getGizmoColor(circleCollider));
		Vector2 middlePosition = getMiddlePosition();
		renderer.circle(middlePosition.x, middlePosition.y, circleCollider.radius, 72);
	}

	@Override
	public float input (float x, float y, int button, int eventtype) {
		switch (eventtype) {
		case EventType.MouseMoved:
			float distanceToMiddle = getMiddleDistance(x, y);
			if (Math.abs(distanceToMiddle - circleCollider.radius) < RavTech.sceneHandler.worldCamera.zoom * 3)
				return distanceToMiddle;
			else
				return -1;
		case EventType.MouseDown:
			oldRadius = circleCollider.radius;
			break;
		case EventType.MouseDrag:
			new ModifyChangeable(circleCollider, "", "radius", oldRadius, getMiddleDistance(x, y)).redo();
			break;
		case EventType.MouseUp:
			ChangeManager.addChangeable(
				new ModifyChangeable(circleCollider, "Set Circle Collider Radius: ", "radius", oldRadius, getMiddleDistance(x, y)));
			break;
		}
		return -1f;
	}

	@Override
	public boolean isInBoundingBox (Vector2 coord) {
		return false;
	}

	private Vector2 getMiddlePosition () {
		return circleCollider.getParent().transform.getPosition()
			.add(circleCollider.getPosition().rotate(circleCollider.getParent().transform.getRotation()));
	}

	private float getMiddleDistance (float x, float y) {
		return getMiddlePosition().dst(x, y);
	}
}
