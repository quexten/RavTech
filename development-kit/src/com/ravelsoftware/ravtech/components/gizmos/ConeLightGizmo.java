
package com.ravelsoftware.ravtech.components.gizmos;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.ravelsoftware.ravtech.components.Light;
import com.ravelsoftware.ravtech.util.EventType;
import com.ravelsoftware.ravtech.util.GeometryUtils;

import box2dLight.ConeLight;

public class ConeLightGizmo extends Gizmo<Light> {

	Light light;
	ConeLight coneLight;
	boolean raySelected;

	public ConeLightGizmo (Light light) {
		super(light);
		this.light = light;
		coneLight = (ConeLight)light.light;
	}

	@Override
	public void draw (PolygonShapeRenderer renderer, boolean selected) {
		renderer.setColor(selected && !raySelected ? Color.YELLOW : Color.CYAN);
		drawCone(renderer);
		renderer.setColor(selected && raySelected ? Color.YELLOW : Color.CYAN);
		//drawRay(renderer, coneLight.getConeDegree());
		//drawRay(renderer, -coneLight.getConeDegree());
	}

	private void drawCone (PolygonShapeRenderer renderer) {
		Vector2 origin = new Vector2(light.getParent().transform.getPosition().x, light.getParent().transform.getPosition().y);
		int indexcount = 360;
		float[] indicies = new float[indexcount * 2];
		for (int i = 0; i < indexcount; i++) {
			float offset = coneLight.getConeDegree() * 2 * (i / (indexcount - 1f) - 0.5f);
			Vector2 endpoint = new Vector2(
				origin.x + (float)Math.cos(Math.toRadians(light.getParent().transform.getRotation() + offset))
					* coneLight.getDistance(),
				origin.y + (float)Math.sin(Math.toRadians(light.getParent().transform.getRotation() + offset))
					* coneLight.getDistance());
			indicies[2 * i] = endpoint.x;
			indicies[2 * i + 1] = endpoint.y;
		}
		renderer.polyline(indicies);
	}

	private void drawRay (ShapeRenderer renderer, float degrees) {
		Vector2 origin = new Vector2(light.getParent().transform.getPosition().x, light.getParent().transform.getPosition().y);
		renderer.line(origin, getRayEndpoint(origin, degrees));
	}

	private Vector2 getRayEndpoint (Vector2 origin, float degrees) {
		return new Vector2(
			origin.x + (float)Math.cos(Math.toRadians(light.getParent().transform.getRotation() + degrees)) * coneLight.getDistance(),
			origin.y + (float)Math.sin(Math.toRadians(light.getParent().transform.getRotation() + degrees)) * coneLight.getDistance());
	}

	@Override
	public float input (float x, float y, int button, int eventType) {
		Vector2 origin = new Vector2(light.getParent().transform.getPosition().x, light.getParent().transform.getPosition().y);
		Vector2 worldPosition = new Vector2(x, y);
		switch (eventType) {
		case EventType.MouseMoved:
			float coneDst = Math.abs(origin.dst(worldPosition) - coneLight.getDistance());
			float rayDst = GeometryUtils.isInBoundingBox(origin, getRayEndpoint(origin, light.angle), worldPosition, 1)
				? GeometryUtils.dstFromLine(origin, getRayEndpoint(origin, light.angle), worldPosition) : Float.MAX_VALUE;
			float rayDst2 = GeometryUtils.isInBoundingBox(origin, getRayEndpoint(origin, -light.angle), worldPosition, 1)
				? GeometryUtils.dstFromLine(origin, getRayEndpoint(origin, -light.angle), worldPosition) : Float.MAX_VALUE;
			if (rayDst > rayDst2)
				rayDst = rayDst2;
			if (coneDst < rayDst && coneDst < 1f) {
				raySelected = false;
				return coneDst;
			} else if (rayDst < coneDst && rayDst < 1f) {
				raySelected = true;
				return rayDst;
			} else
				return -1f;
		case EventType.MouseDown:
			break;
		case EventType.MouseDrag:
			if (!raySelected)
				light.setVariable(1, origin.dst(worldPosition));
			else {
				float angle = worldPosition.cpy().sub(origin).angle();
				float rotation = light.getParent().transform.getRotation();
				angle = angle - rotation;
				if (angle < 0)
					angle += 360;
				angle = angle > 180 ? 180 - Math.abs(angle - 180) : angle;
				light.setVariable(0, angle);
			}
			break;
		case EventType.MouseUp:
			break;
		}
		return -1f;
	}

	@Override
	public boolean isInBoundingBox (Vector2 coord) {
		return false;
	}

}
