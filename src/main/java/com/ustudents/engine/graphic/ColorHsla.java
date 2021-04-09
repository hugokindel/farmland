package com.ustudents.engine.graphic;

import com.ustudents.engine.core.json.annotation.JsonSerializable;
import org.joml.Vector4f;

@JsonSerializable
public class ColorHsla {
    @JsonSerializable
    public Float h;

    @JsonSerializable
    public Float s;

    @JsonSerializable
    public Float l;

    @JsonSerializable
    public Float a;

    public ColorHsla() {
        this(0.0f, 0.0f, 0.0f, 1.0f);
    }

    public ColorHsla(float h, float s, float l) {
        this(h, s, l, 1.0f);
    }

    public ColorHsla(float h, float s, float l, float a) {
        this.h = h;
        this.s = s;
        this.l = l;
        this.a = a;
    }

    public ColorHsla(float[] color) {
        this.h = color[0];
        this.s = color[1];
        this.l = color[2];
        this.a = color[3];
    }

    public ColorHsla(ColorHsla color) {
        set(color);
    }

    public void set(ColorHsla color) {
        h = color.h;
        s = color.s;
        l = color.l;
        a = color.a;
    }

    public void set(float h, float s, float l, float a) {
        this.h = h;
        this.s = s;
        this.l = l;
        this.a = a;
    }

    public Vector4f toVector4f() {
        return new Vector4f(h, s, l, a);
    }

    public ColorHsla clone() {
        return new ColorHsla(h, s, l, a);
    }

    public boolean equals(ColorHsla colorHsla) {
        return h.floatValue() == colorHsla.h.floatValue() &&
                s.floatValue() == colorHsla.s.floatValue() &&
                l.floatValue() == colorHsla.l.floatValue() &&
                a.floatValue() == colorHsla.a.floatValue();
    }

    // Implementation from: https://css-tricks.com/converting-color-spaces-in-javascript/
    public Color toRgba() {
        float l = Math.round(this.l * 100f) / 100f;
        float s = Math.round(this.s * 100f) / 100f;
        float c = (1 - Math.abs(2 * l - 1)) * s;
        float x = c * (1 - Math.abs((h / 60) % 2 - 1));
        float m = l - c / 2;
        float r = 0;
        float g = 0;
        float b = 0;

        if (0 <= h && h < 60) {
            r = c;
            g = x;
            b = 0;
        } else if (60 <= h && h < 120) {
            r = x;
            g = c;
            b = 0;
        } else if (120 <= h && h < 180) {
            r = 0;
            g = c;
            b = x;
        } else if (180 <= h && h < 240) {
            r = 0;
            g = x;
            b = c;
        } else if (240 <= h && h < 300) {
            r = x;
            g = 0;
            b = c;
        } else if (300 <= h && h < 360) {
            r = c;
            g = 0;
            b = x;
        }

        r = r + m;
        g = g + m;
        b = b + m;

        return new Color(r, g, b, a);
    }

    public ColorHsla darken(int percent) {
        l = Math.max(0, l - (1f / 100f) * percent);
        return this;
    }

    public ColorHsla lighten(int percent) {
        l = Math.min(1, l + (1f / 100f) * percent);
        return this;
    }

    @Override
    public String toString() {
        return "ColorHsla{" +
                "h=" + h +
                ", s=" + s +
                ", l=" + l +
                ", a=" + a +
                '}';
    }
}
