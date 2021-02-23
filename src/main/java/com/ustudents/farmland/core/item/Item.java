package com.ustudents.farmland.core.item;

import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.core.json.annotation.JsonSerializableConstructor;
import com.ustudents.engine.core.json.annotation.JsonSerializableType;
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
    private String texture;

    public Spritesheet spritesheet;

    @JsonSerializableConstructor
    public void deserialize(Map<String, Object> json) {
        this.spritesheet = Resources.loadSpritesheet("crops/" + texture);
    }
}
