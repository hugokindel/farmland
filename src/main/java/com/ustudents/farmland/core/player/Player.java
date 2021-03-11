package com.ustudents.farmland.core.player;

import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.core.json.annotation.JsonSerializableConstructor;
import com.ustudents.engine.graphic.Color;
import com.ustudents.farmland.core.item.Item;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonSerializable
public class Player {
    @JsonSerializable
    public String name;

    @JsonSerializable
    public Village village;

    @JsonSerializable
    public Integer money;

    @JsonSerializable
    public Color color;

    @JsonSerializable
    public Vector2f position;

    @JsonSerializable
    public String selectedItemID;

    @JsonSerializable
    public Map<String, Integer> inventory;

    public Player() {
        this.inventory = new HashMap<>();
    }

    public Player(String name, String villageName, Color color) {
        this.name = name;
        this.village = new Village(villageName);
        this.color = color;
        this.money = 500;
        this.inventory = new HashMap<>();
    }

    @JsonSerializableConstructor
    public void deserialize() {
        Map<String, Integer> realInventory = new HashMap<>();
        for (Map.Entry<String, Integer> elements : inventory.entrySet()) {
            Long value = ((Long)((Object)elements.getValue()));
            realInventory.put(elements.getKey(), value.intValue());
        }
        inventory = realInventory;
    }

    public void addToInventory(Item item) {
        if (!inventory.containsKey(item.id)) {
            inventory.put(item.id, 1);
        } else {
            String test = String.valueOf(inventory.get(item.id));
            inventory.put(item.id, Integer.parseInt(test) + 1);
        }
    }

    public boolean deleteFromInventory(Item item) {
        if (inventory.containsKey(item.id)) {
            if (inventory.get(item.id) >= 2) {
                inventory.put(item.id, inventory.get(item.id) - 1);
                return false;
            } else {
                inventory.remove(item.id);
                return true;
            }
        }
        return false;
    }

}
