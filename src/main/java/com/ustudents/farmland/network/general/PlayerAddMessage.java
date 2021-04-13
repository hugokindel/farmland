package com.ustudents.farmland.network.general;

import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.network.messages.Message;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.core.SaveGame;

// PROCESSED ON SERVER
@JsonSerializable
public class PlayerAddMessage extends Message {
    @JsonSerializable
    Integer playerId;

    public PlayerAddMessage() {

    }

    public PlayerAddMessage(int playerId) {
        this.playerId = playerId;
    }

    public int getPlayerId() {
        return playerId;
    }

    @Override
    public void process() {
        int playerId = getPlayerId();
        SaveGame currentSave = Farmland.get().getLoadedSave();

        Farmland.get().setPlayerIdForClientId(getSenderId(), playerId);

        if (Farmland.get().serverPlayerIdPerClientId.size() == currentSave.maxNumberPlayers) {
            Farmland.get().getServer().broadcast(new PlayerAllPresentsMessage());
        }
    }
}
