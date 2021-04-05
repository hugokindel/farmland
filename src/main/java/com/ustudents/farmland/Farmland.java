package com.ustudents.farmland;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.cli.option.annotation.Command;
import com.ustudents.engine.core.cli.option.annotation.Option;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.core.json.Json;
import com.ustudents.engine.core.json.JsonReader;
import com.ustudents.engine.core.json.JsonWriter;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.network.NetMode;
import com.ustudents.engine.network.Packet;
import com.ustudents.farmland.core.SaveGame;
import com.ustudents.farmland.core.item.*;
import com.ustudents.farmland.scene.menus.MainMenu;
import org.joml.Vector2i;

import java.io.File;
import java.net.DatagramPacket;
import java.util.*;

/** The main class of the project. */
@Command(name = "farmland", version = "0.0.1", description = "A management game about farming.")
@SuppressWarnings("unchecked")
public class Farmland extends Game {
    private Map<String, Item> itemDatabase;

    private Map<String, SaveGame> saveGames;

    private Map<String, Object> serverConfig;

    public String saveId;

    @Override
    protected void initialize() {
        changeIcon("ui/farmland_logo.png");
        changeCursor("ui/cursor.png");

        //clearUselessSavedGames();
        loadItemDatabases();
        loadSavedGames();

        if (getNetMode() == NetMode.DedicatedServer) {
            if (saveGames.containsKey("save-server.json")) {
                saveId = "save-server.json";
            }
        }

        sceneManager.changeScene(new MainMenu());
    }

    @Override
    protected void destroy() {
        saveSavedGames();
    }

    @Override
    public void onServerStarted() {
        if (new File(Resources.getDataDirectory() + "/server.json").exists()) {
            serverConfig = JsonReader.readMap(Resources.getDataDirectory() + "/server.json");
        } else {
            serverConfig = new HashMap<>();
            serverConfig.put("serverName", "Local server");
        }
    }

    @Override
    public Packet onServerHandleRequest(Packet packet) {
        Out.println("Farmland handle");

        Packet answer = new Packet(new LinkedHashMap<>(), packet.address, packet.datagram);

        if (packet.data.get("command").equals("loadWorld")) {
            if (getCurrentSave() == null) {
                SaveGame saveGame = new SaveGame((String)serverConfig.get("serverName"), "Forx", "Forx's village", Color.RED, new Vector2i(16, 16), System.currentTimeMillis(), 0);
                saveGame.path = "save-server.json";
                saveGames.put("save-server.json", saveGame);
                saveId = "save-server.json";
            }
            answer.data.put("world", Json.serialize(getCurrentSave()));
            return answer;
        }

        return null;
    }

    @Override
    public void onServerDestroyed() {
        JsonWriter.writeToFile(Resources.getDataDirectory() + "/server.json", serverConfig);
    }

    public void loadItemDatabases() {
        itemDatabase = new HashMap<>();

        List<Object> listOfCrops = JsonReader.readArray(Resources.getItemsDirectoryName() + "/crops.json");
        assert listOfCrops != null;
        for (Object object : listOfCrops) {
            Crop crop = Json.deserialize((Map<String, Object>)object, Crop.class);
            assert crop != null;
            itemDatabase.put(crop.id, crop);
        }

        List<Object> listOfAnimals = JsonReader.readArray(Resources.getItemsDirectoryName() + "/animals.json");
        assert listOfAnimals != null;
        for (Object object : listOfAnimals) {
            Animal animal = Json.deserialize((Map<String, Object>)object, Animal.class);
            assert animal != null;
            itemDatabase.put(animal.id, animal);
        }

        List<Object> listOfProperties = JsonReader.readArray(Resources.getItemsDirectoryName() + "/property.json");
        assert listOfProperties != null;
        for (Object object : listOfProperties) {
            Property property = Json.deserialize((Map<String, Object>)object, Property.class);
            assert property != null;
            itemDatabase.put(property.id, property);
        }

        List<Object> listOfDecorations = JsonReader.readArray(Resources.getItemsDirectoryName() + "/decoration.json");
        assert listOfDecorations != null;
        for (Object object : listOfDecorations) {
            Decoration decoration = Json.deserialize((Map<String, Object>)object, Decoration.class);
            assert decoration != null;
            itemDatabase.put(decoration.id, decoration);
        }
    }

    public void loadSavedGames() {
        saveGames = new HashMap<>();

        File folder = new File(Resources.getSavesDirectoryName());
        File[] listOfFiles = folder.listFiles();

        assert listOfFiles != null;

        for (File file : listOfFiles) {
            if (file.isFile()) {
                String path = file.getPath().replace("\\", "/");
                SaveGame saveGame = Json.deserialize(path, SaveGame.class);
                if (saveGame != null) {
                    saveGame.path = path.replace(Resources.getSavesDirectoryName() + "/", "");
                    saveGames.put(saveGame.name, saveGame);
                } else {
                    Out.printlnError("Cannot load savegame: " + path);
                }

            }
        }
    }

    public void saveSavedGames(){
        for (SaveGame saveGame : saveGames.values()) {
            Json.serialize(Resources.getSavesDirectoryName() + "/" + saveGame.path, saveGame);
        }
        Farmland.get().clearUselessSavedGames();
    }

    public void clearUselessSavedGames(){
        File savedDir = new File(Resources.getSavesDirectoryName());
        File[] list = savedDir.listFiles();
        File toDelete = null;
        assert list != null;
        for(int i = list.length-1; toDelete == null && i > 0 ; i--){
            Map<String,Object> json = JsonReader.readMap(list[i].getPath());
            assert json != null;
            for(int j = i-1; toDelete == null && j >= 0 ; j--){
                Map<String,Object> jsonTwo = JsonReader.readMap(list[j].getPath());
                assert jsonTwo != null;
                if(json.get("name").equals(jsonTwo.get("name"))){
                    toDelete = list[j];
                }
            }
        }
        if(toDelete!= null)
            toDelete.delete();
    }

    public Map<String, Item> getItemDatabase() {
        return itemDatabase;
    }

    public Map<String, Item> getResourceDatabase(){
        Map<String, Item> ResourceDatabase = new HashMap<>();

        for (Item item : itemDatabase.values()){
            if(item.getClass() == Crop.class || item.getClass() == Animal.class){
                ResourceDatabase.put(item.id,item);
            }
        }

        return ResourceDatabase;
    }

    public Map<String, Item> getCropDatabase(){
        Map<String, Item> CropDatabase = new HashMap<>();

        for (Item item : itemDatabase.values()){
            if(item.getClass() == Crop.class){
                CropDatabase.put(item.id,item);
            }
        }

        return CropDatabase;
    }

    public Map<String, Item> getAnimalDatabase(){
        Map<String, Item> AnimalDatabase = new HashMap<>();

        for (Item item : itemDatabase.values()){
            if(item.getClass() == Animal.class){
                AnimalDatabase.put(item.id,item);
            }
        }

        return AnimalDatabase;
    }

    public Map<String, Item> getDecorationDatabase(){
        Map<String, Item> DecorationDatabase = new HashMap<>();

        for (Item item : itemDatabase.values()){
            if(item.getClass() == Decoration.class){
                DecorationDatabase.put(item.id,item);
            }
        }

        return DecorationDatabase;
    }

    public Map<String, Item> getPropertyDatabase(){
        Map<String, Item> PropertyDatabase = new HashMap<>();

        for (Item item : itemDatabase.values()){
            if(item.getClass() == Property.class){
                PropertyDatabase.put(item.id,item);
            }
        }

        return PropertyDatabase;
    }

    public Map<String, SaveGame> getSaveGames() {
        return saveGames;
    }

    public SaveGame getSaveGameWithId(String id) {
        for (SaveGame save : saveGames.values()) {
            if (save.path.replace(".json", "").equals(id)) {
                return save;
            }
        }

        return null;
    }

    public SaveGame getCurrentSave() {
        if (saveId == null) {
            return null;
        }

        return saveGames.get(saveId);
    }

    public void setCurrentSave(SaveGame saveGame){
        saveGames.put(saveId,saveGame);
    }

    public Item getItem(String id) {
        return itemDatabase.get(id);
    }

    public static Farmland get() {
        return (Farmland)Game.get();
    }
}
