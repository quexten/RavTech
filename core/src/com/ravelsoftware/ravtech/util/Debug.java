package com.ravelsoftware.ravtech.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class Debug {

    static boolean debugPerformance = false;
    public static Array<DebugShape> debugLineShapes = new Array<DebugShape>();
    public static Array<DebugShape> debugFilledShapes = new Array<DebugShape>();

    public static void log (String tag, Object message) {
        Gdx.app.log(tag, String.valueOf(message));
    }

    public static void logError (String tag, Object message) {
        Gdx.app.error(tag, message.toString());
    }

    public static void logDebug (String tag, Object message) {
        Gdx.app.debug(tag, message.toString());
    }

    public static void drawRay (Vector2 start, float dir, Color color) {
        debugLineShapes.add(new DebugLineShape(start, dir, color));
    }

    public static void drawLine (Vector2 start, Vector2 end, Color color) {
        debugLineShapes.add(new DebugLineShape(start, end, color));
    }

    public static void drawCircle (Vector2 middlePosition, float radius, Color color, boolean filled) {
        (filled ? debugFilledShapes : debugLineShapes).add(new DebugCircleShape(middlePosition, radius, color));
    }

    public static void drawRectangle (Vector2 middlePosition, Vector2 size, Color color, boolean filled) {
        (filled ? debugFilledShapes : debugLineShapes).add(new DebugRectangle(middlePosition, size, color));
    }

    public static void render (ShapeRenderer renderer) {
        renderer.set(ShapeType.Filled);
        for (int i = 0; i < debugFilledShapes.size; i++) {
            DebugShape shape = debugFilledShapes.get(i);
            shape.draw(renderer);
        }
        renderer.set(ShapeType.Line);
        for (int i = 0; i < debugLineShapes.size; i++) {
            DebugShape shape = debugLineShapes.get(i);
            shape.draw(renderer);
        }
        renderer.end();
    }

    static ObjectMap<String, Long> map = new ObjectMap<String, Long>();

    public static void startTimer (String name) {
        map.put(name, 0L);
    }

    public static void endTimer (String name) {
        if (debugPerformance) Debug.log(name, map.get(name, 0L));
    }
}
