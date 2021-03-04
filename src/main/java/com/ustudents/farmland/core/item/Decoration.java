package com.ustudents.farmland.core.item;

import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.core.json.annotation.JsonSerializableConstructor;

import java.util.Map;

@JsonSerializable
public class Decoration extends Item {

    @JsonSerializableConstructor
    @Override
    public void deserialize(Map<String, Object> json) {
        this.spritesheet = Resources.loadSpritesheet("decoration/" + super.texture);
    }
}
