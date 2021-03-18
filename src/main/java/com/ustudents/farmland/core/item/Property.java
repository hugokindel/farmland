package com.ustudents.farmland.core.item;

import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.core.json.annotation.JsonSerializableConstructor;

import java.util.Map;

@JsonSerializable
public class Property extends Item {

    @JsonSerializable
    public Integer maintenanceCost;

    @JsonSerializable
    public Integer initLevel;

    @JsonSerializable
    public Integer actualLevel;

    @JsonSerializable
    public Integer maxLevel;

    @JsonSerializableConstructor
    @Override
    public void deserialize(Map<String, Object> json) {
        if (!texture.startsWith("property/")) {
            texture = "property/" + super.texture;
        }
        this.spritesheet = Resources.loadSpritesheet(texture);
    }

    public static Property clone(Property property) {
        Property result = new Property();
        result.takeValuesFrom(property);
        result.maintenanceCost = property.maintenanceCost;
        result.initLevel = property.initLevel;
        result.actualLevel = property.actualLevel;
        result.maxLevel = property.maxLevel;
        return result;
    }
}
