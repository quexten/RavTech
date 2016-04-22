
package com.ravelsoftware.ravtech.components.gizmos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.components.PolygonCollider;
import com.ravelsoftware.ravtech.util.EventType;
import com.ravelsoftware.ravtech.util.GeometryUtils;

public class PolygonColliderGizmo extends Gizmo<PolygonCollider> {

	boolean isGrabbed = false;
	Vector2 grabbedPoint = null;
	int selectedLine;
	float closestDst;

	public PolygonColliderGizmo (PolygonCollider component) {
		super(component);
	}

	@Override
	public void draw (PolygonShapeRenderer renderer, boolean selected) {
		renderer.setColor(Color.LIGHT_GRAY);
		Vector2 mousePosition = RavTech.input.getWorldPosition();
		/*for (int i = 0; i < component.vertecies.size; i++)
			renderCircle(renderer, component.vertecies.get(i), mousePosition, true);*/
		float closestDst = Float.MAX_VALUE;
		float selLine = -1;
		for (int i = 0; i < component.vertecies.size; i++)
			if (i < component.vertecies.size) {
				Vector2 firstPoint = component.getParent().transform.getPosition(component.vertecies.get(i).cpy());
				Vector2 secondPoint = component.getParent().transform.getPosition(i < component.vertecies.size - 1
					? component.vertecies.get(i + 1).cpy() : component.vertecies.get(0).cpy());
				if (GeometryUtils.isPointNearLine(firstPoint, secondPoint, mousePosition, closestDst)) {
					float dst = GeometryUtils.dstFromLine(firstPoint, secondPoint, mousePosition);
					if (dst < 0.3f) {
						selLine = i;
						closestDst = dst;
					}
				}
			}
		for (int i = 0; i < component.vertecies.size; i++)
			if (i < component.vertecies.size) {
				Vector2 firstPoint = component.getParent().transform.getPosition(component.vertecies.get(i).cpy());
				Vector2 secondPoint = component.getParent().transform.getPosition(i < component.vertecies.size - 1
					? component.vertecies.get(i + 1).cpy() : component.vertecies.get(0).cpy());
				if (i == selLine)
					renderer.setColor(Color.RED);
				else
					renderer.setColor(Color.GRAY);
				renderer.line(firstPoint, secondPoint);
			}
		Gdx.gl.glLineWidth(1);
	}

	@Override
	public float input (float x, float y, int button, int eventType) {
		Vector2 mousePosition = new Vector2(x, y);
		switch (eventType) {
		case EventType.MouseMoved:
			for (int i = 0; i < component.vertecies.size; i++)
				if (i < component.vertecies.size) {
					Vector2 firstPoint = component.getParent().transform.getPosition(component.vertecies.get(i).cpy());
					Vector2 secondPoint = component.getParent().transform.getPosition(i < component.vertecies.size - 1
						? component.vertecies.get(i + 1).cpy() : component.vertecies.get(0).cpy());
					if (GeometryUtils.isPointNearLine(firstPoint, secondPoint, mousePosition, closestDst)) {
						float dst = GeometryUtils.dstFromLine(firstPoint, secondPoint, mousePosition);
						if (dst < 0.3f) {
							selectedLine = i;
							closestDst = dst;
						}
					}
				}
			return closestDst;
		case EventType.MouseDown:
			float closestDst = Float.MAX_VALUE;
			int selectedLine = -1;
			for (int i = 0; i < component.vertecies.size; i++) {
				Vector2 position = component.getParent().transform.getPosition(component.vertecies.get(i).cpy());
				if (position.dst(mousePosition) < 0.5f) {
					if (Gdx.input.isButtonPressed(Buttons.RIGHT)) {
						component.vertecies.removeIndex(i);
						component.apply();
						return -1f;
					}
					isGrabbed = true;
					grabbedPoint = component.vertecies.get(i);
					return -1f;
				}
				if (i < component.vertecies.size) {
					Vector2 firstPoint = component.getParent().transform.getPosition(component.vertecies.get(i).cpy());
					Vector2 secondPoint = component.getParent().transform.getPosition(i < component.vertecies.size - 1
						? component.vertecies.get(i + 1).cpy() : component.vertecies.get(0).cpy());
					if (GeometryUtils.dstFromLine(firstPoint, secondPoint, mousePosition) < closestDst) {
						selectedLine = i;
						closestDst = GeometryUtils.dstFromLine(firstPoint, secondPoint, mousePosition);
					}
				}
			}
			if (closestDst < 1) {
				component.vertecies.insert(selectedLine + 1, new Vector2(mousePosition));
				grabbedPoint = component.vertecies.get(selectedLine + 1);
				isGrabbed = true;
			}
			return -1f;
		case EventType.MouseDrag:
			if (isGrabbed)
				if (grabbedPoint != null)
					grabbedPoint.set(mousePosition.sub(component.getParent().transform.getPosition())
						.rotate(-component.getParent().transform.getRotation()));
			return -1f;
		case EventType.MouseUp:
			if (isGrabbed) {
				isGrabbed = false;
				grabbedPoint = null;
				component.apply();
				component.getParent().transform.setRotation(component.getParent().transform.getRotation());
			}
			return -1f;
		}
		return -1f;
	}

	private void renderCircle (ShapeRenderer renderer, Vector2 position, Vector2 mousePosition, boolean isClosest) {
		boolean hoverable = (Gdx.input.isButtonPressed(Buttons.LEFT) && isGrabbed || !Gdx.input.isButtonPressed(Buttons.LEFT))
			&& isClosest;
		position = component.getParent().transform.getPosition(position.cpy());
		renderer.setColor(isGrabbed || position.dst(mousePosition.x, mousePosition.y) < 1 && hoverable ? Color.YELLOW : Color.GRAY);
		renderer.circle(position.x, position.y, 1 * RavTech.sceneHandler.worldCamera.zoom, 20);
	}

	@Override
	public boolean isInBoundingBox (Vector2 coord) {
		return false;
	}
}
