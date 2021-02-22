package com.ustudents.farmland.core.item;

import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.core.json.annotation.JsonSerializableConstructor;

import java.util.Map;

@JsonSerializable
public class Crop extends Item {
    public Integer numberOfTurnsToGrow;

    @JsonSerializableConstructor
    @Override
    public void deserialize(Map<String, Object> json) {
        super.deserialize(json);
        numberOfTurnsToGrow = (Integer)json.get("numberOfTurnsToGrow");
    }

    @Override
    public String toString() {
        return "Crop{" +
                "numberOfTurnsToGrow=" + numberOfTurnsToGrow +
                '}';
    }
}
