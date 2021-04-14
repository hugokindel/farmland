package com.ustudents.farmland;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.cli.option.annotation.Command;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.core.event.EventDispatcher;
import com.ustudents.engine.core.json.Json;
import com.ustudents.engine.core.json.JsonReader;
import com.ustudents.engine.core.json.JsonWriter;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.network.NetMode;
import com.ustudents.farmland.core.Save;
import com.ustudents.farmland.core.item.*;
import com.ustudents.farmland.core.player.Player;
import com.ustudents.farmland.network.general.LoadSaveResponse;
import com.ustudents.farmland.scene.menus.MainMenu;
import org.joml.Vector2i;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/** The main class of the project. */
@SuppressWarnings({"unchecked", "unused"})
@Command(name = "farmland", version = "0.0.1", description = "A management game about farming.")
public class Farmland extends Game {
    public Map<String, Item> itemDatabase = new HashMap<>();

    public Map<String, Save> saves = new HashMap<>();

    public Map<String, Object> serverSettings = new HashMap<>();

    public String saveId = null;

    // SERVER SPECIFIC
    public Map<Integer, Integer> serverPlayerIdPerClientId = new ConcurrentHashMap<>();

    // CLIENT SPECIFIC
    public AtomicInteger clientPlayerId = new AtomicInteger(0);

    public AtomicBoolean clientAllPlayersPresents = new AtomicBoolean(false);

    public AtomicBoolean clientGameStarted = new AtomicBoolean(false);

    public EventDispatcher saveChanged = new EventDispatcher();

    @Override
    protected void initialize() {
        changeIcon("ui/farmland_logo.png");
        changeCursor("ui/cursor.png");

        loadTextures();
        loadShaders();
        loadSounds();
        loadItems();
        readAllSaves();

        if (getNetMode() == NetMode.DedicatedServer || getNetMode() == NetMode.ListenServer) {
            loadOrCreateServerSave();
        }

        sceneManager.changeScene(new MainMenu());
    }

    @Override
    protected void destroy() {
        writeAllSaves();
    }

    @Override
    public void onServerStarted() {
        loadServerSettings();
        server.getClientDisconnectedDispatcher().add((dataType, data) -> {
            Farmland.get().getLoadedSave().players.get(serverPlayerIdPerClientId.get(data.clientId)).type = Player.Type.Robot;
            Farmland.get().getLoadedSave().players.get(serverPlayerIdPerClientId.get(data.clientId)).name += " (Robot)";
            serverPlayerIdPerClientId.remove(data.clientId);
            server.broadcast(new LoadSaveResponse(getLoadedSave()));
        });
    }

    @Override
    public void onServerDestroyed() {
        JsonWriter.writeToFile(Resources.getDataDirectory() + "/server.json", serverSettings);
    }

    public static Farmland get() {
        return (Farmland)Game.get();
    }

    public void readAllSaves() {
        saves = new HashMap<>();

        File folder = new File(Resources.getSavesDirectoryName());
        File[] listOfFiles = folder.listFiles();

        assert listOfFiles != null;

        for (File file : listOfFiles) {
            if (file.isFile()) {
                String path = file.getPath().replace("\\", "/");
                Save save = Json.deserialize(path, Save.class);
                if (save != null) {
                    save.path = path.replace(Resources.getSavesDirectoryName() + "/", "");
                    saves.put(save.name, save);
                } else {
                    Out.printlnError("Cannot load savegame: " + path);
                }
            }
        }
    }

    public void writeAllSaves() {
        for (Save save : saves.values()) {
            Json.serialize(Resources.getSavesDirectoryName() + "/" + save.path, save);
        }
    }

    public Map<String, Save> getSaves() {
        return saves;
    }

    public Save getSaveWithFilename(String id) {
        for (Save save : saves.values()) {
            if (save.path.replace(".json", "").equals(id)) {
                return save;
            }
        }

        return null;
    }

    public Save getLoadedSave() {
        if (saveId == null) {
            return null;
        }

        return saves.get(saveId);
    }

    public void replaceLoadedSave(Save save) {
        replaceLoadedSave(save, 0);
    }

    public void replaceLoadedSave(Save save, int playerId) {
        saves.put(saveId, save);
        Farmland.get().getLoadedSave().localPlayerId = playerId;
        saveChanged.dispatch();
    }

    public void loadSave(String saveId) {
        loadSave(saveId, 0);
    }

    public void loadSave(String saveId, int playerId) {
        Farmland.get().saveId = saveId;
        Farmland.get().getLoadedSave().localPlayerId = playerId;
        saveChanged.dispatch();
    }

    public void unloadSave() {
        Farmland.get().saveId = null;
        saveChanged.dispatch();
    }

    public Map<String, Item> getItemDatabase() {
        return itemDatabase;
    }

    public Map<String, Item> getResourceDatabase() {
        return itemDatabase.values()
                .stream()
                .filter(i -> i instanceof Crop || i instanceof Animal)
                .collect(Collectors.toMap(Item::getId, Item::get));
    }

    public Item getItem(String id) {
        return itemDatabase.get(id);
    }

    public int getPlayerId(int clientId) {
        return serverPlayerIdPerClientId.get(clientId);
    }

    public List<Integer> getListOfConnectedPlayers() {
        List<Integer> list = new ArrayList<>();

        for (Map.Entry<Integer, Integer> connectedPlayer : serverPlayerIdPerClientId.entrySet()) {
            list.add(connectedPlayer.getValue());
        }

        return list;
    }

    public void setPlayerIdForClientId(int clientId, int playerId) {
        serverPlayerIdPerClientId.put(clientId, playerId);
    }

    public void serverBroadcastSave() {
        if (Game.get().getNetMode() == NetMode.DedicatedServer) {
            Farmland.get().getServer().broadcast(new LoadSaveResponse(Farmland.get().getLoadedSave()));
        }
    }

    private void loadServerSettings() {
        if (new File(Resources.getDataDirectory() + "/server.json").exists()) {
            serverSettings = JsonReader.readMap(Resources.getDataDirectory() + "/server.json");
        } else {
            serverSettings = new HashMap<>();
            serverSettings.put("serverName", "Mon serveur");
            serverSettings.put("maxNumberPlayers", 2L);
            serverSettings.put("numberBots", 4L);
        }
    }

    private void loadItems() {
        loadItemDatabase("animals", Animal.class);
        loadItemDatabase("crops", Crop.class);
        loadItemDatabase("decorations", Decoration.class);
        loadItemDatabase("properties", Property.class);
    }

    private <T extends Item> void loadItemDatabase(String filename, Class<T> type) {
        List<Object> database =
                JsonReader.readArray(Resources.getDataDirectory() + "/items/" + filename + ".json");
        assert database != null;

        for (Object object : database) {
            T item = Json.deserialize((Map<String, Object>)object, type);
            assert item != null;

            itemDatabase.put(item.id, item);
        }
    }

    private void loadTextures() {
        Resources.loadSpritesheet("animals/chicken.json");
        Resources.loadSpritesheet("animals/cow.json");
        Resources.loadSpritesheet("animals/goat.json");
        Resources.loadSpritesheet("animals/pig.json");
        Resources.loadSpritesheet("animals/sheep.json");

        Resources.loadSpritesheet("crops/corn.json");
        Resources.loadSpritesheet("crops/grapes.json");
        Resources.loadSpritesheet("crops/orange.json");
        Resources.loadSpritesheet("crops/pineapple.json");
        Resources.loadSpritesheet("crops/shuttle.json");
        Resources.loadSpritesheet("crops/strawberry.json");
        Resources.loadSpritesheet("crops/tomato.json");
        Resources.loadSpritesheet("crops/watermelon.json");

        Resources.loadSpritesheet("decoration/cities.json");
        Resources.loadSpritesheet("decoration/mountain.json");

        Resources.loadSpritesheet("property/crops.json");
        Resources.loadSpritesheet("property/fence.json");

        Resources.loadTexture("terrain/grass.png");

        Resources.loadTexture("ui/breeder.png");
        Resources.loadTexture("ui/breeder2.png");
        Resources.loadSpritesheet("ui/button_default.json");
        Resources.loadSpritesheet("ui/button_down.json");
        Resources.loadSpritesheet("ui/button_focused.json");
        Resources.loadTexture("ui/cursor.png");
        Resources.loadTexture("ui/defeat.png");
        Resources.loadTexture("ui/farmer.png");
        Resources.loadTexture("ui/farmer2.png");
        Resources.loadTexture("ui/farmer2breeder.png");
        Resources.loadTexture("ui/farmer2breeder2.png");
        Resources.loadTexture("ui/farmerbreeder.png");
        Resources.loadTexture("ui/farmerbreeder2.png");
        Resources.loadTexture("ui/farmland_logo.png");
        Resources.loadTexture("ui/farmland_title.png");
        Resources.loadTexture("ui/frame.png");
        Resources.loadTexture("ui/gold.png");
        Resources.loadSpritesheet("ui/map_cell_cursor.json");
        Resources.loadSpritesheet("ui/map_territory_indicator_white.json");
        Resources.loadTexture("ui/player.png");
        Resources.loadTexture("ui/victory.png");
        Resources.loadSpritesheet("ui/window_default.json");
    }

    private void loadShaders() {
        Resources.loadShader("spritebatch");
    }

    private void loadSounds() {
        Resources.loadSound("music/main_menu_background.ogg");
    }

    private void loadOrCreateServerSave() {
        if (!saves.containsKey("save-server.json")) {
            Save save = new Save((String) serverSettings.get("serverName"), new Vector2i(16, 16),
                    System.currentTimeMillis(), ((Long) serverSettings.get("numberBots")).intValue());
            save.path = "save-server.json";
            save.maxNumberPlayers = ((Long) serverSettings.get("maxNumberPlayers")).intValue();
            for (int i = 0; i < save.maxNumberPlayers; i++) {
                save.addPlayer("_temp", "_temp", Color.RED);
            }
            saves.put("save-server.json", save);
        }

        loadSave("save-server.json", 0);
    }
}
