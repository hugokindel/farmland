package com.ustudents.farmland.network.general;

import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.network.messages.Message;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.core.Save;
import com.ustudents.farmland.core.player.Player;

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
        Save currentSave = Farmland.get().getLoadedSave();
        int playerId = getPlayerId();
        boolean exists = false;

        if (currentSave.players.size() > playerId && (currentSave.players.get(playerId).type == Player.Type.Human || currentSave.players.get(playerId).type == Player.Type.Bot)) {
            exists = true;
        }

        Farmland.get().getServer().respond(new PlayerExistsResponse(exists), this);
    }
}
