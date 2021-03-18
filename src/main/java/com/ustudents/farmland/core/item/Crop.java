package com.ustudents.farmland.core.item;

import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.core.json.annotation.JsonSerializableConstructor;

import java.util.Map;

@JsonSerializable
public class Crop extends Item {
    @JsonSerializable
    public Integer numberOfTurnsToGrow;

    @JsonSerializableConstructor
    @Override
    public void deserialize(Map<String, Object> json) {
        if (!texture.startsWith("crops/")) {
            texture = "crops/" + texture;
        }
        this.spritesheet = Resources.loadSpritesheet(texture);
    }

    public static Crop clone(Crop crop) {
        Crop result = new Crop();
        result.takeValuesFrom(crop);
        result.numberOfTurnsToGrow = crop.numberOfTurnsToGrow;
        return result;
    }
}
