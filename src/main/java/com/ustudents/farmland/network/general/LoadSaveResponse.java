package com.ustudents.farmland.network.general;

import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.network.messages.Message;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.core.Save;

import java.util.concurrent.atomic.AtomicReference;

// PROCESSED ON CLIENT
@JsonSerializable
public class LoadSaveResponse extends Message {
    private static AtomicReference<Save> updatedSave = null;

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
        updatedSave = new AtomicReference<>(getSave());

        if (updatedSave.get() != null) {
            updatedSave.get().path = "save-server.json";
            updatedSave.get().localPlayerId = Farmland.get().clientPlayerId.get();
        }
    }

    public static Save getUpdatedSaveGame() {
        if (updatedSave == null) {
            return null;
        }

        Save save = updatedSave.get();

        if (save != null) {
            updatedSave = null;
        }

        return save;
    }
}
