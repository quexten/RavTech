
package com.ravelsoftware.ravtech.components.gizmos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.components.Transform;
import com.ravelsoftware.ravtech.dk.RavTechDK;
import com.ravelsoftware.ravtech.dk.RavTechDK.EditingMode;
import com.ravelsoftware.ravtech.util.Debug;
import com.ravelsoftware.ravtech.util.EventType;
import com.ravelsoftware.ravtech.util.GeometryUtils;

public class TransformGizmo extends Gizmo<Transform> {

	// Move / Scale
	private static int AXIS_X = 1, AXIS_Y = 2,
		AXIS_XY = AXIS_X | AXIS_Y;

	// Rotation
	private static int AXIS_ROTATION = 1, RING = 2;

	private int selectedAxis = 2;

	private Vector2 grabOffset = new Vector2();
	private float oldRotation;
	private Vector2 oldScale = new Vector2();

	final static int ARROW_LENGTH = 100;
	final static int ARROW_WIDTH = 20;
	final static int MINIMUM_DST = 20;
	final static int SCALE_SIZE = 10;

	PolygonRegion arrowRegion = new PolygonRegion(
		new TextureRegion(GizmoHandler.whiteTexture),
		new float[] {0, 0, 0.75f, -0.25f, 0.75f, 0.25f, 1, 0},
		new short[] {0, 1, 3, 0, 2, 3});
	PolygonRegion circleRegion = GizmoHandler.createCircleRegion(20);

	public TransformGizmo (Transform transform) {
		super(transform);
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public void draw (PolygonShapeRenderer batch, boolean selected) {
		float positionX = component.getPosition().x;
		float positionY = component.getPosition().y;

		switch (RavTechDK.getEditingMode()) {
			case Move:
				// Draw X Axis
				batch.setColor((selectedAxis & AXIS_X) == 0 || !selected
					? Color.RED : Color.YELLOW);
				batch.draw(arrowRegion, positionX, positionY,
					ARROW_LENGTH * getZoom(), ARROW_WIDTH * getZoom());

				// Draw Y Axis
				batch.setColor((selectedAxis & AXIS_Y) == 0 || !selected
					? Color.GREEN : Color.YELLOW);
				batch.draw(arrowRegion, positionX, positionY, 0, 0,
					ARROW_LENGTH * getZoom(), ARROW_WIDTH * getZoom(), 1,
					1, 90);
				break;
			case Rotate:
				// Draw Difference
				if (Gdx.input.isTouched() && selected) {
					batch.setColor(new Color(0.5f, 0.5f, 0.5f, 0.5f));
					batch.draw(circleRegion, positionX, positionY, 0, 0,
						ARROW_LENGTH, ARROW_LENGTH, getZoom(), getZoom(),
						oldRotation);

					// Draw Old Rotation Axis
					batch.setColor(0.6f, 0.6f, 0.6f, 1f);
					batch.draw(arrowRegion, positionX, positionY, 0, 0,
						ARROW_LENGTH, ARROW_WIDTH, getZoom(), getZoom(),
						oldRotation);
				}

				// Draw Ring
				batch.setThickness(2);
				batch.setColor(
					(selectedAxis != RING) ? Color.BLUE : Color.YELLOW);
				batch.drawCone(positionX, positionY, 0, 360,
					ARROW_LENGTH * getZoom());

				// Draw Rotation Axis
				batch.setColor((selectedAxis != AXIS_ROTATION)
					? Color.BLUE : Color.YELLOW);
				batch.draw(arrowRegion, positionX, positionY, 0, 0,
					ARROW_LENGTH, ARROW_WIDTH, getZoom(), getZoom(),
					component.getRotation());
				break;
			case Scale:				
				float deltaScaleX = 0;				
				float deltaScaleY = 0;
				if(Gdx.input.isTouched() && selected) {
					if((selectedAxis & AXIS_X) > 0)
						deltaScaleX = RavTech.input.getWorldPosition().x - (positionX + grabOffset.x);
					if((selectedAxis & AXIS_Y) > 0)
						deltaScaleY = RavTech.input.getWorldPosition().y -(positionY + grabOffset.y);
				}
				// Draw X Axis
				batch.setColor((selectedAxis & AXIS_X) == 0 || !selected
					? Color.RED : Color.YELLOW);
				batch.line(positionX, positionY,
					positionX + ARROW_LENGTH * getZoom() + deltaScaleX, positionY);

				// Draw X End
				batch.setThickness(10);
				batch.line(positionX + (ARROW_LENGTH - 10) * getZoom() + deltaScaleX,
					positionY - 5 * getZoom(),
					positionX + ARROW_LENGTH * getZoom() + deltaScaleX,
					positionY - 5 * getZoom());
				batch.setThickness(1);

				// Draw Y Axis
				batch.setColor((selectedAxis & AXIS_Y) == 0 || !selected
					? Color.GREEN : Color.YELLOW);
				batch.line(positionX, positionY, positionX,
					positionY + ARROW_LENGTH * getZoom() + deltaScaleY);

				// Draw Y End
				batch.setThickness(10);
				batch.line(positionX + 5 * getZoom(),
					positionY + (ARROW_LENGTH - 10) * getZoom() + deltaScaleY,
					positionX + 5 * getZoom(),
					positionY + ARROW_LENGTH * getZoom() + deltaScaleY);
				batch.setThickness(1);
				break;
		}
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public float input (float x, float y, int button, int eventType) {
		float positionX = component.getPosition().x;
		float positionY = component.getPosition().y;

		switch (RavTechDK.getEditingMode()) {
			case Move:
				switch (eventType) {
					case EventType.MouseDrag:
						float newX = ((AXIS_X & selectedAxis) > 0)
							? x + grabOffset.x
							: component.getLocalPosition().x;
						float newY = ((AXIS_Y & selectedAxis) > 0)
							? y + grabOffset.y
							: component.getLocalPosition().y;

						// Stepping
						if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) {
							if ((AXIS_X & selectedAxis) > 0)
								newX = (int)newX;
							if ((AXIS_Y & selectedAxis) > 0)
								newY = (int)newY;
						}

						component.setLocalPosition(newX, newY);
						break;
					case EventType.MouseDown:
						grabOffset.set(component.getLocalPosition().x - x,
							component.getLocalPosition().y - y);
						break;
					case EventType.MouseMoved:
						float dstXY = component.getPosition().dst(x, y);
						float dstX = (x > positionX
							&& x < positionX + ARROW_LENGTH * getZoom())
								? Math.abs(positionY - y) : Float.MAX_VALUE;
						float dstY = (y > positionY
							&& y < positionY + ARROW_LENGTH * getZoom())
								? Math.abs(positionX - x) : Float.MAX_VALUE;

						if (dstXY < MINIMUM_DST * getZoom()) {
							selectedAxis = AXIS_XY;
							return dstXY;
						} else if (dstX <= dstY
							&& dstX < MINIMUM_DST * getZoom()) {
							selectedAxis = AXIS_X;
							return dstX;
						} else if (dstY < MINIMUM_DST * getZoom()) {
							selectedAxis = AXIS_Y;
							return dstY;
						}
				}
				break;
			case Rotate:
				switch (eventType) {
					case EventType.MouseDrag:
						float newRotation = 0;
						if (selectedAxis == RING) {
							newRotation = oldRotation
								+ (component.getLocalPosition().x - x
									- grabOffset.x) / getZoom()
								+ (component.getLocalPosition().y - y
									- grabOffset.y) / getZoom();
						} else if (selectedAxis == AXIS_ROTATION) {
							newRotation = new Vector2(x, y)
								.sub(component.getLocalPosition()).angle();
						}
						newRotation = newRotation % 360;

						// Stepping when Control is Pressed
						final float step = 22.5f;
						if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT))
							newRotation = step
								* (Math.round(newRotation / step));

						component.setLocalRotation(newRotation);

						float subRot = (component.getLocalRotation()
							- oldRotation);
						circleRegion = GizmoHandler
							.createCircleRegion(Math.abs(subRot) > 180
								? (subRot - 360 * ((subRot > 0) ? 1 : -1))
									% 360
								: subRot);
						break;
					case EventType.MouseDown:
						oldRotation = component.getLocalRotation();
						grabOffset.set(component.getLocalPosition().x - x,
							component.getLocalPosition().y - y);
						circleRegion = GizmoHandler.createCircleRegion(0);
						break;
					case EventType.MouseMoved:
						Vector2 endpoint = component.getPosition().cpy()
							.add(new Vector2(ARROW_LENGTH * getZoom(), 0)
								.rotate(component.getLocalRotation()));

						float dst = component.getPosition().dst(x, y);
						float dstRing = Math
							.abs(dst - ARROW_LENGTH * getZoom());
						float dstAxis = GeometryUtils.dstFromLine(
							component.getPosition(), endpoint,
							new Vector2(x, y));

						boolean isNearAxis = (dstAxis < MINIMUM_DST
							* getZoom())
							&& GeometryUtils.isInBoundingBox(
								component.getPosition(), endpoint,
								new Vector2(x, y), 10 * getZoom());

						if (dstRing < MINIMUM_DST * getZoom()) {
							selectedAxis = RING;
							return dstRing;
						} else if (dst < ARROW_LENGTH * getZoom()
							&& isNearAxis) {
							selectedAxis = AXIS_ROTATION;
							return dstAxis;
						} else {
							selectedAxis = 0;
							return -1;
						}
				}
				break;
			case Scale:
				switch (eventType) {
					case EventType.MouseDrag:
						float newX = ((AXIS_X & selectedAxis) > 0)
							? ((x - component.getPosition().x)
								- grabOffset.x) + oldScale.x
							: component.getLocalScale().x;
						float newY = ((AXIS_Y & selectedAxis) > 0)
							? ((y - component.getPosition().y)
								- grabOffset.y) + oldScale.y
							: component.getLocalScale().y;

						// Stepping
						if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) {
							if ((AXIS_X & selectedAxis) > 0)
								newX = (int)newX;
							if ((AXIS_Y & selectedAxis) > 0)
								newY = (int)newY;
						}

						component.setLocalScale(newX, newY);
						break;
					case EventType.MouseDown:
						grabOffset.set(x - component.getPosition().x,
							y - component.getPosition().y);
						oldScale.set(component.getLocalScale());
						break;
					case EventType.MouseMoved:
						float dstXY = component.getPosition().dst(x, y);
						float dstX = (x > positionX
							&& x < positionX + ARROW_LENGTH * getZoom())
								? Math.abs(positionY - y) : Float.MAX_VALUE;

						float dstY = (y > positionY
							&& y < positionY + ARROW_LENGTH * getZoom())
								? Math.abs(positionX - x) : Float.MAX_VALUE;

						if (dstXY < MINIMUM_DST * getZoom()) {
							selectedAxis = AXIS_XY;
							return dstXY;
						} else if (dstX <= dstY
							&& dstX < MINIMUM_DST * getZoom()) {
							selectedAxis = AXIS_X;
							return dstX;
						} else if (dstY < MINIMUM_DST * getZoom()) {
							selectedAxis = AXIS_Y;
							return dstY;
						}
				}
				break;
		}
		return -1f;
	}

	@Override
	public boolean isInBoundingBox (Vector2 coord) {
		return false;
	}
}
