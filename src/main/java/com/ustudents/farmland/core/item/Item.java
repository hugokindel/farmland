package com.ustudents.farmland.core.item;

import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.core.json.annotation.JsonSerializableConstructor;
import com.ustudents.engine.graphic.Spritesheet;

import java.util.Map;

@JsonSerializable
public class Item {
    @JsonSerializable
    public String id;

    public String name;

    public Spritesheet spritesheet;

    @JsonSerializableConstructor
    public void deserialize(Map<String, Object> json) {
        this.id = (String)json.get("id");
        this.name = (String)json.get("name");
        this.spritesheet = Resources.loadSpritesheet("crops/" + (String)json.get("texture"));
    }
}
