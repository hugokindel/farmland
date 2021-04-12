package com.ustudents.farmland.core.item;

import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.core.json.annotation.JsonSerializableConstructor;
import com.ustudents.engine.graphic.Sprite;

import java.util.Map;

@JsonSerializable
public class Crop extends Item {

    @JsonSerializable
    public Integer numberOfTurnsToGrow;

    @JsonSerializable(necessary = false)
    public Integer currentTurn = 1;

    @JsonSerializableConstructor
    @Override
    public void deserialize(Map<String, Object> json) {
        if (!texture.startsWith("crops/")) {
            texture = "crops/" + texture;
        }
        this.spritesheet = Resources.loadSpritesheet(texture);
        if (currentTurn == 0) {
            currentTurn = 1;
        }
    }

    public static Crop clone(Crop crop) {
        Crop result = new Crop();
        result.takeValuesFrom(crop);
        result.numberOfTurnsToGrow = crop.numberOfTurnsToGrow;
        return result;
    }

    @Override
    public void endTurn() {
        currentTurn++;
    }

    @Override
    public Sprite getSprite() {
        return spritesheet.getSprite(id + currentTurn);
    }

    @Override
    public boolean shouldBeDestroyed() {
        return currentTurn > numberOfTurnsToGrow;
    }
}
