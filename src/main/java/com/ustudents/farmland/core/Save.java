package com.ustudents.farmland.core;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.core.event.Event;
import com.ustudents.engine.core.event.EventDispatcher;
import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.core.json.annotation.JsonSerializableConstructor;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.graphic.Sprite;
import com.ustudents.engine.graphic.Texture;
import com.ustudents.engine.network.NetMode;
import com.ustudents.engine.utility.SeedRandom;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.core.grid.Cell;
import com.ustudents.farmland.core.item.Animal;
import com.ustudents.farmland.core.item.Crop;
import com.ustudents.farmland.core.item.Item;
import com.ustudents.farmland.core.player.Bot;
import com.ustudents.farmland.core.player.Player;
import com.ustudents.farmland.network.actions.EndTurnMessage;
import com.ustudents.farmland.network.general.LoadSaveResponse;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;

import java.io.File;
import java.nio.file.Files;
import java.util.*;

@JsonSerializable
public class Save {
    public static final int timePerTurn = 90;

    @JsonSerializable
    public String name;

    @JsonSerializable
    public Integer turn;

    @JsonSerializable
    public Integer turnTimePassed;

    @JsonSerializable
    public Integer timePassed;

    @JsonSerializable
    public Vector2i mapSize;

    @JsonSerializable
    public Long seed;

    @JsonSerializable
    public Integer maxBorrow;

    @JsonSerializable
    public Integer debtRate;

    @JsonSerializable
    public Integer currentPlayerId;

    @JsonSerializable
    public List<Player> players;

    @JsonSerializable
    public List<Item> buyTurnItemDataBase;

    @JsonSerializable
    public List<List<Item>> buyItemDatabasePerTurn;

    @JsonSerializable
    public List<Item> sellTurnItemDataBase;

    @JsonSerializable
    public List<List<Item>> sellItemDatabasePerTurn;

    @JsonSerializable
    public List<Crop> cropItem;

    @JsonSerializable
    public List<Animal> animalItem;

    @JsonSerializable
    public List<List<Cell>> cells;

    @JsonSerializable
    public Integer capacity;

    public Integer localPlayerId;
    
    @JsonSerializable
    public Boolean startWithBots;

    @JsonSerializable
    public Bot.Difficulty difficulty;

    @JsonSerializable
    public List<Integer> deadPlayers;

    public String path;

    public EventDispatcher<Event> turnEnded = new EventDispatcher<>();

    public EventDispatcher<Event> itemUsed = new EventDispatcher<>();

    public SeedRandom random;

    public Save() {}

    public Save(String name, Vector2i mapSize, Long seed, int numberOfBots, int maxBorrow, int debtRate, Bot.Difficulty difficulty) {
        this.mapSize = mapSize;
        this.seed = seed;

        if (this.seed == null) {
            this.seed = System.currentTimeMillis();
        }

        SeedRandom random = new SeedRandom(this.seed);

        this.deadPlayers = new LinkedList<>();
        this.startWithBots = numberOfBots > 0;
        this.capacity = 1;
        this.difficulty = difficulty;
        this.turn = 0;
        this.turnTimePassed = 0;
        this.timePassed = 0;
        this.currentPlayerId = 0;
        this.name = name;
        this.players = new ArrayList<>();
        this.random = new SeedRandom(seed);
        this.maxBorrow = maxBorrow;
        this.debtRate = debtRate;

        cropItem = new ArrayList<>();
        animalItem = new ArrayList<>();
        for(Item item: Farmland.get().getItemDatabase().values()){
            if(item instanceof Crop){
                cropItem.add(Crop.clone((Crop)item));
            }else if(item instanceof Animal){
                animalItem.add(Animal.clone((Animal)item));
            }

        }

        buyItemDatabasePerTurn = new ArrayList<>();
        buyTurnItemDataBase = new ArrayList<>();
        sellItemDatabasePerTurn = new ArrayList<>();
        sellTurnItemDataBase = new ArrayList<>();

        this.cells = new ArrayList<>();

        Texture cellBackground = Resources.loadTexture("terrain/grass.png");

        for (int x = 0; x < mapSize.x; x++) {
            this.cells.add(new ArrayList<>());

            for (int y = 0; y < mapSize.y; y++) {
                Vector2f spriteRegion = new Vector2f(
                        24 * random.generateInRange(1, 120 / 24),
                        24 * random.generateInRange(1, 120 / 24));
                Sprite sprite = new Sprite(cellBackground,
                        new Vector4f(spriteRegion.x, spriteRegion.y, 24, 24));
                Vector4f viewRectangle = new Vector4f(
                        5 + x * 24,
                        5 + y * 24,
                        5 + x * 24 + 24,
                        5 + y * 24 + 24);

                cells.get(x).add(new Cell(sprite, viewRectangle));
            }
        }

        List<Color> usedColors = new ArrayList<>();
        List<Vector2i> usedLocations = new ArrayList<>();
        usedLocations.add(new Vector2i(mapSize.x / 2 - 1, mapSize.y / 2 - 1));
        for (int i = 0; i < numberOfBots; i++) {
            this.players.add(new Player("Robot " + (i + 1), "Village de Robot " + (i + 1),
                    generateColor(random, usedColors), generateColor(random, new ArrayList<>()),
                    generateColor(random, new ArrayList<>()), generateColor(random, new ArrayList<>()),
                    generateColor(random, new ArrayList<>()), Player.Type.Bot));
            Vector2i villagePosition = generateMapLocation(random, usedLocations);
            this.players.get(i).village.position = new Vector2f(5 + villagePosition.x * 24, 5 + villagePosition.y * 24);
            this.cells.get(villagePosition.x).get(villagePosition.y).setOwned(true, i);
            this.cells.get(villagePosition.x + 1).get(villagePosition.y).setOwned(true, i);
            this.cells.get(villagePosition.x).get(villagePosition.y + 1).setOwned(true, i);
            this.cells.get(villagePosition.x + 1).get(villagePosition.y + 1).setOwned(true, i);
        }

        File f = new File(Resources.getSavesDirectoryName());
        this.path = "save-" + (getMaxSavedGamesId() + 1) + ".json";

        if (Game.isDebugging()) {
            Out.printlnDebug("Savegame created.");
        }
    }

    public Save(Save save) {
        this(save.name, save.players.get(0).name, save.players.get(0).village.name, save.players.get(0).bannerColor,
                save.players.get(0).avatar.bracesColor, save.players.get(0).avatar.shirtColor,
                save.players.get(0).avatar.hatColor, save.players.get(0).avatar.buttonsColor, save.mapSize, save.seed,
                save.getNumberOfBots(), save.maxBorrow, save.debtRate, save.difficulty);
    }

    public Save(String name, String playerName, String playerVillageName, Color playerColor, Color bracesColor, Color shirtColor, Color hatColor, Color buttonColor, Vector2i mapSize, Long seed, int numberOfBots, int maxBorrow, int debtRate, Bot.Difficulty difficulty) {
        this(name, mapSize, seed, numberOfBots, maxBorrow, debtRate, difficulty);
        addPlayer(playerName, playerVillageName, playerColor, bracesColor, shirtColor, hatColor, buttonColor, Player.Type.Human);
    }

    @JsonSerializableConstructor
    public void deserialize() {
        random = new SeedRandom(this.seed);
    }

    public void addPlayer(String name, String villageName, Color bannerColor, Color bracesColor, Color shirtColor, Color hatColor, Color buttonColor, Player.Type type) {
        int playerId = getAvailableHumanId();
        this.players.add(playerId, new Player(name, villageName, bannerColor, bracesColor, shirtColor, hatColor, buttonColor, type));
        Vector2i villagePosition = generateMapLocation(random, getUsedLocations());
        this.players.get(playerId).village.position = new Vector2f(5 + villagePosition.x * 24, 5 + villagePosition.y * 24);
        this.cells.get(villagePosition.x).get(villagePosition.y).setOwned(true, playerId);
        this.cells.get(villagePosition.x + 1).get(villagePosition.y).setOwned(true, playerId);
        this.cells.get(villagePosition.x).get(villagePosition.y + 1).setOwned(true, playerId);
        this.cells.get(villagePosition.x + 1).get(villagePosition.y + 1).setOwned(true, playerId);
    }
    
    public Map<String, Item> getResourceDatabase(){
        Map<String, Item> ResourceDatabase = new HashMap<>();
        for(Item item: cropItem){
            ResourceDatabase.put(item.id,item);
        }

        for(Item item: animalItem){
            ResourceDatabase.put(item.id,item);
        }
        return ResourceDatabase;
    }

    private int getMaxSavedGamesId(){
        File savedDir = new File(Resources.getSavesDirectoryName());
        File[] list = savedDir.listFiles();
        int max = -1;
        assert list != null;
        for (File file : list) {
            String tmp = file.getName().substring(5, 6);

            if (Character.isDigit(tmp.charAt(0))) {
                int fileId = Integer.parseInt(tmp);
                if (fileId > max) {
                    max = fileId;
                }
            }
        }
        return max;
    }

    public void endTurn() {
        if (Game.get().hasAuthority()) {
            if (Game.isDebugging()) {
                Out.printlnDebug("Turn end");
            }

            if (currentPlayerId == players.size() - 1) {
                turn++;
                currentPlayerId = 0;
            } else {
                currentPlayerId++;
            }

            turnEnded.dispatch();

            if (Game.get().getNetMode() == NetMode.DedicatedServer) {
                Farmland.get().getServer().broadcast(new LoadSaveResponse(Farmland.get().getLoadedSave()));
            }
        } else {
            Game.get().getClient().send(new EndTurnMessage());
        }
    }

    public boolean isCurrentPlayerDead() {
        return deadPlayers.contains(currentPlayerId);
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerId);
    }

    private Color generateColor(SeedRandom random, List<Color> usedColors) {
        while (true) {
            boolean unique = true;

            Color color = new Color();
            color.r = random.generateInRange(0, 255) / 255.0f;
            color.g = random.generateInRange(0, 255) / 255.0f;
            color.b = random.generateInRange(0, 255) / 255.0f;
            color.a = random.generateInRange(0, 255) / 255.0f;

            for (Color usedColor : usedColors) {
                if (color.equals(usedColor)) {
                    unique = false;
                    break;
                }
            }

            if (unique) {
                usedColors.add(color);
                return color;
            }
        }
    }

    private Vector2i generateMapLocation(SeedRandom random, List<Vector2i> usedLocations) {
        while (true) {
            boolean unique = true;

            Vector2i position = new Vector2i();
            position.x = random.generateInRange(0, cells.size() - 2);
            position.y = random.generateInRange(0, cells.get(0).size() - 2);

            for (Vector2i usedPosition : usedLocations) {
                if (position.x < usedPosition.x + 2 &&
                        position.x + 2 > usedPosition.x &&
                        position.y < usedPosition.y + 2 &&
                        position.y + 2 > usedPosition.y) {
                    unique = false;
                    break;
                }
            }

            if (unique) {
                usedLocations.add(position);
                return position;
            }
        }
    }

    public boolean hasBots() {
        return getNumberOfBots() > 0;
    }

    public boolean hasMultipleHumans() {
        return  getNumberOfHumans() > 1;
    }

    public boolean hasAnyoneWon() {
        for(Player player : players) {
            if (!Farmland.get().getLoadedSave().deadPlayers.contains(player.getId()) &&
                    player.money >= 1000 && player.remainingDebt <= 0) {
                return true;
            }
        }

        return false;
    }

    public boolean hasAnyHumanWon() {
        if (hasBots() && areAllBotsDead() && (getNumberOfHumans() == 1 || getNumberOfDeadHumans() == getNumberOfHumans() - 1)) {
            return true;
        }

        for(Player player : players) {
            if (player.type == Player.Type.Human && player.money >= 1000 && player.remainingDebt <= 0 && !player.isDead()) {
                return true;
            }
        }

        return false;
    }

    public boolean hasAnyHumanLost() {
        for(Player player : players) {
            if (player.type == Player.Type.Human && player.money <= 0 && !player.isDead()) {
                return true;
            }
        }

        return false;
    }

    public boolean hasAnyBotWon() {
        for(Player player : players) {
            if (player.type == Player.Type.Bot && player.money >= 1000 && player.remainingDebt <= 0 && !player.isDead()) {
                return true;
            }
        }

        return false;
    }

    public boolean hasAnyBotLoose() {
        for(Player player : players) {
            if (player.type == Player.Type.Bot && player.money <= 0 && !player.isDead()) {
                return true;
            }
        }

        return false;
    }

    public int getNumberOfDeadHumans() {
        int count = 0;

        for(Player player : players) {
            if (player.type == Player.Type.Human && player.isHuman()) {
                count++;
            }
        }

        return count;
    }

    public boolean areAllBotsDead() {
        for(Player player : players) {
            if (player.type == Player.Type.Bot && !player.isDead()) {
                return false;
            }
        }

        return true;
    }

    public boolean areAllPlayersDead() {
        for(Player player : players) {
            if (player.type == Player.Type.Human && !player.isDead()) {
                return false;
            }
        }

        return true;
    }

    public int getNumberOfHumans() {
        int count = 0;

        for(Player player : players) {
            if (player.type == Player.Type.Human) {
                count++;
            }
        }

        return count;
    }

    public void kill(Player player) {
        for (int x = 0; x < Farmland.get().getLoadedSave().cells.size(); x++) {
            for (int y = 0; y < Farmland.get().getLoadedSave().cells.get(x).size(); y++) {
                Cell cell = Farmland.get().getLoadedSave().cells.get(x).get(y);

                if (cell.isOwnedBy(player)) {
                    cell.reset();
                }
            }
        }

        Farmland.get().getLoadedSave().deadPlayers.add(player.getId());
    }

    public boolean BotMeetCondition() {
        for(Player player : players) {
            if(player.type == Player.Type.Bot && !Farmland.get().getLoadedSave().deadPlayers.contains(player.getId())) {
                if (player.money <= 0 || (player.money >= 1000 && player.remainingDebt <= 0)){
                    return true;
                }
            }
        }

        return false;
    }

    public Player getLocalPlayer() {
        return players.get(localPlayerId);
    }

    public boolean isLocalPlayerTurn() {
        return getCurrentPlayer().getId().equals(getLocalPlayer().getId());
    }

    public List<Vector2i> getUsedLocations() {
        List<Vector2i> locations = new ArrayList<>();

        for (Player player : players) {
            if (player.village.position != null) {
                Vector2i villagePosition = new Vector2i(((int)player.village.position.x - 5) / 24, ((int)player.village.position.y - 5) / 24);

                if (!locations.contains(villagePosition)) {
                    locations.add(villagePosition);
                }
            }
        }

        return locations;
    }

    public Cell getCell(int x, int y) {
        return cells.get(x).get(y);
    }

    public Cell getCell(Vector2i position) {
        return cells.get(position.x).get(position.y);
    }

    private int getAvailableHumanId() {
        int i = 0;

        for (Player player : players) {
            if (player.type == Player.Type.Undefined) {
                i++;
            }
        }

        for (int x = 0; x < cells.size(); x++) {
            for (int y = 0; y < cells.get(x).size(); y++) {
                Cell cell = cells.get(x).get(y);

                if (cell.isOwnedByBot(this)) {
                    cell.ownerId++;
                }
            }
        }

        return i;
    }
    
    public void fillBuyItemDataBasePerTurn(){
        buyItemDatabasePerTurn.add(new ArrayList<>(buyTurnItemDataBase));
        sellItemDatabasePerTurn.add(new ArrayList<>(sellTurnItemDataBase));
    }

    public void fillTurnItemDataBase(Item item, boolean buyInventory){
        assert(item != null);

        if (buyInventory) {
            boolean contains = false;
            for(Item i: buyTurnItemDataBase){
                if(item.id.equals(i.id)){
                    contains = true;
                    i.quantity += 1;
                }
            }
            if(!contains){
                Item clone = Item.clone(item);
                assert clone != null;
                clone.quantity = 1;
                buyTurnItemDataBase.add(clone);
            }
        }else{
            boolean contains = false;
            for(Item i: sellTurnItemDataBase){
                if(item.id.equals(i.id)){
                    contains = true;
                    i.quantity += 1;
                }
            }
            if(!contains){
                Item clone = Item.clone(item);
                assert clone != null;
                clone.quantity = 1;
                sellTurnItemDataBase.add(clone);
            }
        }
    }

    public void clearTurnItemDatabase(){
        buyTurnItemDataBase.clear();
        sellTurnItemDataBase.clear();
    }

    public int getNumberOfBots() {
        int i = 0;

        for (Player player : players) {
            if (player.type == Player.Type.Bot) {
                i++;
            }
        }

        return i;
    }

    public void removeFile() {
        String filePath = Resources.getSavesDirectoryName() + "/" + path;

        if(!new File(filePath).exists()) {
            return;
        }

        try {
            Files.delete(new File(filePath).toPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Player getPlayerByName(String name) {
        for (Player player : players) {
            if (player.name.equals(name)) {
                return player;
            }
        }

        return null;
    }
}
