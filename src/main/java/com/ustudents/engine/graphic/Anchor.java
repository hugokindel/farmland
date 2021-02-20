package com.ustudents.engine.graphic;

public class Anchor {
    public enum Vertical {
        Custom,
        Top,
        Middle,
        Bottom
    }

    public enum Horizontal {
        Custom,
        Left,
        Center,
        Right
    }

    public Vertical vertical;

    public Horizontal horizontal;

    public Anchor() {
        this.vertical = Vertical.Top;
        this.horizontal = Horizontal.Left;
    }

    public Anchor(Vertical vertical, Horizontal horizontal) {
        this.vertical = vertical;
        this.horizontal = horizontal;
    }
}
