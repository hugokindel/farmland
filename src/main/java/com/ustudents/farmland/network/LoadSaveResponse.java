package com.ustudents.farmland.network;

import com.ustudents.engine.core.json.Json;
import com.ustudents.engine.network.messages.Message;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.core.SaveGame;

import java.util.Map;

// PROCESSED ON CLIENT
@SuppressWarnings("unchecked")
public class LoadSaveResponse extends Message {
    private static SaveGame updatedSaveGame = null;

    public LoadSaveResponse() {

    }

    public LoadSaveResponse(SaveGame saveGame) {
        getPayload().put("save", saveGame);
    }

    public SaveGame getSave() {
        return Json.deserialize((Map<String, Object>) getPayload().get("save"), SaveGame.class);
    }

    @Override
    public void process() {
        updatedSaveGame = getSave();
        updatedSaveGame.path = "save-server.json";
        updatedSaveGame.localPlayerId = Farmland.get().clientPlayerId.get();
    }

    public static SaveGame getUpdatedSaveGame() {
        SaveGame saveGame = updatedSaveGame;

        if (saveGame != null) {
            updatedSaveGame = null;
        }

        return saveGame;
    }
}
