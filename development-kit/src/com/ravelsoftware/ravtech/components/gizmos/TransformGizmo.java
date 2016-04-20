
package com.ravelsoftware.ravtech.components.gizmos;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.components.Transform;
import com.ravelsoftware.ravtech.history.ChangeManager;
import com.ravelsoftware.ravtech.history.ModifyChangeable;
import com.ravelsoftware.ravtech.util.EventType;
import com.ravelsoftware.ravtech.util.GeometryUtils;

public class TransformGizmo extends Gizmo {

	Transform transform;
	private float oldX, oldY;
	private static int xaxis = 1, yaxis = 2, xyaxis = 3;
	private Vector2 grabPosition = null;
	private boolean isGrabbed = false;
	private int grabbedAxis = 0;
	float currentDst = 0;
	public boolean moveGrab;
	int selectedaxis;

	public TransformGizmo (Transform transform) {
		this.transform = transform;
	}

	@Override
	public void draw (ShapeRenderer renderer, boolean selected) {
		float zoom = RavTech.sceneHandler.worldCamera.zoom;
		Vector2 endpoint_x = new Vector2(transform.getPosition().cpy().add(new Vector2(50f * zoom, 0)));
		Vector2 endpoint_y = new Vector2(transform.getPosition().cpy().add(new Vector2(0, 50f * zoom)));
		renderer.setColor(selectedaxis != xaxis && selectedaxis != xyaxis || !selected ? Color.RED : Color.YELLOW);
		renderer.line(transform.getPosition(), endpoint_x);
		renderer.line(endpoint_x, new Vector2(endpoint_x.x - (float)Math.cos(Math.toRadians(25)) * 0.2f * 50 * zoom,
			endpoint_x.y - (float)Math.sin(Math.toRadians(25)) * 0.2f * 50 * zoom));
		renderer.line(endpoint_x, new Vector2(endpoint_x.x - (float)Math.cos(Math.toRadians(-25)) * 0.2f * 50 * zoom,
			endpoint_x.y - (float)Math.sin(Math.toRadians(-25)) * 0.2f * 50 * zoom));
		renderer.setColor(selectedaxis != yaxis && selectedaxis != xyaxis || !selected ? Color.GREEN : Color.YELLOW);
		renderer.line(transform.getPosition(), endpoint_y);
		renderer.line(endpoint_y, new Vector2(endpoint_y.x - (float)Math.cos(Math.toRadians(115)) * 0.2f * 50 * zoom,
			endpoint_y.y - (float)Math.sin(Math.toRadians(115)) * 0.2f * 50 * zoom));
		renderer.line(endpoint_y, new Vector2(endpoint_y.x - (float)Math.cos(Math.toRadians(65)) * 0.2f * 50 * zoom,
			endpoint_y.y - (float)Math.sin(Math.toRadians(65)) * 0.2f * 50 * zoom));	
	}

	@Override
	public float input (float x, float y, int button, int eventtype) {
		Vector2 worldPosition = new Vector2(x, y);
		if (!isGrabbed)
			selectedaxis = getSelectedAxis(worldPosition);
		else
			selectedaxis = grabbedAxis;

		if (moveGrab)
			selectedaxis = xyaxis;
		if (selectedaxis != 0) {
			switch (eventtype) {
			case EventType.MouseDown:
				grabPosition = transform.getPosition().cpy().sub(worldPosition);
				oldX = transform.getLocalPosition().cpy().x;
				oldY = transform.getLocalPosition().cpy().y;				
				isGrabbed = true;
				grabbedAxis = selectedaxis;
				break;
			case EventType.MouseDrag:
				if (!isGrabbed)
					return currentDst;
				Vector2 currentposition = transform.getPosition();
				Vector2 mouseposition = worldPosition;
				if (grabPosition != null && (grabbedAxis == xaxis || grabbedAxis == yaxis)) {
					transform.setPosition(new Vector2(selectedaxis == xaxis ? mouseposition.x + grabPosition.x : currentposition.x,
						selectedaxis == yaxis ? mouseposition.y + grabPosition.y : currentposition.y));
					ModifyChangeable changeable = grabbedAxis == xaxis
						? new ModifyChangeable(transform, "", "x", oldX, transform.getLocalPosition().cpy().x)
						: new ModifyChangeable(transform, "", "y", oldY, transform.getLocalPosition().cpy().y);
					changeable.isDummy = true;
					ChangeManager.addChangeable(changeable);
				} else if (grabPosition != null && grabbedAxis == xyaxis) {
					transform.setPosition(mouseposition.x + grabPosition.x, mouseposition.y + grabPosition.y);
					ModifyChangeable changeableX = new ModifyChangeable(transform, "", "x", oldX,
						transform.getLocalPosition().cpy().x);
					changeableX.isDummy = true;
					ChangeManager.addChangeable(changeableX);
					ModifyChangeable changeableY = new ModifyChangeable(transform, "", "y", oldY,
						transform.getLocalPosition().cpy().y);
					changeableY.isDummy = true;
					ChangeManager.addChangeable(changeableY);
				}
				break;
			case EventType.MouseUp:
				isGrabbed = false;
				ModifyChangeable changeable = null;
				if (grabPosition != null && grabbedAxis == xaxis || grabbedAxis == xyaxis) {
					changeable = new ModifyChangeable(transform, "Set Transform X:" + transform.getLocalPosition().cpy().x, "x", oldX,
						transform.getLocalPosition().cpy().x);
					ChangeManager.addChangeable(changeable);
				}
				if (grabPosition != null && grabbedAxis == yaxis || grabbedAxis == xyaxis) {
					changeable = new ModifyChangeable(transform, "Set Transform Y:" + transform.getLocalPosition().cpy().y, "y", oldY,
						transform.getLocalPosition().cpy().y);
					if (grabbedAxis == xyaxis)
						changeable.previousConnected = true;
					ChangeManager.addChangeable(changeable);
				}				
				grabbedAxis = 0;
				break;
			}
			return currentDst;
		}
		return -1f;
	}

	private int getSelectedAxis (Vector2 worldPosition) {
		float zoom = RavTech.sceneHandler.worldCamera.zoom;
		float selectiondst = 0.2f * 20f * zoom;
		int selectedaxis = 0;
		Vector2 endpoint_x = new Vector2(transform.getPosition().cpy().add(new Vector2(50 * zoom, 0)));
		Vector2 endpoint_y = new Vector2(transform.getPosition().cpy().add(new Vector2(0, 50 * zoom)));
		float xaxisdst = GeometryUtils.dstFromLine(transform.getPosition().cpy(), endpoint_x, worldPosition);
		boolean ispointnearxaxis = GeometryUtils.isPointNearLine(transform.getPosition().cpy(), endpoint_x, worldPosition,
			selectiondst);
		float yaxisdst = GeometryUtils.dstFromLine(transform.getPosition().cpy(), endpoint_y, worldPosition);
		boolean ispointnearyaxis = GeometryUtils.isPointNearLine(transform.getPosition().cpy(), endpoint_y, worldPosition,
			selectiondst);
		if (ispointnearxaxis && xaxisdst <= yaxisdst) {
			selectedaxis = xaxis;
			currentDst = xaxisdst;
		} else if (ispointnearyaxis) {
			selectedaxis = yaxis;
			currentDst = yaxisdst;
		}
		if (worldPosition.dst(transform.getPosition().cpy()) < 0.3f * 20f * zoom) {
			selectedaxis = xyaxis;
			currentDst = worldPosition.dst(transform.getPosition().cpy());
		}
		return selectedaxis;
	}

	@Override
	public boolean isInBoundingBox (Vector2 coord) {
		return false;
	}
}
