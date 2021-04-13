package com.ustudents.farmland.network.general;

import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.network.messages.Message;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.core.SaveGame;

// PROCESSED ON SERVER
@JsonSerializable
public class PlayerExistsRequest extends Message {
    @JsonSerializable
    Integer playerId;

    public PlayerExistsRequest() {

    }

    public PlayerExistsRequest(int playerId) {
        this.playerId = playerId;
    }

    public int getPlayerId() {
        return playerId;
    }

    @Override
    public void process() {
        SaveGame currentSave = Farmland.get().getLoadedSave();
        int playerId = getPlayerId();
        boolean exists = false;

        if (currentSave.players.size() > playerId &&
                currentSave.players.get(playerId).typeOfPlayer.equals("Humain") &&
                !currentSave.players.get(playerId).name.equals("TEMP")) {
            exists = true;
        }

        Farmland.get().getServer().respond(new PlayerExistsResponse(exists), this);
    }
}
