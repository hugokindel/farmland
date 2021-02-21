package com.ustudents.engine.graphic;

public class Origin {
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

    public float customHorizontal;

    public float customVertical;

    public Origin() {
        this.vertical = Vertical.Top;
        this.horizontal = Horizontal.Left;
    }

    public Origin(Vertical vertical, Horizontal horizontal) {
        this.vertical = vertical;
        this.horizontal = horizontal;
    }
}
