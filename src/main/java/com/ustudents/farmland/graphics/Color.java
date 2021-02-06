package com.ustudents.farmland.graphics;

public class Color {
    public float r;
    public float g;
    public float b;
    public float a;

    public Color() {
        r = 0;
        g = 0;
        b = 0;
        a = 0;
    }

    public Color(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
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
}
