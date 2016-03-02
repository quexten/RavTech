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

	public DebugLineShape (Vector2 start, float direction, Color color) {
		super(color);
		this.start = start;
		this.end = start.add(
			new Vector2(MathUtils.cos(direction * MathUtils.degreesToRadians), MathUtils.sin(direction * MathUtils.degreesToRadians))
				.scl(Float.MAX_VALUE));
	}

	@Override
	public void draw (ShapeRenderer renderer) {
		renderer.setColor(color);
		renderer.line(start, end);
	}
}
