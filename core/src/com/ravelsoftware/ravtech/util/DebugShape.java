package com.ravelsoftware.ravtech.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public abstract class DebugShape {

    Color color;

    public DebugShape(Color color) {
        this.color = color;
    }

    public abstract void draw (ShapeRenderer renderer);
}
