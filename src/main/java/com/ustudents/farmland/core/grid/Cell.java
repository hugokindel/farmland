package com.ustudents.farmland.core.grid;

import com.ustudents.engine.core.json.Json;
import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.core.json.annotation.JsonSerializableConstructor;
import com.ustudents.engine.graphic.Sprite;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.core.item.*;
import org.joml.Vector4f;

import java.util.Map;

@JsonSerializable
@SuppressWarnings("unchecked")
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

    @JsonSerializableConstructor
    public void deserialize(Map<String, Object> map) {
        if (item != null) {
            Item real = Farmland.get().getItem(item.id);

            if (real instanceof Animal) {
                item = Json.deserialize((Map<String, Object>)map.get("item"), Animal.class);
            } else if (real instanceof Crop) {
                item = Json.deserialize((Map<String, Object>)map.get("item"), Crop.class);
            } else if (real instanceof Decoration) {
                item = Json.deserialize((Map<String, Object>)map.get("item"), Decoration.class);
            } else if (real instanceof Property) {
                item = Json.deserialize((Map<String, Object>)map.get("item"), Property.class);
            }
        }
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
