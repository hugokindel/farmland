package com.ustudents.farmland.network.general;

import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.network.messages.Message;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.core.Save;

// PROCESSED ON CLIENT
@JsonSerializable
public class LoadSaveResponse extends Message {
    private static Save updatedSave = null;

    @JsonSerializable
    Save save;

    public LoadSaveResponse() {

    }

    public LoadSaveResponse(Save save) {
        this.save = save;
    }

    public Save getSave() {
        return save;
    }

    @Override
    public void process() {
        updatedSave = getSave();
        updatedSave.path = "save-server.json";
        updatedSave.localPlayerId = Farmland.get().clientPlayerId.get();
    }

    public static Save getUpdatedSaveGame() {
        Save save = updatedSave;

        if (save != null) {
            updatedSave = null;
        }

        return save;
    }
}
