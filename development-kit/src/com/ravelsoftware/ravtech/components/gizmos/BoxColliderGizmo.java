
package com.ravelsoftware.ravtech.components.gizmos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.components.BoxCollider;
import com.ravelsoftware.ravtech.dk.ui.utils.ColorUtils;
import com.ravelsoftware.ravtech.util.Debug;
import com.ravelsoftware.ravtech.util.EventType;
import com.ravelsoftware.ravtech.util.GeometryUtils;

public class BoxColliderGizmo extends Gizmo {

	BoxCollider boxCollider;
	boolean isGrabbed = false;
	int grabbedPoint = 0;
	Vector2 oldPosition;
	Vector2 trueOldPosition;
	Vector2 oldBounds;
	boolean canEdit = true;
	float closestDst;
	int selectedPoint;

	public BoxColliderGizmo (BoxCollider boxCollider) {
		this.boxCollider = boxCollider;
		this.isExclusive = true;
	}

	@Override
	public void draw (ShapeRenderer renderer, boolean selected) {
		if (!canEdit) return;
		renderer.setAutoShapeType(true);
		renderer.setColor(ColorUtils.getGizmoColor(this.boxCollider));
		float rotation = boxCollider.getParent().transform.getRotation();
		Vector2 middlePosition = boxCollider.getParent().transform.getPosition()
			.add(new Vector2((boxCollider.x), (boxCollider.y)).rotate(rotation));
		Vector2 tl = middlePosition.cpy().add(new Vector2(boxCollider.width / 2, boxCollider.height / 2).rotate(+rotation));
		Vector2 tr = middlePosition.cpy().add(new Vector2(boxCollider.width / 2, -boxCollider.height / 2).rotate(+rotation));
		Vector2 br = middlePosition.cpy().sub(new Vector2(boxCollider.width / 2, boxCollider.height / 2).rotate(+rotation));
		Vector2 bl = middlePosition.cpy().sub(new Vector2(boxCollider.width / 2, -boxCollider.height / 2).rotate(+rotation));
		// tl
		Vector2 tlb = tl.cpy().interpolate(bl, 0.25f, Interpolation.linear);
		Vector2 tlr = tl.cpy().interpolate(tr, 0.25f, Interpolation.linear);
		// tr
		Vector2 trb = tr.cpy().interpolate(br, 0.25f, Interpolation.linear);
		Vector2 trl = tr.cpy().interpolate(tl, 0.25f, Interpolation.linear);
		// br
		Vector2 brt = br.cpy().interpolate(tr, 0.25f, Interpolation.linear);
		Vector2 brl = br.cpy().interpolate(bl, 0.25f, Interpolation.linear);
		// bl
		Vector2 blt = bl.cpy().interpolate(tl, 0.25f, Interpolation.linear);
		Vector2 blr = bl.cpy().interpolate(br, 0.25f, Interpolation.linear);
		renderer.line(tl, tr);
		renderer.line(tr, br);
		renderer.line(br, bl);
		renderer.line(bl, tl);
		renderer.end();
		renderer.begin(ShapeType.Line);
		if (selected) {
			Gdx.gl.glLineWidth(4);
			renderer.setColor(Color.YELLOW);
		}
		switch (selectedPoint) {
		case 0:
			renderer.line(brt, br);
			renderer.line(brl, br);
			break;
		case 1:
			renderer.line(blt, bl);
			renderer.line(blr, bl);
			break;
		case 2:
			renderer.line(tlb, tl);
			renderer.line(tlr, tl);
			break;
		case 3:
			renderer.line(trb, tr);
			renderer.line(trl, tr);
			break;
		case 4:
			renderer.line(bl, tl);
			break;
		case 5:
			renderer.line(tl, tr);
			break;
		case 6:
			renderer.line(tr, br);
			break;
		case 7:
			renderer.line(br, bl);
			break;
		}
		renderer.setColor(Color.GRAY);
		renderer.end();
		renderer.begin(ShapeType.Line);
		Gdx.gl.glLineWidth(1);
		renderer.setColor(Color.GRAY);
	}

	@Override
	public float input (float x, float y, int button, int eventType) {
		float rotation = boxCollider.getParent().transform.getRotation();
		Vector2 middlePosition = boxCollider.getParent().transform.getPosition()
			.sub(new Vector2(-boxCollider.x, -boxCollider.y).rotate(rotation));
		Vector2 mousePosition = new Vector2(x, y);
		switch (eventType) {
		case EventType.MouseMoved:
			Vector2 tl = middlePosition.cpy().sub(new Vector2(boxCollider.width / 2, boxCollider.height / 2).rotate(+rotation));
			Vector2 tr = middlePosition.cpy().sub(new Vector2(boxCollider.width / 2, -boxCollider.height / 2).rotate(+rotation));
			Vector2 br = middlePosition.cpy().add(new Vector2(boxCollider.width / 2, boxCollider.height / 2).rotate(+rotation));
			Vector2 bl = middlePosition.cpy().add(new Vector2(boxCollider.width / 2, -boxCollider.height / 2).rotate(+rotation));
			closestDst = Float.MAX_VALUE;
			Array<Vector2> positions = new Array<Vector2>();
			positions.add(tl);
			positions.add(tr);
			positions.add(br);
			positions.add(bl);
			float camFactor = RavTech.sceneHandler.worldCamera.zoom * 20f;
			float lDst = GeometryUtils.isInBoundingBox(tl, tr, mousePosition, camFactor)
				? GeometryUtils.dstFromLine(tl, tr, mousePosition) : Float.MAX_VALUE;
			float tDst = GeometryUtils.isInBoundingBox(tr, br, mousePosition, camFactor)
				? GeometryUtils.dstFromLine(tr, br, mousePosition) : Float.MAX_VALUE;
			float rDst = GeometryUtils.isInBoundingBox(br, bl, mousePosition, camFactor)
				? GeometryUtils.dstFromLine(br, bl, mousePosition) : Float.MAX_VALUE;
			float bDst = GeometryUtils.isInBoundingBox(bl, tl, mousePosition, camFactor)
				? GeometryUtils.dstFromLine(bl, tl, mousePosition) : Float.MAX_VALUE;
			if (closestDst > tDst) {
				selectedPoint = 4;
				closestDst = tDst;
			}
			if (closestDst > rDst) {
				selectedPoint = 5;
				closestDst = rDst;
			}
			if (closestDst > bDst) {
				selectedPoint = 6;
				closestDst = bDst;
			}
			if (closestDst > lDst) {
				selectedPoint = 7;
				closestDst = lDst;
			}
			for (int i = 0; i < positions.size; i++)
				if (camFactor > positions.get(i).dst(mousePosition)) {
					closestDst = positions.get(i).dst(mousePosition);
					selectedPoint = i;
				}
			if (closestDst > camFactor) return -1f;
			break;
		case EventType.MouseDown:
			oldPosition = boxCollider.getParent().transform.getPosition()
				.sub(new Vector2(-boxCollider.x * 2, -boxCollider.y * 2).rotate(rotation));
			middlePosition = oldPosition;
			oldPosition = boxCollider.getParent().transform.getPosition()
				.sub(new Vector2(-boxCollider.x, -boxCollider.y).rotate(rotation));
			trueOldPosition = boxCollider.getParent().transform.getPosition()
				.sub(new Vector2(-boxCollider.x * 2, -boxCollider.y * 2).rotate(0));
			oldBounds = new Vector2(boxCollider.width, boxCollider.height);
			isGrabbed = true;
			grabbedPoint = selectedPoint;
			return -1f;
		case EventType.MouseDrag:
			if (isGrabbed) switch (grabbedPoint) {
			case 0: // tl
				changeBounds(mousePosition.cpy().sub(oldPosition).rotate(-boxCollider.getParent().transform.getRotation()).x,
					mousePosition.cpy().sub(oldPosition).rotate(-boxCollider.getParent().transform.getRotation()).y, false, false);
				break;
			case 1: // tr
				changeBounds(mousePosition.cpy().sub(oldPosition).rotate(-boxCollider.getParent().transform.getRotation()).x,
					mousePosition.cpy().sub(oldPosition).rotate(-boxCollider.getParent().transform.getRotation()).y, false, true);
				break;
			case 2: // br
				changeBounds(mousePosition.cpy().sub(oldPosition).rotate(-boxCollider.getParent().transform.getRotation()).x,
					mousePosition.cpy().sub(oldPosition).rotate(-boxCollider.getParent().transform.getRotation()).y, true, true);
				break;
			case 3: // bl
				changeBounds(mousePosition.cpy().sub(oldPosition).rotate(-boxCollider.getParent().transform.getRotation()).x,
					mousePosition.cpy().sub(oldPosition).rotate(-boxCollider.getParent().transform.getRotation()).y, true, false);
				break;
			case 4: // t
				changeHeight(mousePosition.sub(oldPosition).rotate(-boxCollider.getParent().transform.getRotation()).y, true);
				break;
			case 5: // r
				changeWidth(mousePosition.sub(oldPosition).rotate(-boxCollider.getParent().transform.getRotation()).x, true);
				break;
			case 6: // b
				changeHeight(mousePosition.sub(oldPosition).rotate(-boxCollider.getParent().transform.getRotation()).y, false);
				break;
			case 7: // l
				changeWidth(mousePosition.sub(oldPosition).rotate(-boxCollider.getParent().transform.getRotation()).x, false);
				break;
			case 8:
				Vector2 subPosition = mousePosition.sub(boxCollider.getParent().transform.getPosition());
				boxCollider.x = subPosition.x / boxCollider.width * 2;
				boxCollider.y = subPosition.y / boxCollider.height * 2;
				break;
			}
			return -1f;
		case EventType.MouseUp:
			isGrabbed = false;
			break;
		}
		return this.closestDst;
	}

	private void changeWidth (float width, boolean changeRight) {
		Debug.log("OldBounds", oldBounds);
		Debug.log("TrueOLdPosition", trueOldPosition);
		width = (changeRight ? 1f : -1f) * (width - oldBounds.x * 0.5f) + oldBounds.x * (changeRight ? 1 : 0);
		Vector2 addPosition = new Vector2(changeRight ? width + -oldBounds.x : oldBounds.x - width, 0);
		boxCollider.x = trueOldPosition.cpy().add(addPosition).sub(boxCollider.getParent().transform.getPosition()).x / 2;
		boxCollider.width = width;
	}

	private void changeHeight (float height, boolean changeTop) {
		height = (changeTop ? 1 : -1f) * (height - oldBounds.y * 0.5f) + oldBounds.y * (changeTop ? 1 : 0);
		Vector2 addPosition = new Vector2(0, changeTop ? height + -oldBounds.y : oldBounds.y - height);
		boxCollider.y = trueOldPosition.cpy().add(addPosition).sub(boxCollider.getParent().transform.getPosition()).y / 2;
		boxCollider.height = height;
	}

	private void changeBounds (float width, float height, boolean changeRight, boolean changeTop) {
		width = (changeRight ? 1f : -1f) * (width - oldBounds.x * 0.5f) + oldBounds.x * (changeRight ? 1 : 0);
		Vector2 addPosition = new Vector2(changeRight ? width + -oldBounds.x : oldBounds.x - width, 0);
		boxCollider.x = trueOldPosition.cpy().add(addPosition).sub(boxCollider.getParent().transform.getPosition()).x;
		boxCollider.y = -trueOldPosition.cpy().add(addPosition).sub(boxCollider.getParent().transform.getPosition()).y;
		height = (changeTop ? 1 : -1f) * (height - oldBounds.y * 0.5f) + oldBounds.y * (changeTop ? 1 : 0);
		Vector2 addPosition2 = new Vector2(0, changeTop ? height + -oldBounds.y : oldBounds.y - height);
		Vector2 newPosition = new Vector2(-boxCollider.x, -boxCollider.y);
		boxCollider.x = -newPosition.cpy().add(addPosition2).x / 2;
		boxCollider.y = newPosition.cpy().add(addPosition2).y / 2;
		boxCollider.width = width;
		boxCollider.height = height;
	}

	@Override
	public boolean isInBoundingBox (Vector2 coord) {
		return false;
	}
}
