package com.ustudents.engine.graphic;

import org.joml.Vector4f;

public class Color {
    public static final Color CLEAR = new Color(0x00000000);
    public static final Color WHITE = new Color(0xFFFFFFFF);
    public static final Color BLACK = new Color(0x000000FF);
    public static final Color RED = new Color(0xFF0000FF);
    public static final Color GREEN = new Color(0x00FF00FF);
    public static final Color BLUE = new Color(0x0000FFFF);
    public static final Color LIGHT_GRAY = new Color(0xBFBFBFFF);
    public static final Color GRAY = new Color(0x7F7F7FFF);
    public static final Color DARK_GRAY = new Color(0x3F3F3FFF);
    public static final Color CYAN = new Color(0x00FFFFFF);
    public static final Color YELLOW = new Color(0xFFFF00FF);
    public static final Color MAGENTA = new Color(0xFF00FFFF);

    public Float r;

    public Float g;

    public Float b;

    public Float a;

    public Color() {
        this(0.0f, 0.0f, 0.0f, 1.0f);
    }

    public Color(float r, float g, float b) {
        this(r, g, b, 1.0f);
    }

    public Color(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public Color(int rgba) {
        rgbaToColor(this, rgba);
    }

    public Color(Color color) {
        set(color);
    }

    public void set(Color color) {
        r = color.r;
        g = color.g;
        b = color.b;
        a = color.a;
    }

    public void set(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public Vector4f toVector4f() {
        return new Vector4f(r, g, b, a);
    }

    public Color clone() {
        return new Color(r, g, b, a);
    }

    public static void rgbaToColor(Color color, int value) {
        color.r = ((value & 0xFF000000) >>> 24) / 255.0f;
        color.g = ((value & 0x00FF0000) >>> 16) / 255.0f;
        color.b = ((value & 0x0000FF00) >>> 8) / 255.0f;
        color.a = ((value & 0x000000FF)) / 255.0f;
    }
}
