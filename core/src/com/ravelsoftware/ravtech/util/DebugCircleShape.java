
package com.ravelsoftware.ravtech.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class DebugCircleShape extends DebugShape {

	Vector2 position;
	float radius;

	public DebugCircleShape (Vector2 position, float radius,
		Color color) {
		super(color);
		this.position = position;
		this.radius = radius;
	}

	@Override
	public void draw (ShapeRenderer renderer) {
		renderer.setColor(color);
		renderer.circle(position.x, position.y, radius, 20);
	}
}
