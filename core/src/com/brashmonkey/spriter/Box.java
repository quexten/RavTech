
package com.brashmonkey.spriter;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.brashmonkey.spriter.Entity.ObjectInfo;

/** Represents a box, which consists of four points: top-left, top-right, bottom-left and bottom-right. A box is responsible for
 * checking collisions and calculating a bounding box for a {@link Timeline.Key.Bone}.
 * 
 * @author Trixt0r */
public class Box {
	public final Vector2[] points;
	private Rectangle rect;

	/** Creates a new box with no witdh and height. */
	public Box () {
		this.points = new Vector2[4];
		// this.temp = new Vector2[4];
		for (int i = 0; i < 4; i++) {
			this.points[i] = new Vector2(0, 0);
			// this.temp[i] = new Vector2(0,0);
		}
		this.rect = new Rectangle(0, 0, 0, 0);
	}

	/** Calculates its four points for the given bone or object with the given info.
	 * 
	 * @param boneOrObject the bone or object
	 * @param info the info
	 * @throws NullVector2erException if info or boneOrObject is <code>null</code> */
	public void calcFor (Timeline.Key.Bone boneOrObject, ObjectInfo info) {
		float width = info.size.width * boneOrObject.scale.x;
		float height = info.size.height * boneOrObject.scale.y;

		float pivotX = width * boneOrObject.pivot.x;
		float pivotY = height * boneOrObject.pivot.y;

		this.points[0].set(-pivotX, -pivotY);
		this.points[1].set(width - pivotX, -pivotY);
		this.points[2].set(-pivotX, height - pivotY);
		this.points[3].set(width - pivotX, height - pivotY);

		for (int i = 0; i < 4; i++)
			this.points[i].rotate(boneOrObject.angle);
		for (int i = 0; i < 4; i++)
			this.points[i].add(boneOrObject.position);
	}

	/** Returns whether the given coordinates lie inside the box of the given bone or object.
	 * 
	 * @param boneOrObject the bone or object
	 * @param info the object info of the given bone or object
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return <code>true</code> if the given Vector2 lies in the box
	 * @throws NullVector2erException if info or boneOrObject is <code>null</code> */
	public boolean collides (Timeline.Key.Bone boneOrObject, ObjectInfo info, float x, float y) {
		float width = info.size.width * boneOrObject.scale.x;
		float height = info.size.height * boneOrObject.scale.y;

		float pivotX = width * boneOrObject.pivot.x;
		float pivotY = height * boneOrObject.pivot.y;

		Vector2 Vector2 = new Vector2(x - boneOrObject.position.x, y - boneOrObject.position.y);
		Vector2.rotate(-boneOrObject.angle);

		return Vector2.x >= -pivotX && Vector2.x <= width - pivotX && Vector2.y >= -pivotY && Vector2.y <= height - pivotY;
	}

	/** Returns whether this box is inside the given rectangle.
	 * 
	 * @param rect the rectangle
	 * @return <code>true</code> if one of the four points is inside the rectangle */
	public boolean isInside (Rectangle rect) {
		boolean inside = false;
		for (Vector2 p : points)
			inside |= rect.contains(p);
		return inside;
	}

	public Vector2 getBoundingRect () {
		return new Vector2();
	}

}
