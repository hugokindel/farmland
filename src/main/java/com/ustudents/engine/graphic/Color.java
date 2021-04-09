package com.ustudents.engine.graphic;

import com.ustudents.engine.core.json.annotation.JsonSerializable;
import org.joml.Vector4f;

import java.math.BigDecimal;
import java.math.RoundingMode;

@JsonSerializable
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

    @JsonSerializable
    public Float r;

    @JsonSerializable
    public Float g;

    @JsonSerializable
    public Float b;

    @JsonSerializable
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

    public Color(float[] color) {
        this.r = color[0];
        this.g = color[1];
        this.b = color[2];
        this.a = color[3];
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

    public static Color rgbToColor(int value) {
        Color color = new Color();
        color.r = ((value & 0xFF000000) >>> 24) / 255.0f;
        color.g = ((value & 0x00FF0000) >>> 16) / 255.0f;
        color.b = ((value & 0x0000FF00) >>> 8) / 255.0f;
        color.a = 1.0f;
        return color;
    }

    public static Color rgbaToColor(int value) {
        Color color = new Color();
        color.r = ((value & 0xFF000000) >>> 24) / 255.0f;
        color.g = ((value & 0x00FF0000) >>> 16) / 255.0f;
        color.b = ((value & 0x0000FF00) >>> 8) / 255.0f;
        color.a = ((value & 0x000000FF)) / 255.0f;
        return color;
    }

    public static void rgbaToColor(Color color, int value) {
        color.r = ((value & 0xFF000000) >>> 24) / 255.0f;
        color.g = ((value & 0x00FF0000) >>> 16) / 255.0f;
        color.b = ((value & 0x0000FF00) >>> 8) / 255.0f;
        color.a = ((value & 0x000000FF)) / 255.0f;
    }

    public boolean equals(Color color) {
        return r.floatValue() == color.r.floatValue() &&
               g.floatValue() == color.g.floatValue() &&
               b.floatValue() == color.b.floatValue() &&
               a.floatValue() == color.a.floatValue();
    }

    // Implementation from: https://css-tricks.com/converting-color-spaces-in-javascript/
    public ColorHsla toHsla() {
        float cmin = Math.min(r, Math.min(g, b));
        float cmax = Math.max(r, Math.min(g, b));
        float delta = cmax - cmin;
        float h = 0;
        float s = 0;
        float l = 0;

        if (delta == 0) {
            h = 0;
        } else if (cmax == r) {
            h = ((g - b) / delta) % 6;
        } else if (cmax == g) {
            h = (b - r) / delta + 2;
        } else {
            h = (r - g) / delta + 4;
        }

        h = Math.round(h * 60);

        if (h < 0) {
            h += 360;
        }

        l = (cmax + cmin) / 2;

        s = delta == 0 ? 0 : delta / (1 - Math.abs(2 * l - 1));

        s = BigDecimal.valueOf(s * 100).setScale(1, RoundingMode.HALF_UP).floatValue();
        l = BigDecimal.valueOf(l * 100).setScale(1, RoundingMode.HALF_UP).floatValue();

        return new ColorHsla(h, s / 100, l / 100, a);
    }

    public Color darken(int percent) {
        set(toHsla().darken(percent).toRgba());
        return this;
    }

    public Color lighten(int percent) {
        set(toHsla().lighten(percent).toRgba());
        return this;
    }

    @Override
    public String toString() {
        return "Color{" +
                "r=" + r +
                ", g=" + g +
                ", b=" + b +
                ", a=" + a +
                '}';
    }
}
