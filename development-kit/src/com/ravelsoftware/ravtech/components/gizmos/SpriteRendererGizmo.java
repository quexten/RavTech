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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.components.SpriteRenderer;
import com.ravelsoftware.ravtech.util.EventType;
import com.ravelsoftware.ravtech.util.GeometryUtils;

public class SpriteRendererGizmo extends Gizmo {

	SpriteRenderer spriteRenderer;
	boolean isGrabbed = false;
	int grabbedPoint = 0;
	Vector2 oldPosition;
	Vector2 trueOldPosition;
	Vector2 oldBounds;
	boolean canEdit = true;
	float ppuX; // Pixels per Unit
	float ppuY; // Pixels per Unit
	int oldSrcWidth;
	int oldSrcHeight;
	int oldSrcX;
	int oldSrcY;
	float closestDst;
	int selectedPoint;

	public SpriteRendererGizmo (SpriteRenderer spriteRenderer) {
		this.spriteRenderer = spriteRenderer;
	}

	@Override
	public void draw (ShapeRenderer renderer, boolean selected) {
		if (!canEdit) return;
		renderer.setAutoShapeType(true);
		renderer.setColor(Color.LIGHT_GRAY);
		float rotation = spriteRenderer.getParent().transform.getRotation();
		Vector2 middlePosition = spriteRenderer.getParent().transform.getPosition().sub(
			new Vector2(spriteRenderer.originX * (spriteRenderer.width / 2), spriteRenderer.originY * (spriteRenderer.height / 2))
				.rotate(rotation));
		Vector2 tl = middlePosition.cpy().sub(new Vector2(spriteRenderer.width / 2, spriteRenderer.height / 2).rotate(+rotation));
		Vector2 tr = middlePosition.cpy().sub(new Vector2(spriteRenderer.width / 2, -spriteRenderer.height / 2).rotate(+rotation));
		Vector2 br = middlePosition.cpy().add(new Vector2(spriteRenderer.width / 2, spriteRenderer.height / 2).rotate(+rotation));
		Vector2 bl = middlePosition.cpy().add(new Vector2(spriteRenderer.width / 2, -spriteRenderer.height / 2).rotate(+rotation));
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
			renderer.line(tlb, tl);
			renderer.line(tlr, tl);
			break;
		case 1:
			renderer.line(trb, tr);
			renderer.line(trl, tr);
			break;
		case 2:
			renderer.line(brt, br);
			renderer.line(brl, br);
			break;
		case 3:
			renderer.line(blt, bl);
			renderer.line(blr, bl);
			break;
		case 4:
			renderer.line(tr, br);
			break;
		case 5:
			renderer.line(br, bl);
			break;
		case 6:
			renderer.line(bl, tl);
			break;
		case 7:
			renderer.line(tl, tr);
			break;
		}
		renderer.setColor(Color.GRAY);
		renderer.end();
		renderer.begin(ShapeType.Line);
		Gdx.gl.glLineWidth(1);
		renderer.setColor(Color.GRAY);
	}

	@Override
	public float input (int button, int eventType) {
		float rotation = spriteRenderer.getParent().transform.getRotation();
		Vector2 middlePosition = spriteRenderer.getParent().transform.getPosition().sub(
			new Vector2(spriteRenderer.originX * (spriteRenderer.width / 2), spriteRenderer.originY * (spriteRenderer.height / 2))
				.rotate(rotation));
		Vector2 mousePosition = RavTech.input.getWorldPosition();
		switch (eventType) {
		case EventType.MouseMoved:
			Vector2 tl = middlePosition.cpy()
				.sub(new Vector2(spriteRenderer.width / 2, spriteRenderer.height / 2).rotate(+rotation));
			Vector2 tr = middlePosition.cpy()
				.sub(new Vector2(spriteRenderer.width / 2, -spriteRenderer.height / 2).rotate(+rotation));
			Vector2 br = middlePosition.cpy()
				.add(new Vector2(spriteRenderer.width / 2, spriteRenderer.height / 2).rotate(+rotation));
			Vector2 bl = middlePosition.cpy()
				.add(new Vector2(spriteRenderer.width / 2, -spriteRenderer.height / 2).rotate(+rotation));
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
			this.ppuX = spriteRenderer.srcWidth / spriteRenderer.width;
			this.ppuY = spriteRenderer.srcHeight / spriteRenderer.height;
			this.oldSrcWidth = spriteRenderer.srcWidth;
			this.oldSrcHeight = spriteRenderer.srcHeight;
			this.oldSrcX = spriteRenderer.srcX;
			this.oldSrcY = spriteRenderer.srcY;
			oldPosition = spriteRenderer.getParent().transform.getPosition().sub(
				new Vector2(spriteRenderer.originX * (spriteRenderer.width / 2), spriteRenderer.originY * (spriteRenderer.height / 2))
					.rotate(rotation));
			middlePosition = oldPosition;
			oldPosition = spriteRenderer.getParent().transform.getPosition().sub(
				new Vector2(spriteRenderer.originX * (spriteRenderer.width / 2), spriteRenderer.originY * (spriteRenderer.height / 2))
					.rotate(rotation));
			trueOldPosition = spriteRenderer.getParent().transform.getPosition().sub(
				new Vector2(spriteRenderer.originX * spriteRenderer.width, spriteRenderer.originY * spriteRenderer.height).rotate(0));
			oldBounds = new Vector2(spriteRenderer.width, spriteRenderer.height);
			isGrabbed = true;
			grabbedPoint = selectedPoint;
			return -1f;
		case EventType.MouseDrag:
			if (isGrabbed) switch (grabbedPoint) {
			case 0: // tl
				changeBounds(mousePosition.cpy().sub(oldPosition).rotate(-spriteRenderer.getParent().transform.getRotation()).x,
					mousePosition.cpy().sub(oldPosition).rotate(-spriteRenderer.getParent().transform.getRotation()).y, false, false);
				break;
			case 1: // tr
				changeBounds(mousePosition.cpy().sub(oldPosition).rotate(-spriteRenderer.getParent().transform.getRotation()).x,
					mousePosition.cpy().sub(oldPosition).rotate(-spriteRenderer.getParent().transform.getRotation()).y, false, true);
				break;
			case 2: // br
				changeBounds(mousePosition.cpy().sub(oldPosition).rotate(-spriteRenderer.getParent().transform.getRotation()).x,
					mousePosition.cpy().sub(oldPosition).rotate(-spriteRenderer.getParent().transform.getRotation()).y, true, true);
				break;
			case 3: // bl
				changeBounds(mousePosition.cpy().sub(oldPosition).rotate(-spriteRenderer.getParent().transform.getRotation()).x,
					mousePosition.cpy().sub(oldPosition).rotate(-spriteRenderer.getParent().transform.getRotation()).y, true, false);
				break;
			case 4: // t
				changeHeight(mousePosition.sub(oldPosition).rotate(-spriteRenderer.getParent().transform.getRotation()).y, true);
				break;
			case 5: // r
				changeWidth(mousePosition.sub(oldPosition).rotate(-spriteRenderer.getParent().transform.getRotation()).x, true);
				break;
			case 6: // b
				changeHeight(mousePosition.sub(oldPosition).rotate(-spriteRenderer.getParent().transform.getRotation()).y, false);
				break;
			case 7: // l
				changeWidth(mousePosition.sub(oldPosition).rotate(-spriteRenderer.getParent().transform.getRotation()).x, false);
				break;
			case 8:
				Vector2 subPosition = mousePosition.sub(spriteRenderer.getParent().transform.getPosition());
				spriteRenderer.originX = -subPosition.x / spriteRenderer.width * 2;
				spriteRenderer.originY = -subPosition.y / spriteRenderer.height * 2;
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
		width = (changeRight ? 1f : -1f) * (width - oldBounds.x * 0.5f) + oldBounds.x * (changeRight ? 1 : 0);
		Vector2 addPosition = new Vector2(changeRight ? width + -oldBounds.x : oldBounds.x - width, 0);
		spriteRenderer.originX = -trueOldPosition.cpy().add(addPosition).sub(spriteRenderer.getParent().transform.getPosition()).x
			/ width;
		spriteRenderer.originY = spriteRenderer.originY; // -trueOldPosition.cpy().add(addPosition).sub(spriteRenderer.getParent().transform.getPosition()).y
																			// /
																			// (spriteRenderer.height
																			// );
		spriteRenderer.width = width;
		if (spriteRenderer.uWrap.equals(TextureWrap.Repeat)) {
			spriteRenderer.srcWidth = (int)(ppuX * width);
			if (!changeRight) spriteRenderer.srcX = this.oldSrcX + this.oldSrcWidth - spriteRenderer.srcWidth;
		}
	}

	private void changeHeight (float height, boolean changeTop) {
		height = (changeTop ? 1 : -1f) * (height - oldBounds.y * 0.5f) + oldBounds.y * (changeTop ? 1 : 0);
		Vector2 addPosition = new Vector2(0, changeTop ? height + -oldBounds.y : oldBounds.y - height);
		spriteRenderer.originX = spriteRenderer.originX;
		spriteRenderer.originY = -trueOldPosition.cpy().add(addPosition).sub(spriteRenderer.getParent().transform.getPosition()).y
			/ height;
		spriteRenderer.height = height;
		if (spriteRenderer.vWrap.equals(TextureWrap.Repeat)) {
			spriteRenderer.srcHeight = (int)(ppuY * height);
			if (changeTop) spriteRenderer.srcY = this.oldSrcY + this.oldSrcHeight - spriteRenderer.srcHeight;
		}
	}

	private void changeBounds (float width, float height, boolean changeRight, boolean changeTop) {
		width = (changeRight ? 1f : -1f) * (width - oldBounds.x * 0.5f) + oldBounds.x * (changeRight ? 1 : 0);
		Vector2 addPosition = new Vector2(changeRight ? width + -oldBounds.x : oldBounds.x - width, 0);
		spriteRenderer.originX = -trueOldPosition.cpy().add(addPosition).sub(spriteRenderer.getParent().transform.getPosition()).x;
		spriteRenderer.originY = trueOldPosition.cpy().add(addPosition).sub(spriteRenderer.getParent().transform.getPosition()).y;
		height = (changeTop ? 1 : -1f) * (height - oldBounds.y * 0.5f) + oldBounds.y * (changeTop ? 1 : 0);
		Vector2 addPosition2 = new Vector2(0, changeTop ? height + -oldBounds.y : oldBounds.y - height);
		Vector2 newPosition = new Vector2(spriteRenderer.originX, spriteRenderer.originY);
		spriteRenderer.originX = newPosition.cpy().add(addPosition2).x / width;
		spriteRenderer.originY = -newPosition.cpy().add(addPosition2).y / height;
		spriteRenderer.width = width;
		spriteRenderer.height = height;
		if (spriteRenderer.uWrap.equals(TextureWrap.Repeat)) {
			spriteRenderer.srcWidth = (int)(ppuX * width);
			if (!changeRight) spriteRenderer.srcX = this.oldSrcX + this.oldSrcWidth - spriteRenderer.srcWidth;
		}
		if (spriteRenderer.vWrap.equals(TextureWrap.Repeat)) {
			spriteRenderer.srcHeight = (int)(ppuY * height);
			if (changeTop) spriteRenderer.srcY = this.oldSrcY + this.oldSrcHeight - spriteRenderer.srcHeight;
		}
	}

	@Override
	public boolean isInBoundingBox (Vector2 coord) {
		float rotation = spriteRenderer.getParent().transform.getRotation();
		Vector2 middlePosition = spriteRenderer.getParent().transform.getPosition().sub(
			new Vector2(spriteRenderer.originX * (spriteRenderer.width / 2), spriteRenderer.originY * (spriteRenderer.height / 2))
				.rotate(rotation));
		Vector2 bl = middlePosition.cpy().sub(new Vector2(spriteRenderer.width / 2, spriteRenderer.height / 2).rotate(+rotation));
		Vector2 tr = middlePosition.cpy().add(new Vector2(spriteRenderer.width / 2, spriteRenderer.height / 2).rotate(+rotation));
		return GeometryUtils.isInBoundingBox(bl, tr, coord, 0);
	}
}
