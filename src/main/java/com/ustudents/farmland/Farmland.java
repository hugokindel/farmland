package com.ustudents.farmland;

import com.ustudents.engine.Game;
import com.ustudents.engine.GameConfig;
import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.cli.option.annotation.Command;
import com.ustudents.engine.core.cli.option.annotation.Option;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.tools.console.Console;
import com.ustudents.engine.core.event.EventDispatcher;
import com.ustudents.engine.core.json.Json;
import com.ustudents.engine.core.json.JsonReader;
import com.ustudents.engine.input.Action;
import com.ustudents.engine.input.Key;
import com.ustudents.engine.input.Mapping;
import com.ustudents.engine.input.MouseButton;
import com.ustudents.engine.network.NetMode;
import com.ustudents.farmland.core.Save;
import com.ustudents.farmland.core.item.*;
import com.ustudents.farmland.core.player.Avatar;
import com.ustudents.farmland.core.player.Player;
import com.ustudents.farmland.network.general.LoadSaveResponse;
import com.ustudents.farmland.scene.InGameScene;
import com.ustudents.farmland.scene.menus.MainMenu;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/** The main class of the project. */
@SuppressWarnings({"unchecked", "unused"})
@Command(name = "farmland", version = "1.0.0", description = "A management game about farming.")
public class Farmland extends Game {
    public Map<String, Item> itemDatabase = new HashMap<>();

    public Map<String, Save> saves = new HashMap<>();

    public String loadedSaveId = null;

    public EventDispatcher loadedSaveChanged = new EventDispatcher();

    public FarmlandConfig config;

    @Option(names = "--no-save", description = "Disable saving system (can still load but will never save).")
    protected boolean noSave = false;

    @Option(names = "--fast-bot", description = "Makes bot turns instantaneous.")
    public boolean fastBot = false;

    @Option(names = "--fast-harvest", description = "Makes animals/crops harvest instantaneous.")
    public boolean fastHarvest = false;

    // SERVER SPECIFIC
    public Map<Integer, Integer> serverPlayerIdPerClientId = new ConcurrentHashMap<>();

    public FarmlandServerConfig serverConfig;

    public CopyOnWriteArrayList<String> serverCommands = new CopyOnWriteArrayList<>();

    // CLIENT SPECIFIC
    public AtomicInteger clientPlayerId = new AtomicInteger(0);

    public AtomicBoolean clientAllPlayersPresents = new AtomicBoolean(false);

    public AtomicBoolean serverGameStarted = new AtomicBoolean(false);

    public String clientServerIp;

    public int clientServerPort;

    @Override
    protected void initialize() {
        changeIcon("ui/farmland_logo.png");
        changeCursor("ui/cursor.png");
        Console.create(new FarmlandConsoleCommands());
        configMessage =
                        "// THIS FILE SHOULD NOT BE MANUALLY EDITED!\n" +
                        "//\n" +
                        "// If you need to reset the settings, ensure the game is shutdown and\n" +
                        "// delete this file, a new one will be generated next time you start the game.\n\n";

        loadConfig();
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
        writeLoadedSave();
        saveConfig();
    }

    @Override
    public void beforeServerStarts() {
        readServerConfig();
    }

    @Override
    public void onServerStarted() {
        server.getClientDisconnectedDispatcher().add((dataType, data) -> {
            if (serverPlayerIdPerClientId.containsKey(data.clientId)) {
                if (getLoadedSave() != null && serverPlayerIdPerClientId.containsKey(data.clientId) &&
                        !getLoadedSave().players.get(serverPlayerIdPerClientId.get(data.clientId)).isDead()) {
                    getLoadedSave().players.get(serverPlayerIdPerClientId.get(data.clientId)).type = Player.Type.Bot;
                    getLoadedSave().players.get(serverPlayerIdPerClientId.get(data.clientId)).name += " (Robot)";
                    serverPlayerIdPerClientId.remove(data.clientId);
                    server.broadcast(new LoadSaveResponse(getLoadedSave()));
                    if (Game.get().getSceneManager().getCurrentScene() instanceof InGameScene && serverPlayerIdPerClientId.isEmpty()) {
                        ((InGameScene)Game.get().getSceneManager().getCurrentScene()).setPause(true);
                    }
                }
            }
        });
    }

    @Override
    public void onServerDestroyed() {
        writeServerConfig();
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

    public void writeLoadedSave() {
        if (loadedSaveId != null) {
            Json.serialize(Resources.getSavesDirectoryName() + "/" + getLoadedSave().path, getLoadedSave());
        }
    }

    public void writeAllSaves() {
        if (!noSave) {
            for (Save save : saves.values()) {
                Json.serialize(Resources.getSavesDirectoryName() + "/" + save.path, save);
            }
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
        if (loadedSaveId == null) {
            return null;
        }

        return saves.get(loadedSaveId);
    }

    public void replaceLoadedSave(Save save) {
        replaceLoadedSave(save, 0);
    }

    public void replaceLoadedSave(Save save, int playerId) {
        saves.put(loadedSaveId, save);
        getLoadedSave().localPlayerId = playerId;
        loadedSaveChanged.dispatch();
    }

    public void loadSave(String saveId) {
        loadSave(saveId, 0);
    }

    public void loadSave(String saveId, int playerId) {
        loadedSaveId = saveId;
        getLoadedSave().localPlayerId = playerId;
        loadedSaveChanged.dispatch();
    }

    public void unloadSave() {
        if (loadedSaveId != null) {
            loadedSaveId = null;
            loadedSaveChanged.dispatch();
        }
    }

    public boolean hasSaveWithName(String name) {
        for (Map.Entry<String, Save> saveEntry : saves.entrySet()) {
            Save save = saveEntry.getValue();

            if (save.name.equals(name)) {
                return true;
            }
        }

        return false;
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

    public int getClientId(int playerId) {
        for (Map.Entry<Integer, Integer> entry : serverPlayerIdPerClientId.entrySet()) {
            if (entry.getValue().equals(playerId)) {
                return entry.getKey();
            }
        }

        return -1;
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
            getServer().broadcast(new LoadSaveResponse(getLoadedSave()));
        }
    }

    private void readServerConfig() {
        if (new File(Resources.getDataDirectory() + "/server.json").exists()) {
            serverConfig = Json.deserialize(Resources.getDataDirectory() + "/server.json", FarmlandServerConfig.class);
        } else {
            serverConfig = new FarmlandServerConfig();
        }
    }

    private void writeServerConfig() {
        Json.serialize(Resources.getDataDirectory() + "/server.json", serverConfig,
                        "// This file contains the server configuration, feel free to edit what you need.\n" +
                        "//\n" +
                        "// Some settings will only happen when you remove the save file because they\n" +
                        "// need to happen before the save file is created. Such as `capacity`,\n" +
                        "// `numberOfBots`, `maximumLoanValue`, `debtRate` and `difficulty`, `mapSize`\n" +
                        "// and `seed`. If you do not respect this directive, unexpected behavior\n" +
                        "// might happen during gameplay.\n" +
                        "//\n" +
                        "// To reset the settings, ensure the server is shutdown and delete this file,\n" +
                        "// a new config file will be generated next time you launch the server.\n" +
                        "//\n" +
                        "// The following recommandations should be followed for the server to work:\n" +
                        "// - `name` should be a string with a size within the range 1 to 16.\n" +
                        "// - `port` should be an integer within the range 1 to 65536.\n" +
                        "// - `capacity` should be an integer within the range 1 to 4.\n" +
                        "// - `mapSize` should be a vector of two integers within th range 8 to 64.\n" +
                        "// - `seed` should be a positive long value.\n" +
                        "// - `numberOfBots` should be an integer within the range 1 to 4.\n" +
                        "// - `maximumLoanValue` should be an integer greater than 0.\n" +
                        "// - `debtRate` should be an integer within the range 1 to 100.\n" +
                        "// - `difficulty` should be either `Easy`, `Normal`, `Hard` or `Impossible`.\n" +
                        "//\n" +
                        "// Have fun!\n\n");
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

    private void loadConfig() {
        config = Json.deserialize(Resources.getConfig().game, FarmlandConfig.class);
        if(Resources.getConfig().commands.isEmpty()){
            initializeCommands(Resources.getConfig());
        }
        reloadCustomizableCommands();
    }

    public void initializeCommands(GameConfig config){
        Map<String, Action> actions = config.commands;
        Action action;

        if(!actions.containsKey("goUp")){
            action = new Action();
            Mapping KeyW = new Mapping("keyboard");
            Mapping KeyUp = new Mapping("keyboard");
            KeyW.bindDownAction(Key.W);
            KeyUp.bindDownAction(Key.Up);
            action.addMapping(KeyW);
            action.addMapping(KeyUp);
            config.commands.put("goUp", action);
        }

        if(!actions.containsKey("goDown")){
            action = new Action();
            Mapping KeyS = new Mapping("keyboard");
            Mapping KeyDown = new Mapping("keyboard");
            KeyS.bindDownAction(Key.S);
            KeyDown.bindDownAction(Key.Down);
            action.addMapping(KeyS);
            action.addMapping(KeyDown);
            config.commands.put("goDown", action);
        }

        if(!actions.containsKey("goLeft")){
            action = new Action();
            Mapping KeyA = new Mapping("keyboard");
            Mapping KeyLeft = new Mapping("keyboard");
            KeyA.bindDownAction(Key.A);
            KeyLeft.bindDownAction(Key.Left);
            action.addMapping(KeyA);
            action.addMapping(KeyLeft);
            config.commands.put("goLeft", action);
        }

        if(!actions.containsKey("goRight")){
            action = new Action();
            Mapping KeyD = new Mapping("keyboard");
            Mapping KeyRight = new Mapping("keyboard");
            KeyD.bindDownAction(Key.D);
            KeyRight.bindDownAction(Key.Right);
            action.addMapping(KeyD);
            action.addMapping(KeyRight);
            config.commands.put("goRight", action);
        }

        if(!actions.containsKey("showTerritory")) {
            action = new Action();
            Mapping KeyCtrlG = new Mapping("keyboard");
            Mapping KeyCtrlD = new Mapping("keyboard");
            KeyCtrlG.bindDownAction(Key.LeftControl);
            KeyCtrlD.bindDownAction(Key.RightControl);
            action.addMapping(KeyCtrlG);
            action.addMapping(KeyCtrlD);
            config.commands.put("showTerritory", action);
        }

        if(!actions.containsKey("putItem")) {
            action = new Action();
            Mapping leftMouseButton = new Mapping("mouse");
            leftMouseButton.bindDownAction(MouseButton.Left);
            action.addMapping(leftMouseButton);
            config.commands.put("putItem", action);
        }

        if(!actions.containsKey("getItem")) {
            action = new Action();
            Mapping RightMouseButton = new Mapping("mouse");
            RightMouseButton.bindDownAction(MouseButton.Right);
            action.addMapping(RightMouseButton);
            config.commands.put("getItem", action);
        }

        if(!actions.containsKey("showDebug")) {
            action = new Action();
            Mapping debug = new Mapping("keyboard");
            debug.bindPressedAction(Key.F1);
            action.addMapping(debug);
            config.commands.put("showDebug", action);
        }

        if(!actions.containsKey("showPerfomance")) {
            action = new Action();
            Mapping showPerfomance = new Mapping("keyboard");
            showPerfomance.bindPressedAction(Key.F2);
            action.addMapping(showPerfomance);
            config.commands.put("showPerfomance", action);
        }

        if(!actions.containsKey("showConsole")) {
            action = new Action();
            Mapping showConsole = new Mapping("keyboard");
            showConsole.bindPressedAction(Key.GraveAccent);
            action.addMapping(showConsole);
            config.commands.put("showConsole", action);
        }
    }

    public void reloadCustomizableCommands(){
        Map<String, Action> actions = Resources.getConfig().commands;

        if(actions.get("goUp").getFirstBindInMapping() <= 0){
            actions.get("goUp").addFirstBindInMapping(Key.W, "down");
        }
        if(actions.get("goDown").getFirstBindInMapping() <= 0){
            actions.get("goDown").addFirstBindInMapping(Key.S, "down");
        }
        if(actions.get("goLeft").getFirstBindInMapping() <= 0){
            actions.get("goLeft").addFirstBindInMapping(Key.A, "down");
        }
        if(actions.get("goRight").getFirstBindInMapping() <= 0){
            actions.get("goRight").addFirstBindInMapping(Key.D, "down");
        }
        if(actions.get("showTerritory").getFirstBindInMapping() <= 0){
            actions.get("showTerritory").addFirstBindInMapping(Key.D, "down");
        }
        if(actions.get("putItem").getFirstBindInMapping() <= 0){
            actions.get("putItem").addFirstBindInMapping(MouseButton.Left, "down");
        }
        if(actions.get("getItem").getFirstBindInMapping() <= 0){
            actions.get("getItem").addFirstBindInMapping(MouseButton.Right, "down");
        }
        if(actions.get("showDebug").getFirstBindInMapping() <= 0){
            actions.get("showDebug").addFirstBindInMapping(Key.F1, "pressed");
        }
        if(actions.get("showPerfomance").getFirstBindInMapping() <= 0){
            actions.get("showPerfomance").addFirstBindInMapping(Key.F2, "pressed");
        }
        if(actions.get("showConsole").getFirstBindInMapping() <= 0){
            actions.get("showConsole").addFirstBindInMapping(Key.GraveAccent, "pressed");
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

        Resources.loadSpritesheet("decoration/city.json");
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
        Resources.loadSpritesheet("ui/window_default.json");
        Resources.loadTexture("ui/en/defeat.png");
        Resources.loadTexture("ui/en/victory.png");
        Resources.loadTexture("ui/fr/defeat.png");
        Resources.loadTexture("ui/fr/victory.png");
    }

    private void loadShaders() {
        Resources.loadShader("spritebatch");
    }

    private void loadSounds() {
        Resources.loadSound("music/main_menu_background.ogg");
    }

    private void loadOrCreateServerSave() {
        if (!saves.containsKey("save-server.json")) {
            Save save = new Save(serverConfig.name, serverConfig.mapSize,
                    serverConfig.seed, serverConfig.numberOfBots, serverConfig.maximumLoanValue,
                    serverConfig.debtRate, serverConfig.difficulty);
            save.path = "save-server.json";
            save.capacity = serverConfig.capacity;

            for (int i = 0; i < save.capacity; i++) {
                save.addPlayer("", "", Player.DEFAULT_BANNER_COLOR, Avatar.DEFAULT_BRACES_COLOR,
                        Avatar.DEFAULT_SHIRT_COLOR, Avatar.DEFAULT_HAT_COLOR, Avatar.DEFAULT_BUTTONS_COLOR,
                        Player.Type.Undefined);
            }

            saves.put("save-server.json", save);
        }

        loadSave("save-server.json", 0);
    }

    private void saveConfig() {
        Resources.getConfig().game = Json.serialize(config);
    }

    public boolean hasServerSave() {
        for (Save save : saves.values()) {
            if (save.path.startsWith("save-server")) {
                return true;
            }
        }

        return false;
    }

    public void resetConfig() {
        Resources.resetConfig();
        config = new FarmlandConfig();
        if(Resources.getConfig().commands.isEmpty()){
            initializeCommands(Resources.getConfig());
        }
        reloadCustomizableCommands();
    }

    public void serverGameEnded() {
        try {
            Thread.sleep(100);
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (getServer().hasMessagesToSend()) {
            try {
                Thread.sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Out.println("The game is over, restart the server to create a new game.");

        getLoadedSave().removeFile();
        unloadSave();
        saves.clear();
        quit();
    }
}
