package com.ustudents.farmland.core.item;

import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.core.json.annotation.JsonSerializableConstructor;
import com.ustudents.engine.graphic.Sprite;

import java.util.Map;

@JsonSerializable
public class Animal extends Item {

    @JsonSerializable
    public Integer numberOfTurnsToReachMaturity;

    @JsonSerializable(necessary = false)
    public Integer currentTurn = 1;

    @JsonSerializable(necessary = false)
    public String babyId;

    @JsonSerializableConstructor
    @Override
    public void deserialize(Map<String, Object> json) {
        if (!texture.startsWith("animals/")) {
            texture = "animals/" + texture;
        }
        this.spritesheet = Resources.loadSpritesheet(texture);
        if (currentTurn == 0) {
            currentTurn = 1;
        }
    }

    public static Animal clone(Animal animal) {
        Animal result = new Animal();
        result.takeValuesFrom(animal);
        result.numberOfTurnsToReachMaturity = animal.numberOfTurnsToReachMaturity;
        result.babyId = animal.babyId;
        return result;
    }

    @Override
    public void endTurn() {
        currentTurn++;
    }

    @Override
    public Sprite getSprite() {
        return spritesheet.getSprite(id + (currentTurn > 4 ? (currentTurn % 5) + 1 : currentTurn));
    }

    @Override
    public boolean shouldBeDestroyed() {
        return currentTurn >= numberOfTurnsToReachMaturity;
    }

    public String getLocalizedBabyName() {
        if (babyId == null || babyId.isEmpty()) {
            return id;
        }

        return Resources.getLocalizedText(babyId);
    }
}
