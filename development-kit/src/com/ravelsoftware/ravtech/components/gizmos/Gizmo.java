
package com.ravelsoftware.ravtech.components.gizmos;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public abstract class Gizmo {

	/** Describes whether the Gizmo needs Exclusive rights to be edited, or will be editable in general selection mode */
	public boolean isExclusive = false;

	/** Draws the gizmo
	 * @param renderer - the shapeRenderer
	 * @param batch - the spriteBatch
	 * @param selected - whether the gizmo is selected */
	public abstract void draw (ShapeRenderer renderer, boolean selected);

	/** Handles the given mouse input
	 * @param x - the x coordinate in world coordinate space
	 * @param y - the y coordinate in world coordinate space
	 * @param button - the mouse button the event was performed with (e.g Left / Right / Middle)
	 * @param eventType - the type of the event (MouseDown, MouseDrag, MouseUp)
	 * @return returns the distance to the component, < 0 if not handled */
	public abstract float input (float x, float y, int button, int eventType);

	/** Checks whether the given coordinate is within the bounding box (drag area) of the gizmo
	 * @param coord - the coordinate to check
	 * @return Whether the coordinate is in the bounding box. */
	public abstract boolean isInBoundingBox (Vector2 coord);
}
