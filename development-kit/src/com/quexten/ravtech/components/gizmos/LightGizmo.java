
package com.quexten.ravtech.components.gizmos;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.quexten.ravtech.components.Light;
import com.quexten.ravtech.components.Light.LightType;
import com.quexten.ravtech.graphics.PolygonShapeRenderer;
import com.quexten.ravtech.util.EventType;
import com.quexten.ravtech.util.GeometryUtils;

public class LightGizmo extends Gizmo<Light> {

	boolean raySelected;

	public LightGizmo (Light light) {
		super(light);
	}

	@Override
	public void draw (PolygonShapeRenderer renderer, boolean selected) {
		if (component.getLightType() == LightType.ConeLight || component.getLightType() == LightType.PointLight) {
			renderer.setColor(selected && !raySelected ? Color.YELLOW : Color.CYAN);
			drawCone(renderer);
			renderer.setColor(selected && raySelected ? Color.YELLOW : Color.CYAN);
		} else if (component.getLightType() == LightType.ChainLight) {
			((box2dLight.RavChainLight)component.getLight()).debugRender(renderer);
		} else {
			renderer.setColor(Color.CYAN);
			Vector2 origin = new Vector2(component.getParent().transform.getPosition().x,
				component.getParent().transform.getPosition().y);
			float rotation = component.getParent().transform.getRotation();
			Vector2 addition = new Vector2(1, 0).rotate(rotation);
			renderer.line(origin.x - 2, origin.y, origin.x - 2 + addition.x, origin.y + addition.y);
			renderer.line(origin.x - 1, origin.y, origin.x - 1 + addition.x, origin.y + addition.y);
			renderer.line(origin.x + 0, origin.y, origin.x + 0 + addition.x, origin.y + addition.y);
			renderer.line(origin.x + 1, origin.y, origin.x + 1 + addition.x, origin.y + addition.y);
			renderer.line(origin.x + 2, origin.y, origin.x + 2 + addition.x, origin.y + addition.y);
		}
	}

	private void drawCone (PolygonShapeRenderer renderer) {

		boolean isConeLight = component.getLightType() == LightType.ConeLight;
		Vector2 origin = new Vector2(component.getParent().transform.getPosition().x,
			component.getParent().transform.getPosition().y);
		int indexcount = component.getRayCount() + ((component.getLightType() == LightType.PointLight) ? 1 : 0);
		float[] indicies = new float[indexcount * 2 + (isConeLight ? 4 : 0)];
		Color coneColor = renderer.getColor();
		renderer.setColor(0.2f, 0.2f, 0.2f, 0.5f);

		for (int i = 0; i < indexcount; i++) {
			float offset = component.getAngle() * 2 * (i / (indexcount - 1f) - 0.5f);
			Vector2 endpoint = new Vector2(
				origin.x + (float)Math.cos(Math.toRadians(component.getParent().transform.getRotation() + offset))
					* component.getDistance(),
				origin.y + (float)Math.sin(Math.toRadians(component.getParent().transform.getRotation() + offset))
					* component.getDistance());
			indicies[2 * i] = endpoint.x;
			indicies[2 * i + 1] = endpoint.y;

			renderer.line(origin, endpoint);
		}

		if (isConeLight) {
			indicies[indicies.length - 4] = origin.x;
			indicies[indicies.length - 3] = origin.y;
			indicies[indicies.length - 2] = indicies[0];
			indicies[indicies.length - 1] = indicies[1];
		}

		renderer.setColor(coneColor);
		renderer.polyline(indicies);
	}

	private Vector2 getRayEndpoint (Vector2 origin, float degrees) {
		return new Vector2(
			origin.x
				+ (float)Math.cos(Math.toRadians(component.getParent().transform.getRotation() + degrees)) * component.getDistance(),
			origin.y
				+ (float)Math.sin(Math.toRadians(component.getParent().transform.getRotation() + degrees)) * component.getDistance());
	}

	@Override
	public float input (float x, float y, int button, int eventType) {
		Vector2 origin = new Vector2(component.getParent().transform.getPosition().x,
			component.getParent().transform.getPosition().y);
		Vector2 worldPosition = new Vector2(x, y);
		switch (eventType) {
			case EventType.MouseMoved:
				float coneDst = Math.abs(origin.dst(worldPosition) - component.getDistance());
				float rayDst = GeometryUtils.isInBoundingBox(origin, getRayEndpoint(origin, component.getAngle()), worldPosition, 1)
					? GeometryUtils.dstFromLine(origin, getRayEndpoint(origin, component.getAngle()), worldPosition) : Float.MAX_VALUE;
				float rayDst2 = GeometryUtils.isInBoundingBox(origin, getRayEndpoint(origin, -component.getAngle()), worldPosition, 1)
					? GeometryUtils.dstFromLine(origin, getRayEndpoint(origin, -component.getAngle()), worldPosition)
					: Float.MAX_VALUE;
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
					component.setVariable(component.getVariableId("distance"), origin.dst(worldPosition));
				else {
					float angle = worldPosition.cpy().sub(origin).angle();
					float rotation = component.getParent().transform.getRotation();
					angle = angle - rotation;
					if (angle < 0)
						angle += 360;
					angle = angle > 180 ? 180 - Math.abs(angle - 180) : angle;
					component.setVariable(0, angle);
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
