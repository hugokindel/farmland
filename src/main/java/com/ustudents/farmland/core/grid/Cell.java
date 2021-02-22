package com.ustudents.farmland.core.grid;

import com.ustudents.engine.graphic.Sprite;
import com.ustudents.farmland.core.item.Item;
import org.joml.Vector4f;

public class Cell {
    public Sprite sprite;

    public Vector4f viewRectangle;

    public boolean isOwned;

    public Item item;

    public Cell(Sprite sprite, Vector4f viewRectangle) {
        this.sprite = sprite;
        this.viewRectangle = viewRectangle;
    }

    public void setOwned(boolean owned) {
        isOwned = owned;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
