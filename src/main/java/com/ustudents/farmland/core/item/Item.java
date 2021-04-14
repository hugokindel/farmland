package com.ustudents.farmland.core.item;

import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.json.Json;
import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.core.json.annotation.JsonSerializableConstructor;
import com.ustudents.engine.core.json.annotation.JsonSerializableType;
import com.ustudents.engine.graphic.Sprite;
import com.ustudents.engine.graphic.Spritesheet;

import java.util.Map;

@JsonSerializable
public class Item {
    @JsonSerializable
    public String id;

    @JsonSerializable
    public String name;

    @JsonSerializable
    public Integer initValue;

    @JsonSerializable
    public Integer value;

    @JsonSerializable
    public String texture;

    @JsonSerializable(necessary = false)
    public Integer quantity;

    public Spritesheet spritesheet;

    public Item() {
        this.quantity = 0;
    }

    @JsonSerializableConstructor
    public void deserialize(Map<String, Object> json) {
        this.spritesheet = Resources.loadSpritesheet(texture);
    }

    public void takeValuesFrom(Item from) {
        this.id = from.id;
        this.name = from.name;
        this.initValue = from.initValue;
        this.value = from.value;
        this.texture = from.texture;
        this.quantity = from.quantity;
        this.spritesheet = from.spritesheet;
    }

    public static Item clone(Item item) {
        return Json.deserialize(Json.serialize(item), item.getClass());
    }

    public void endTurn() {

    }

    public Sprite getSprite() {
        return spritesheet.getSprite(id + "1");
    }

    public String getId() {
        return id;
    }

    public Item get() {
        return this;
    }

    public boolean shouldBeDestroyed() {
        return false;
    }
}
