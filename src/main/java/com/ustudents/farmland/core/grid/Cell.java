package com.ustudents.farmland.core.grid;

import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.graphic.Sprite;
import com.ustudents.farmland.core.item.Item;
import org.joml.Vector4f;

@JsonSerializable
public class Cell {
    @JsonSerializable
    public Sprite sprite;

    @JsonSerializable
    public Integer ownerId;

    @JsonSerializable
    public Item item;

    // TODO: Change
    @JsonSerializable(necessary = false)
    public Vector4f viewRectangle;

    public Cell() {
        this.sprite = null;
        this.viewRectangle = null;
        this.ownerId = -1;
        this.item = null;
    }

    public Cell(Sprite sprite, Vector4f viewRectangle) {
        this.sprite = sprite;
        this.viewRectangle = viewRectangle;
        this.ownerId = -1;
        this.item = null;
    }

    public void setOwned(boolean owned, int ownerId) {
        this.ownerId = ownerId;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public boolean isOwned() {
        return ownerId != -1;
    }

    public boolean isOwnedByCurrentPlayer() {
        return ownerId == 0;
    }

    public boolean hasItem() {
        return item != null;
    }
}
