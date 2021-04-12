package com.ustudents.farmland.core.player;

import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.core.event.EventDispatcher;
import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.utility.Pair;
import com.ustudents.engine.utility.Triplet;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.component.GridComponent;
import com.ustudents.farmland.core.grid.Cell;
import com.ustudents.farmland.core.item.*;
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
    public String selectedItemID;

    @JsonSerializable
    public List<Crop> boughtCrops;

    @JsonSerializable
    public List<Animal> boughtAnimals;

    @JsonSerializable
    public List<Crop> soldCrops;

    @JsonSerializable
    public List<Animal> soldAnimals;

    public String ipAddress;


    public List<Pair<Integer,Integer>> caravans;


    public Triplet<Integer,Integer,Integer> farmerResearch;


    public Triplet<Integer,Integer,Integer> breederResearch;

    public EventDispatcher moneyChanged = new EventDispatcher();

    public Player() {
    }

    public Player(String name, String villageName, Color color, String typeOfPlayer) {
        this.name = name;
        this.village = new Village(villageName);
        this.color = color;
        this.money = 500;
        this.typeOfPlayer = typeOfPlayer;
        this.boughtCrops = new ArrayList<>();
        this.boughtAnimals = new ArrayList<>();
        this.soldCrops = new ArrayList<>();
        this.soldAnimals = new ArrayList<>();
        this.caravans = new ArrayList<>();
        this.farmerResearch = new Triplet<Integer, Integer, Integer>(10,1,0);
        this.breederResearch = new Triplet<Integer, Integer, Integer>(10,1,0);
    }

    public void addToInventory(Item item, String name){
        if(name.equals("Buy")){
            boolean contains = false;
            List<? extends Item> items = getGoodList(item, true);
            assert items != null;
            for(Item i: items){
                if(item.id.equals(i.id)){
                    contains = true;
                    i.quantity += 1;
                }
            }
            if(!contains){
                if(item instanceof Crop){
                    Crop clone = (Crop) Crop.clone(item);
                    assert clone != null;
                    clone.quantity = 1;
                    boughtCrops.add(clone);
                }else if(item instanceof Animal){
                    Animal clone = (Animal) Animal.clone(item);
                    assert clone != null;
                    clone.quantity = 1;
                    boughtAnimals.add(clone);
                }
            }
        }else{
            boolean contains = false;
            List<? extends Item> items = getGoodList(item, false);
            assert items != null;
            for(Item i: items){
                if(item.id.equals(i.id)){
                    contains = true;
                    i.quantity += 1;
                }
            }
            if(!contains){
                if(item instanceof Crop){
                    Crop clone = (Crop) Crop.clone(item);
                    assert clone != null;
                    clone.quantity = 1;
                    soldCrops.add(clone);
                }else if(item instanceof Animal){
                    Animal clone = (Animal) Animal.clone(item);
                    assert clone != null;
                    clone.quantity = 1;
                    soldAnimals.add(clone);
                }
            }
        }
    }

    private List<? extends Item> getGoodList(Item item, boolean bought){
        if(item instanceof Crop){
            return (bought)? boughtCrops: soldCrops;
        }else if(item instanceof Animal){
            return (bought)? boughtAnimals: soldAnimals;
        }
        return null;
    }

    public Map<String, Item> getAllItemOfBoughtInventory(){
        Map<String, Item> playerInventory = new HashMap<>();
        for(Item item: boughtCrops){
            playerInventory.put(item.id,item);
        }

        for(Item item: boughtAnimals){
            playerInventory.put(item.id,item);
        }
        return playerInventory;
    }

    public Map<String, Item> getAllItemOfSellInventory(){
        Map<String, Item> playerInventory = new HashMap<>();
        for(Item item: soldCrops){
            playerInventory.put(item.id,item);
        }

        for(Item item: soldAnimals){
            playerInventory.put(item.id,item);
        }
        return playerInventory;
    }

    public boolean deleteFromInventory(Item item, String name) {
            List<? extends Item> items = getGoodList(item, (name.contains("Buy")));
            Item todelete = null;
            assert items != null;
            for(Item i: items){
                if(item.id.equals(i.id)){
                    i.quantity -= 1;
                    if (i.quantity <= 0){
                        todelete = i;
                    }
                }
            }

            if(todelete != null){
                items.remove(todelete);
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
        return getAllItemOfBoughtInventory().getOrDefault(id, null);
    }

    public Item getCurrentItemFromInventory() {
        if (selectedItemID != null) {
            return getAllItemOfBoughtInventory().get(selectedItemID);
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
        Vector2i gridSize = Farmland.get().getSceneManager().getCurrentScene().getEntityByName("map").getComponent(GridComponent.class).gridSize;
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
