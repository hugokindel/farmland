package com.ustudents.farmland.core.player;

import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.graphic.Color;
import com.ustudents.farmland.core.item.Item;
import org.joml.Vector2f;

import java.util.ArrayList;
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

    private int currentActionPlayed;

    public List<Item> listOfItems;

    public Player() {
        listOfItems = new ArrayList<>();
    }

    public Player(String name, String villageName, Color color) {
        this.name = name;
        this.village = new Village(villageName);
        this.color = color;
        this.money = 500;
        listOfItems = new ArrayList<>();
    }
}
