package com.ustudents.farmland.network;

import com.ustudents.engine.network.messages.Message;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.core.SaveGame;

// PROCESSED ON SERVER
public class PlayerExistsRequest extends Message {
    public PlayerExistsRequest() {

    }

    public PlayerExistsRequest(int playerId) {
        getPayload().put("playerId", playerId);
    }

    public int getPlayerId() {
        return ((Long) getPayload().get("playerId")).intValue();
    }

    @Override
    public void process() {
        SaveGame currentSave = Farmland.get().getCurrentSave();
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
