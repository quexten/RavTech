/*******************************************************************************
 * Copyright 2014-2016 Bernd Schoolmann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.ravelsoftware.ravtech.components.gizmos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.components.PolygonCollider;
import com.ravelsoftware.ravtech.util.EventType;
import com.ravelsoftware.ravtech.util.GeometryUtils;

public class PolygonColliderGizmo extends Gizmo {

	PolygonCollider polygonCollider;
	boolean isGrabbed = false;
	Vector2 grabbedPoint = null;
	int selectedLine;
	float closestDst;

	public PolygonColliderGizmo (PolygonCollider polygonCollider) {
		this.polygonCollider = polygonCollider;
	}

	@Override
	public void draw (ShapeRenderer renderer, boolean selected) {
		renderer.setAutoShapeType(true);
		renderer.setColor(Color.LIGHT_GRAY);
		renderer.set(ShapeType.Filled);
		Vector2 mousePosition = RavTech.input.getWorldPosition();
		for (int i = 0; i < polygonCollider.vertecies.size; i++)
			renderCircle(renderer, polygonCollider.vertecies.get(i), mousePosition, true);
		renderer.set(ShapeType.Line);
		float closestDst = Float.MAX_VALUE;
		float selLine = -1;
		for (int i = 0; i < polygonCollider.vertecies.size; i++)
			if (i < polygonCollider.vertecies.size) {
				Vector2 firstPoint = polygonCollider.getParent().transform.getPosition(polygonCollider.vertecies.get(i).cpy());
				Vector2 secondPoint = polygonCollider.getParent().transform.getPosition(i < polygonCollider.vertecies.size - 1
					? polygonCollider.vertecies.get(i + 1).cpy() : polygonCollider.vertecies.get(0).cpy());
				if (GeometryUtils.isPointNearLine(firstPoint, secondPoint, mousePosition, closestDst)) {
					float dst = GeometryUtils.dstFromLine(firstPoint, secondPoint, mousePosition);
					if (dst < 0.3f) {
						selLine = i;
						closestDst = dst;
					}
				}
			}
		for (int i = 0; i < polygonCollider.vertecies.size; i++)
			if (i < polygonCollider.vertecies.size) {
				Vector2 firstPoint = polygonCollider.getParent().transform.getPosition(polygonCollider.vertecies.get(i).cpy());
				Vector2 secondPoint = polygonCollider.getParent().transform.getPosition(i < polygonCollider.vertecies.size - 1
					? polygonCollider.vertecies.get(i + 1).cpy() : polygonCollider.vertecies.get(0).cpy());
				if (i == selLine)
					renderer.setColor(Color.RED);
				else
					renderer.setColor(Color.GRAY);
				renderer.line(firstPoint, secondPoint);
			}
		Gdx.gl.glLineWidth(1);
	}

	@Override
	public float input (int button, int eventType) {
		Vector3 unprojectedMouse = RavTech.sceneHandler.worldCamera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
		Vector2 mousePosition = new Vector2(unprojectedMouse.x, unprojectedMouse.y);
		switch (eventType) {
		case EventType.MouseMoved:
			for (int i = 0; i < polygonCollider.vertecies.size; i++)
				if (i < polygonCollider.vertecies.size) {
					Vector2 firstPoint = polygonCollider.getParent().transform.getPosition(polygonCollider.vertecies.get(i).cpy());
					Vector2 secondPoint = polygonCollider.getParent().transform.getPosition(i < polygonCollider.vertecies.size - 1
						? polygonCollider.vertecies.get(i + 1).cpy() : polygonCollider.vertecies.get(0).cpy());
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
			for (int i = 0; i < polygonCollider.vertecies.size; i++) {
				Vector2 position = polygonCollider.getParent().transform.getPosition(polygonCollider.vertecies.get(i).cpy());
				if (position.dst(mousePosition) < 0.5f) {
					if (Gdx.input.isButtonPressed(Buttons.RIGHT)) {
						polygonCollider.vertecies.removeIndex(i);
						polygonCollider.apply();
						return -1f;
					}
					isGrabbed = true;
					grabbedPoint = polygonCollider.vertecies.get(i);
					return -1f;
				}
				if (i < polygonCollider.vertecies.size) {
					Vector2 firstPoint = polygonCollider.getParent().transform.getPosition(polygonCollider.vertecies.get(i).cpy());
					Vector2 secondPoint = polygonCollider.getParent().transform.getPosition(i < polygonCollider.vertecies.size - 1
						? polygonCollider.vertecies.get(i + 1).cpy() : polygonCollider.vertecies.get(0).cpy());
					if (GeometryUtils.dstFromLine(firstPoint, secondPoint, mousePosition) < closestDst) {
						selectedLine = i;
						closestDst = GeometryUtils.dstFromLine(firstPoint, secondPoint, mousePosition);
					}
				}
			}
			if (closestDst < 1) {
				polygonCollider.vertecies.insert(selectedLine + 1, new Vector2(mousePosition));
				this.grabbedPoint = polygonCollider.vertecies.get(selectedLine + 1);
				isGrabbed = true;
			}
			return -1f;
		case EventType.MouseDrag:
			if (isGrabbed)
				if (grabbedPoint != null) grabbedPoint.set(mousePosition.sub(polygonCollider.getParent().transform.getPosition())
					.rotate(-polygonCollider.getParent().transform.getRotation()));
			return -1f;
		case EventType.MouseUp:
			if (isGrabbed) {
				isGrabbed = false;
				grabbedPoint = null;
				polygonCollider.apply();
				polygonCollider.getParent().transform.setRotation(polygonCollider.getParent().transform.getRotation());
			}
			return -1f;
		}
		return -1f;
	}

	private void renderCircle (ShapeRenderer renderer, Vector2 position, Vector2 mousePosition, boolean isClosest) {
		boolean hoverable = (Gdx.input.isButtonPressed(Buttons.LEFT) && isGrabbed || !Gdx.input.isButtonPressed(Buttons.LEFT))
			&& isClosest;
		position = polygonCollider.getParent().transform.getPosition(position.cpy());
		renderer.setColor(isGrabbed || position.dst(mousePosition.x, mousePosition.y) < 1 && hoverable ? Color.YELLOW : Color.GRAY);
		renderer.circle(position.x, position.y, 1 * RavTech.sceneHandler.worldCamera.zoom, 20);
	}

	@Override
	public boolean isInBoundingBox (Vector2 coord) {
		return false;
	}
}
