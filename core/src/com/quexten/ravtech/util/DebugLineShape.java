
package com.ravelsoftware.ravtech.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class DebugLineShape extends DebugShape {

	Vector2 start, end;

	public DebugLineShape (Vector2 start, Vector2 end, Color color) {
		super(color);
		this.start = start;
		this.end = end;
	}

	public DebugLineShape (Vector2 start, float direction,
		Color color) {
		super(color);
		this.start = start;
		end = start.add(new Vector2(
			MathUtils.cos(direction * MathUtils.degreesToRadians),
			MathUtils.sin(direction * MathUtils.degreesToRadians))
				.scl(Float.MAX_VALUE));
	}

	@Override
	public void draw (ShapeRenderer renderer) {
		renderer.setColor(color);
		renderer.line(start, end);
	}
}
