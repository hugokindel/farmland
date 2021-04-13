package com.ustudents.farmland.network.general;

import com.ustudents.engine.core.json.Json;
import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.network.messages.Message;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.core.SaveGame;

import java.util.Map;

// PROCESSED ON CLIENT
@JsonSerializable
public class LoadSaveResponse extends Message {
    private static SaveGame updatedSaveGame = null;

    @JsonSerializable
    SaveGame save;

    public LoadSaveResponse() {

    }

    public LoadSaveResponse(SaveGame save) {
        this.save = save;
    }

    public SaveGame getSave() {
        return save;
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
