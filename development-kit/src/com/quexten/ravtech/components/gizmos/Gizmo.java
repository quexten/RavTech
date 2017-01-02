
package com.quexten.ravtech.components.gizmos;

import com.badlogic.gdx.math.Vector2;
import com.quexten.ravtech.components.GameComponent;
import com.quexten.ravtech.dk.RavTechDK;
import com.quexten.ravtech.graphics.PolygonShapeRenderer;

public abstract class Gizmo<T extends GameComponent> {
	
	public GizmoHandler handler;
	public T component;
	
	/** Describes whether the Gizmo needs Exclusive rights to be edited, or will be editable in general selection mode */
	public boolean isExclusive = false;
	
	public Gizmo (GizmoHandler handler, T component) {
		this.handler = handler;
		this.component = component;
	}

	/** Draws the gizmo
	 * @param batch - the polygon batch
	 * @param selected - whether the gizmo is selected */
	public abstract void draw (PolygonShapeRenderer batch, boolean selected);

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

	protected float getZoom () {
		return handler.sceneView.camera.zoom;
	}

}
