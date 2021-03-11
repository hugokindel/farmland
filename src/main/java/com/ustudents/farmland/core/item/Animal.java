package com.ustudents.farmland.core.item;

import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.core.json.annotation.JsonSerializableConstructor;

import java.util.Map;

@JsonSerializable
public class Animal extends Item {

    @JsonSerializable
    public Integer numberOfTurnsToReachMaturity;

    @JsonSerializableConstructor
    @Override
    public void deserialize(Map<String, Object> json) {
        if (!texture.startsWith("animals/")) {
            texture = "animals/" + texture;
        }
        this.spritesheet = Resources.loadSpritesheet(texture);
    }
}
