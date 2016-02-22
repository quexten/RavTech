package com.ravelsoftware.ravtech.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class JsonUtil {

    public static void writeColorToJson (Json json, Color color, String name) {
        json.writeObjectStart(name);
        json.writeValue("r", color.r);
        json.writeValue("g", color.g);
        json.writeValue("b", color.b);
        json.writeValue("a", color.a);
        json.writeObjectEnd();
    }

    public static Color readColorFromJson (JsonValue jsonData, String name) {
        JsonValue colordata = jsonData.getChild(name);
        if (colordata == null) return null;
        float r = 0;
        float g = 0;
        float b = 0;
        float a = 0;
        boolean hasnext = true;
        while (hasnext) {
            if (colordata.name().equals("r"))
                r = colordata.asFloat();
            else if (colordata.name().equals("g"))
                g = colordata.asFloat();
            else if (colordata.name().equals("b"))
                b = colordata.asFloat();
            else if (colordata.name().equals("a")) a = colordata.asFloat();
            colordata = colordata.next();
            hasnext = colordata != null;
        }
        return new Color(r, g, b, a);
    }
}
