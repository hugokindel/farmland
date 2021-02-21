package com.ustudents.farmland.core;

import com.ustudents.engine.graphic.Sprite;
import org.joml.Vector4f;

public class Cell {
    public Sprite sprite;

    public Vector4f viewRectangle;

    public boolean isOwned;

    public Cell(Sprite sprite, Vector4f viewRectangle) {
        this.sprite = sprite;
        this.viewRectangle = viewRectangle;
    }

    public void setOwned(boolean owned) {
        isOwned = owned;
    }
}
