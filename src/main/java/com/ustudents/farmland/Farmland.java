package com.ustudents.farmland;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.cli.option.annotation.Command;
import com.ustudents.engine.core.json.Json;
import com.ustudents.engine.core.json.JsonReader;
import com.ustudents.farmland.core.SaveGame;
import com.ustudents.farmland.core.item.Crop;
import com.ustudents.farmland.core.item.Item;
import com.ustudents.farmland.scene.menus.MainMenu;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** The main class of the project. */
@Command(name = "farmland", version = "0.0.1", description = "A management game about farming.")
@SuppressWarnings("unchecked")
public class Farmland extends Game {
    private Map<String, Item> itemDatabase;

    private Map<String, SaveGame> saveGames;

    public String saveId;

    @Override
    protected void initialize() {
        changeIcon("ui/farmland_logo.png");
        changeCursor("ui/cursor.png");

        loadItemDatabases();
        loadSavedGames();

        sceneManager.changeScene(new MainMenu());
    }

    @Override
    protected void destroy() {
        saveSavedGames();
    }

    private void loadItemDatabases() {
        itemDatabase = new HashMap<>();

        List<Object> listOfCrops = JsonReader.readArray(Resources.getItemsDirectoryName() + "/crops.json");
        assert listOfCrops != null;
        for (Object object : listOfCrops) {
            Crop crop = Json.deserialize((Map<String, Object>)object, Crop.class);
            assert crop != null;
            itemDatabase.put(crop.id, crop);
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
                assert saveGame != null;
                saveGame.path = path.replace(Resources.getSavesDirectoryName() + "/", "");
                saveGames.put(saveGame.name, saveGame);

            }
        }
    }

    public void saveSavedGames() {
        for (SaveGame saveGame : saveGames.values()) {
            Json.serialize(Resources.getSavesDirectoryName() + "/" + saveGame.path, saveGame);
        }
    }

    public Map<String, Item> getItemDatabase() {
        return itemDatabase;
    }

    public Map<String, SaveGame> getSaveGames() {
        return saveGames;
    }

    public SaveGame getSaveGame(String id) {
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

    public Item getItem(String id) {
        return itemDatabase.get(id);
    }

    public static Farmland get() {
        return (Farmland)Game.get();
    }
}
