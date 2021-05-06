package com.ustudents.farmland.core.player;

import com.ustudents.engine.Game;
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
import com.ustudents.farmland.network.actions.*;
import com.ustudents.farmland.core.system.Caravan;
import com.ustudents.farmland.core.system.Research;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonSerializable
@SuppressWarnings("unchecked")
public class Player {
    public enum Type {
        Undefined,
        Human,
        Bot
    }

    public static final Color DEFAULT_BANNER_COLOR = new Color(1f, 0f, 0f, 1f);

    @JsonSerializable
    public String name;

    @JsonSerializable
    public Village village;

    @JsonSerializable
    public Integer money;

    @JsonSerializable
    public Type type;

    @JsonSerializable
    public Integer loanMoney;

    @JsonSerializable
    public Integer debtMoney;

    @JsonSerializable
    public Color bannerColor;

    @JsonSerializable
    public Avatar avatar;

    @JsonSerializable
    public Vector2f position;

    @JsonSerializable
    public String selectedItemId;

    @JsonSerializable
    public List<Crop> boughtCrops;

    @JsonSerializable
    public List<Animal> boughtAnimals;

    @JsonSerializable
    public List<Crop> soldCrops;

    @JsonSerializable
    public List<Animal> soldAnimals;

    @JsonSerializable
    public List<Caravan> caravanList;

    @JsonSerializable
    public List<Research> researchList;

    public EventDispatcher moneyChanged = new EventDispatcher();

    public static int DEFAULT_CELL_PRICE = 25;

    public Player() {
    }

    public Player(String name, String villageName, Color bannerColor, Color bracesColor, Color shirtColor, Color hatColor, Color buttonColor, Type type) {
        this.name = name;
        this.village = new Village(villageName);
        this.bannerColor = bannerColor;
        this.avatar = new Avatar(bracesColor, shirtColor, hatColor, buttonColor);
        this.money = 500;
        this.type = type;
        this.loanMoney = 0;
        this.debtMoney = 0;
        this.caravanList = new ArrayList<>();
        this.researchList = new ArrayList<>();
        this.researchList.add(new Research("Fermier"));
        this.researchList.add(new Research("Eleveur"));
        this.boughtCrops = new ArrayList<>();
        this.boughtAnimals = new ArrayList<>();
        this.soldCrops = new ArrayList<>();
        this.soldAnimals = new ArrayList<>();
    }

    public void addToInventory(Item item, String name) {
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

    public void clearSoldLists(){
        soldCrops.clear();
        soldAnimals.clear();
    }

    public void deleteFromSoldInventory(Item item, boolean isAnimal){
        if (isAnimal){
            Animal todelete = null;
            for(Item i: soldAnimals){
                if(item.id.equals(i.id)){
                    i.quantity -= 1;
                    if (i.quantity <= 0){
                        todelete = (Animal)i;
                    }
                }
            }
            if(todelete != null){
                soldAnimals.remove(todelete);
            }
        } else {
            Crop todelete = null;
            for(Item i: soldCrops){
                if(item.id.equals(i.id)){
                    i.quantity -= 1;
                    if (i.quantity <= 0){
                        todelete = (Crop)i;
                    }
                }
            }
            if(todelete != null){
                soldCrops.remove(todelete);
            }
        }
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
        if (selectedItemId != null) {
            return getAllItemOfBoughtInventory().get(selectedItemId);
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

    public String getSelectedItemId() {
        return selectedItemId;
    }

    public void setSelectedItemId(String selectedItemId) {
        this.selectedItemId = selectedItemId;
    }

    public void selectItem(String itemId) {
        if (Game.get().hasAuthority()) {
            setSelectedItemId(itemId);

            Farmland.get().serverBroadcastSave();
        } else {
            Game.get().getClient().send(new SelectItemMessage(itemId));
        }
    }

    public void buyItem(Item item, int quantity) {
        if (Game.get().hasAuthority()) {
            setMoney(money - (item.buyingValue * quantity));

            for (int i = 0; i < quantity; i++) {
                addToInventory(item, "Buy");

                Farmland.get().getLoadedSave().buyItemDatabasePerTurn.get(Farmland.get().getLoadedSave().turn).add(item);
            }

            Farmland.get().serverBroadcastSave();
        } else {
            Game.get().getClient().send(new BuyItemMessage(item.id));
        }
    }

    public void sellItem(String itemId, int quantity) {
        if (Game.get().hasAuthority()) {
            for (int i = 0; i < quantity; i++) {
                setMoney(money + (int)(getItemFromInventory(itemId).sellingValue/1.5));
                deleteFromInventory(getItemFromInventory(itemId), "Sell");
            }

            Farmland.get().serverBroadcastSave();
        } else {
            Game.get().getClient().send(new SellItemMessage(itemId));
        }
    }

    public void buyCell(Vector2i position) {
        if (Game.get().hasAuthority()) {
            setMoney(money - DEFAULT_CELL_PRICE);

            Farmland.get().getLoadedSave().getCell(position).setOwned(true, getId());

            Farmland.get().serverBroadcastSave();
        } else {
            Game.get().getClient().send(new BuyCellMessage(position));
        }
    }

    public void placeSelectedItem(Vector2i position) {
        if (Game.get().hasAuthority()) {
            Item selectedItem = getItemFromInventory(selectedItemId);
            Item selectedItemClone = Item.clone(selectedItem);
            assert selectedItemClone != null;
            selectedItemClone.quantity = 1;

            Farmland.get().getLoadedSave().getCell(position).setItem(selectedItemClone);

            Farmland.get().getLoadedSave().getLocalPlayer().deleteFromInventory(selectedItem, "Buy");

            if (getItemFromInventory(selectedItem.id) == null) {
                Farmland.get().getLoadedSave().getLocalPlayer().selectedItemId = null;
            }

            Farmland.get().getLoadedSave().itemUsed.dispatch();

            Farmland.get().serverBroadcastSave();
        } else {
            Game.get().getClient().send(new PlaceSelectedItemMessage(position));
        }
    }
}
