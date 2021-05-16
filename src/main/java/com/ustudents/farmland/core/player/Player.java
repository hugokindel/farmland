package com.ustudents.farmland.core.player;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.core.event.EventDispatcher;
import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.graphic.Color;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.component.GridComponent;
import com.ustudents.farmland.core.grid.Cell;
import com.ustudents.farmland.core.item.*;
import com.ustudents.farmland.network.actions.*;
import com.ustudents.farmland.core.system.Caravan;
import com.ustudents.farmland.core.system.Research;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.util.*;

@JsonSerializable
@SuppressWarnings("unchecked")
public class Player {
    public enum Type {
        Undefined,
        Human,
        Bot
    }

    public enum InventoryType {
        WaitingToBePlanted,
        WaitingToBeSold
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
    public Integer loan;

    @JsonSerializable
    public Integer remainingDebt;

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
    public List<Caravan> caravans;

    @JsonSerializable
    public List<Research> researches;

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
        this.loan = 0;
        this.remainingDebt = 0;
        this.caravans = new ArrayList<>();
        this.researches = new ArrayList<>();
        this.researches.add(new Research(Research.Type.Farmer));
        this.researches.add(new Research(Research.Type.Breeder));
        this.boughtCrops = new ArrayList<>();
        this.boughtAnimals = new ArrayList<>();
        this.soldCrops = new ArrayList<>();
        this.soldAnimals = new ArrayList<>();
    }

    public boolean hasLost() {
        return money <= 0;
    }

    public boolean hasWon() {
        return money >= 1000 && remainingDebt <= 0 && !isDead();
    }

    public boolean isHuman() {
        return type == Type.Human;
    }

    public boolean isBot() {
        return type == Type.Bot;
    }

    public boolean isOnlySurvivor() {
        for(Player player : Farmland.get().getLoadedSave().players) {
            if (!player.getId().equals(getId()) && !player.isDead()) {
                return false;
            }
        }

        return !isDead();
    }

    public boolean isAloneInGame() {
        return Farmland.get().getLoadedSave().players.size() == 1;
    }

    public void addToInventory(Item item, InventoryType type) {
        if (type == InventoryType.WaitingToBePlanted) {
            boolean contains = false;
            List<? extends Item> items = getGoodList(item, InventoryType.WaitingToBePlanted);
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
        } else {
            boolean contains = false;
            List<? extends Item> items = getGoodList(item, InventoryType.WaitingToBeSold);
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

    private List<? extends Item> getGoodList(Item item, InventoryType type){
        if(item instanceof Crop){
            return (type == InventoryType.WaitingToBePlanted) ? boughtCrops : soldCrops;
        }else if(item instanceof Animal){
            return (type == InventoryType.WaitingToBePlanted) ? boughtAnimals : soldAnimals;
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

    public void deleteFromSoldInventory(Item item) {
        if (item instanceof Animal){
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

    public boolean deleteFromInventory(Item item, InventoryType type) {
            List<? extends Item> items = getGoodList(item, type);
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

    public boolean deleteFromInventory(List<Item> list, InventoryType type){
        for(Item item: list){
            if(!deleteFromInventory(item, type)){
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

    public void addMoney(int money) {
        setMoney(this.money + money);
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

    public Research findResearch(Research.Type type) {
        for (Research research : researches) {
            if (research.type == type) {
                return research;
            }
        }

        return null;
    }

    public boolean isDead() {
        return Farmland.get().getLoadedSave().deadPlayers.contains(getId());
    }

    public void selectItem(String itemId) {
        if (Game.get().hasAuthority()) {
            if (itemId == null || (getItemFromInventory(itemId) != null && getItemFromInventory(itemId).quantity > 0)) {
                setSelectedItemId(itemId);
            }

            Farmland.get().serverBroadcastSave();
        } else {
            Game.get().getClient().send(new SelectItemMessage(itemId));
        }
    }

    public void buyItem(Item item, int quantity) {
        if (Game.get().hasAuthority()) {
            setMoney(money - (item.buyingValue * quantity));

            for (int i = 0; i < quantity; i++) {
                addToInventory(item, InventoryType.WaitingToBePlanted);

                Farmland.get().getLoadedSave().fillTurnItemDataBase(item, true);
            }

            Farmland.get().serverBroadcastSave();
        } else {
            Game.get().getClient().send(new BuyItemMessage(item.id));
        }
    }

    public void sellItem(String itemId, int quantity) {
        if (Game.get().hasAuthority()) {
            money += Farmland.get().getLoadedSave().getResourceDatabase().get(itemId).sellingValue;

            if (remainingDebt > 0) {
                payLoan((Math.max(loan * (Farmland.get().getLoadedSave().debtRate / 100), 1)), false);
            }

            Item item = Item.clone(getAllItemOfSellInventory().get(itemId));
            Farmland.get().getLoadedSave().fillTurnItemDataBase(item, false);
            deleteFromInventory(getAllItemOfSellInventory().get(itemId), InventoryType.WaitingToBeSold);

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

            Farmland.get().getLoadedSave().getLocalPlayer().deleteFromInventory(selectedItem, InventoryType.WaitingToBePlanted);

            if (getItemFromInventory(selectedItem.id) == null) {
                Farmland.get().getLoadedSave().getLocalPlayer().selectedItemId = null;
            }

            Farmland.get().getLoadedSave().itemUsed.dispatch();

            Farmland.get().serverBroadcastSave();
        } else {
            Game.get().getClient().send(new PlaceSelectedItemMessage(position));
        }
    }

    public void harvestSelectedItem(Vector2i position) {
        if (Game.get().hasAuthority()) {
            Cell cell = Farmland.get().getLoadedSave().getCell(position);
            addToInventory(cell.item, Player.InventoryType.WaitingToBeSold);
            cell.item = null;

            Farmland.get().serverBroadcastSave();
        } else {
            Game.get().getClient().send(new HarvestItemMessage(position));
        }
    }

    public void takeLoan(int amount) {
        if (Game.get().hasAuthority()) {
            money += amount;
            loan = amount + (int)(amount * 0.03f) + 1;
            remainingDebt += amount + (int)(amount * 0.03f) + 1;

            Farmland.get().serverBroadcastSave();
        } else {
            Game.get().getClient().send(new TakeLoanMessage(amount));
        }
    }

    public void payLoan(int amount) {
        payLoan(amount, true);
    }

    public void payLoan(int amount, boolean broadcast) {
        if (Game.get().hasAuthority()) {
            if (remainingDebt > 0) {
                money -= amount;
                remainingDebt -= amount;

                if (remainingDebt <= 0) {
                    remainingDebt = 0;
                    loan = 0;
                }
            }

            if (broadcast) {
                Farmland.get().serverBroadcastSave();
            }
        } else {
            Game.get().getClient().send(new PayLoanMessage(amount));
        }
    }

    public void upgradeResearch(Research.Type type) {
        if (Game.get().hasAuthority()) {
            if (Game.isDebugging()) {
                Out.println("Upgrade: " + name);
            }

            Research research = findResearch(type);

            money -= research.getPrice();
            research.levelUp(10, 1);

            Farmland.get().serverBroadcastSave();
        } else {
            Game.get().getClient().send(new UpgradeResearch(type));
        }
    }

    public void sendCaravan(int travelPrice, int travelTime, int sellValue, String itemId) {
        if (Game.get().hasAuthority()) {
            int itemQuantity = getAllItemOfSellInventory().get(itemId).quantity;

            money -= travelPrice;
            caravans.add(new Caravan(sellValue, travelTime, itemId));

            for (int i = 0; i < itemQuantity / 2; i++) {
                deleteFromInventory(getAllItemOfSellInventory().get(itemId), InventoryType.WaitingToBeSold);
            }

            Farmland.get().serverBroadcastSave();
        } else {
            Game.get().getClient().send(new SendCaravanMessage(travelPrice, travelTime, sellValue, itemId));
        }
    }
}
