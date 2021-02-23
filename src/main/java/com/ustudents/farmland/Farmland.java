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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** The main class of the project. */
@Command(name = "farmland", version = "0.0.1", description = "A management game about farming.")
@SuppressWarnings("unchecked")
public class Farmland extends Game {
    /*private static ArrayList<Player> players;

    private static boolean playersIsInit;

    private static String kindOfGame;

    private static boolean isInGame;

    private static GoalComponent goal;*/

    private List<Item> itemDatabase;

    private List<SaveGame> saveGames;

    public SaveGame currentSave;

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
        itemDatabase = new ArrayList<>();

        List<Object> listOfCrops = JsonReader.readArray(Resources.getItemsDirectoryName() + "/crops.json");

        for (Object object : listOfCrops) {
            itemDatabase.add(Json.deserialize((Map<String, Object>)object, Crop.class));
        }
    }

    public void loadSavedGames() {
        saveGames = new ArrayList<>();

        File folder = new File(Resources.getSavesDirectoryName());
        File[] listOfFiles = folder.listFiles();

        assert listOfFiles != null;

        for (File file : listOfFiles) {
            if (file.isFile()) {
                String path = file.getPath().replace("\\", "/");
                SaveGame saveGame = Json.deserialize(path, SaveGame.class);
                saveGame.path = path.replace(Resources.getSavesDirectoryName() + "/", "");
                saveGames.add(saveGame);

            }
        }
    }

    public void saveSavedGames() {
        for (SaveGame saveGame : saveGames) {
            Json.serialize(Resources.getSavesDirectoryName() + "/" + saveGame.path, saveGame);
        }
    }

    public List<Item> getItemDatabase() {
        return itemDatabase;
    }

    public List<SaveGame> getSaveGames() {
        return saveGames;
    }

    public SaveGame getSaveGame(String id) {
        for (SaveGame save : saveGames) {
            if (save.path.replace(".json", "").equals(id)) {
                return save;
            }
        }

        return null;
    }

    public SaveGame getCurrentSave() {
        return currentSave;
    }

    public static Farmland get() {
        return (Farmland)Game.get();
    }
}
