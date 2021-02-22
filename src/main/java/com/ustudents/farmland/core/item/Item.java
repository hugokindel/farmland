package com.ustudents.farmland.core.item;

import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.core.json.annotation.JsonSerializableConstructor;
import com.ustudents.engine.graphic.Spritesheet;
import com.ustudents.engine.graphic.Texture;

import java.util.Map;

@JsonSerializable
public class Item {
    public String id;

    public String name;

    public Spritesheet texture;

    @JsonSerializableConstructor
    public void deserialize(Map<String, Object> json) {
        this.id = (String)json.get("id");
        this.name = (String)json.get("name");
        this.texture = Resources.loadSpritesheet("crops/" + (String)json.get("texture"));
    }

    @Override
    public String toString() {
        return "Item{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", texture=" + texture +
                '}';
    }
}
