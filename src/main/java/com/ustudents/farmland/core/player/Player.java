package com.ustudents.farmland.core.player;

import com.ustudents.engine.core.event.EventDispatcher;
import com.ustudents.engine.core.json.Json;
import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.core.json.annotation.JsonSerializableConstructor;
import com.ustudents.engine.graphic.Color;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.component.GridComponent;
import com.ustudents.farmland.core.grid.Cell;
import com.ustudents.farmland.core.item.*;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.beans.EventHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonSerializable
@SuppressWarnings("unchecked")
public class Player {
    @JsonSerializable
    public String name;

    @JsonSerializable
    public Village village;

    @JsonSerializable
    public Integer money;

    @JsonSerializable
    public String typeOfPlayer;

    @JsonSerializable
    public Color color;

    @JsonSerializable
    public Vector2f position;

    @JsonSerializable
    public String selectedItemID;

    @JsonSerializable
    public Map<String, Item> buyInventory;

    @JsonSerializable
    public Map<String, Item> sellInventory;

    public String ipAddress;

    public EventDispatcher moneyChanged = new EventDispatcher();

    public Player() {
        this.buyInventory = new HashMap<>();
        this.sellInventory = new HashMap<>();
    }

    public Player(String name, String villageName, Color color, String typeOfPlayer) {
        this.name = name;
        this.village = new Village(villageName);
        this.color = color;
        this.money = 500;
        this.typeOfPlayer = typeOfPlayer;
        this.buyInventory = new HashMap<>();
        this.sellInventory = new HashMap<>();
    }

    @JsonSerializableConstructor
    public void deserialize() {
        Map<String, Item> realInventory = new HashMap<>();
        Map<String, Item> realSellInventory = new HashMap<>();
        for (Map.Entry<String, Item> elements : buyInventory.entrySet()) {
            Map<String, Object> value = ((Map<String, Object>)((Object)elements.getValue()));
            Item item = Farmland.get().getItem((String)value.get("id"));
            if (item instanceof Animal) {
                realInventory.put(elements.getKey(), Json.deserialize(value, Animal.class));
            } else if (item instanceof Crop) {
                realInventory.put(elements.getKey(), Json.deserialize(value, Crop.class));
            } else if (item instanceof Decoration) {
                realInventory.put(elements.getKey(), Json.deserialize(value, Decoration.class));
            } else if (item instanceof Property) {
                realInventory.put(elements.getKey(), Json.deserialize(value, Property.class));
            }
        }
        buyInventory = realInventory;
        sellInventory = realSellInventory;
    }

    public void addToInventory(Item item, String name) {
        if(name.equals("Buy")){
            if (!buyInventory.containsKey(item.id)) {
                buyInventory.put(item.id, Item.clone(item));
            }

            buyInventory.get(item.id).quantity++;
        }else{
            if (!sellInventory.containsKey(item.id)) {
                sellInventory.put(item.id, Item.clone(item));
            }

            sellInventory.get(item.id).quantity++;
        }

    }

    public boolean deleteFromInventory(Item item, String name) {
        if(name.equals("Buy")){
            if (buyInventory.containsKey(item.id)) {
                if (buyInventory.get(item.id).quantity >= 2) {
                    buyInventory.get(item.id).quantity--;
                    return false;
                } else {
                    buyInventory.remove(item.id);
                    return true;
                }
            }

        }else{
            if (sellInventory.containsKey(item.id)) {
                if (sellInventory.get(item.id).quantity >= 2) {
                    sellInventory.get(item.id).quantity--;
                    return false;
                } else {
                    sellInventory.remove(item.id);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean deleteFromInventory(List<Item> list, String name){
        for(Item item: list){
            if(!deleteFromInventory(item, name)){
                return false;
            }
        }
        return true;
    }

    public Item getItemFromInventory(String id) {
        return buyInventory.getOrDefault(id, null);
    }

    public Item getCurrentItemFromInventory() {
        if (selectedItemID != null) {
            return buyInventory.get(selectedItemID);
        } else {
            return null;
        }
    }

    public void setMoney(Integer money) {
        this.money = money;
        moneyChanged.dispatch();
    }

    public List<Cell> getOwnedCells() {
        List<Cell> cells = new ArrayList<>();

        for (int x = 0; x < Farmland.get().getCurrentSave().cells.size(); x++) {
            for (int y = 0; y < Farmland.get().getCurrentSave().cells.get(x).size(); y++) {
                Cell cell = Farmland.get().getCurrentSave().cells.get(x).get(y);

                if (cell.ownerId.equals(getId())) {
                    cells.add(cell);
                }
            }
        }

        return cells;
    }

    public List<Cell> getOwnedCellsWithNoItem() {
        List<Cell> cells = new ArrayList<>();

        for (int x = 0; x < Farmland.get().getCurrentSave().cells.size(); x++) {
            for (int y = 0; y < Farmland.get().getCurrentSave().cells.get(x).size(); y++) {
                Cell cell = Farmland.get().getCurrentSave().cells.get(x).get(y);

                if (cell.ownerId.equals(getId()) && !cell.hasItem()) {
                    cells.add(cell);
                }
            }
        }

        return cells;
    }

    public List<Cell> getCloseCellsAvailable() {
        List<Cell> cells = new ArrayList<>();

        for (int x = 0; x < Farmland.get().getCurrentSave().cells.size(); x++) {
            for (int y = 0; y < Farmland.get().getCurrentSave().cells.get(x).size(); y++) {
                Cell cell = Farmland.get().getCurrentSave().cells.get(x).get(y);

                if (Farmland.get().getCurrentSave().cells.get(x).get(y).ownerId.equals(-1) && cellIsClosedToOwnedCell(x, y)) {
                    cells.add(cell);
                }
            }
        }

        return cells;
    }

    public boolean cellIsClosedToOwnedCell(int x, int y) {
        Vector2i gridSize = Farmland.get().getSceneManager().getCurrentScene().getEntityByName("grid").getComponent(GridComponent.class).gridSize;
        return ((x < gridSize.x - 1 && Farmland.get().getCurrentSave().cells.get(x + 1).get(y).ownerId.equals(getId()))) ||
                (x > 0 && Farmland.get().getCurrentSave().cells.get(x - 1).get(y).ownerId.equals(getId())) ||
                (y < gridSize.y - 1 && Farmland.get().getCurrentSave().cells.get(x).get(y + 1).ownerId.equals(getId())) ||
                (y > 0 && Farmland.get().getCurrentSave().cells.get(x).get(y - 1).ownerId.equals(getId()));
    }

    public Integer getId() {
        if (Farmland.get().getCurrentSave() != null) {
            for (int i = 0; i < Farmland.get().getCurrentSave().players.size(); i++) {
                if (Farmland.get().getCurrentSave().players.get(i).name.equals(name)) {
                    return i;
                }
            }
        }

        return -1;
    }
}
