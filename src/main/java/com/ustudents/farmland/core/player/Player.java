package com.ustudents.farmland.core.player;

import com.ustudents.engine.core.json.annotation.JsonSerializable;
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

    public void addToInventory(Item item) {
        if (!inventory.containsKey(item.id)) {
            inventory.put(item.id, 1);
        } else {
            inventory.put(item.id, inventory.get(item.id) + 1);
        }
    }
}
