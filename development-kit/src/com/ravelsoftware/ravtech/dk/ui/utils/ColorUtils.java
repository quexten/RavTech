package com.ravelsoftware.ravtech.dk.ui.utils;

import com.badlogic.gdx.graphics.Color;

public class ColorUtils {

    public static java.awt.Color gdxToSwing (Color c) {
        return new java.awt.Color(c.r, c.g, c.b, c.a);
    }

    public static Color swingToGdx (java.awt.Color c) {
        return new Color(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f, c.getAlpha() / 255.0f);
    }
}
