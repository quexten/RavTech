package com.ravelsoftware.ravtech.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class DebugRectangle extends DebugShape {

    Vector2 position, bounds;

    public DebugRectangle(Vector2 position, Vector2 bounds, Color color) {
        super(color);
        this.position = position;
        this.bounds = bounds.scl(2);
    }

    @Override
    public void draw (ShapeRenderer renderer) {
        renderer.setColor(color);
        renderer.rect(position.x - bounds.x / 2, position.y - bounds.y / 2.0f, bounds.x, bounds.y);
    }
}
