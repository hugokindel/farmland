package com.ustudents.farmland.core.item;

import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.json.annotation.JsonSerializable;

import java.util.Map;

@JsonSerializable
public class Animal extends Item {

    @JsonSerializable
    public Integer numberOfTurnsToReachMaturity;

    @Override
    public void deserialize(Map<String, Object> json) {
        this.spritesheet = Resources.loadSpritesheet("animals/" + super.texture);
    }
}
