package com.ustudents.farmland.network;

import com.ustudents.engine.core.json.Json;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.network.messages.Message;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.core.SaveGame;

import java.util.Map;

// PROCESSED ON SERVER
@SuppressWarnings("unchecked")
public class PlayerAddMessage extends Message {
    public PlayerAddMessage() {

    }

    public PlayerAddMessage(int playerId) {
        getPayload().put("playerId", Long.valueOf(playerId));
    }

    public int getPlayerId() {
        return ((Long)getPayload().get("playerId")).intValue();
    }

    @Override
    public void process() {
        int playerId = getPlayerId();
        SaveGame currentSave = Farmland.get().getCurrentSave();

        Farmland.get().setPlayerIdForClientId(getSenderId(), playerId);

        if (Farmland.get().serverPlayerIdPerClientId.size() == currentSave.maxNumberPlayers) {
            Farmland.get().getServer().broadcast(new PlayerAllPresentsMessage());
        }
    }
}
