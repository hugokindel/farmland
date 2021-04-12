package com.ustudents.farmland.core.player;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.event.EventDispatcher;
import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.utility.Pair;
import com.ustudents.engine.utility.Triplet;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.component.GridComponent;
import com.ustudents.farmland.core.grid.Cell;
import com.ustudents.farmland.core.item.*;
import com.ustudents.farmland.network.actions.BuyMessage;
import org.joml.Vector2f;
import org.joml.Vector2i;

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
    public String selectedItemId;

    @JsonSerializable
    public Map<String, Item> buyInventory;

    @JsonSerializable
    public Map<String, Item> sellInventory;

    public String ipAddress;

    public List<Pair<Integer,Integer>> caravans;

    public Triplet<Integer,Integer,Integer> farmerResearch;

    public Triplet<Integer,Integer,Integer> breederResearch;

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
        this.caravans = new ArrayList<>();
        this.farmerResearch = new Triplet<Integer, Integer, Integer>(10,1,0);
        this.breederResearch = new Triplet<Integer, Integer, Integer>(10,1,0);
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
        if (selectedItemId != null) {
            return buyInventory.get(selectedItemId);
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

        for (int x = 0; x < Farmland.get().getLoadedSave().cells.size(); x++) {
            for (int y = 0; y < Farmland.get().getLoadedSave().cells.get(x).size(); y++) {
                Cell cell = Farmland.get().getLoadedSave().cells.get(x).get(y);

                if (cell.ownerId.equals(getId())) {
                    cells.add(cell);
                }
            }
        }

        return cells;
    }

    public List<Cell> getOwnedCellsWithNoItem() {
        List<Cell> cells = new ArrayList<>();

        for (int x = 0; x < Farmland.get().getLoadedSave().cells.size(); x++) {
            for (int y = 0; y < Farmland.get().getLoadedSave().cells.get(x).size(); y++) {
                Cell cell = Farmland.get().getLoadedSave().cells.get(x).get(y);

                if (cell.ownerId.equals(getId()) && !cell.hasItem()) {
                    cells.add(cell);
                }
            }
        }

        return cells;
    }

    public List<Cell> getCloseCellsAvailable() {
        List<Cell> cells = new ArrayList<>();

        for (int x = 0; x < Farmland.get().getLoadedSave().cells.size(); x++) {
            for (int y = 0; y < Farmland.get().getLoadedSave().cells.get(x).size(); y++) {
                Cell cell = Farmland.get().getLoadedSave().cells.get(x).get(y);

                if (Farmland.get().getLoadedSave().cells.get(x).get(y).ownerId.equals(-1) && cellIsClosedToOwnedCell(x, y)) {
                    cells.add(cell);
                }
            }
        }

        return cells;
    }

    public boolean cellIsClosedToOwnedCell(int x, int y) {
        Vector2i gridSize = Farmland.get().getSceneManager().getCurrentScene().getEntityByName("grid").getComponent(GridComponent.class).gridSize;
        return ((x < gridSize.x - 1 && Farmland.get().getLoadedSave().cells.get(x + 1).get(y).ownerId.equals(getId()))) ||
                (x > 0 && Farmland.get().getLoadedSave().cells.get(x - 1).get(y).ownerId.equals(getId())) ||
                (y < gridSize.y - 1 && Farmland.get().getLoadedSave().cells.get(x).get(y + 1).ownerId.equals(getId())) ||
                (y > 0 && Farmland.get().getLoadedSave().cells.get(x).get(y - 1).ownerId.equals(getId()));
    }

    public Integer getId() {
        if (Farmland.get().getLoadedSave() != null) {
            for (int i = 0; i < Farmland.get().getLoadedSave().players.size(); i++) {
                if (Farmland.get().getLoadedSave().players.get(i).name.equals(name)) {
                    return i;
                }
            }
        }

        return -1;
    }

    public void buy(Item item, int quantity) {
        if (Game.get().hasAuthority()) {
            setMoney(money - (item.value * quantity));

            for (int i = 0; i < quantity; i++) {
                addToInventory(item, "Buy");
                Farmland.get().getLoadedSave().itemsTurn.add(item);
            }

            Farmland.get().serverBroadcastSave();
        } else {
            Game.get().getClient().send(new BuyMessage(item.id));
        }
    }

    public void placeSelectedItemInCell(int x, int y) {
        if (Game.get().hasAuthority()) {
            Item selectedItem = buyInventory.get(selectedItemId);
            Item selectedItemClone = Item.clone(selectedItem);
            assert selectedItemClone != null;
            selectedItemClone.quantity = 1;

            Farmland.get().getLoadedSave().getCell(x, y).setItem(selectedItemClone);

            if (Farmland.get().getLoadedSave().getLocalPlayer().deleteFromInventory(selectedItem, "Buy")) {
                Farmland.get().getLoadedSave().getLocalPlayer().selectedItemId = null;
            }

            Farmland.get().getLoadedSave().itemUsed.dispatch();

            Farmland.get().serverBroadcastSave();
        } else {
            // TODO
        }
    }
}
