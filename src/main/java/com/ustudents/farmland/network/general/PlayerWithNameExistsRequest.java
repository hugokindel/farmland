package com.ustudents.farmland.network.general;

import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.network.messages.Message;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.core.Save;
import com.ustudents.farmland.core.player.Player;

// PROCESSED ON SERVER
@JsonSerializable
public class PlayerWithNameExistsRequest extends Message {
    @JsonSerializable
    String playerName;

    public PlayerWithNameExistsRequest() {

    }

    public PlayerWithNameExistsRequest(String playerName) {
        this.playerName = playerName;
    }

    @Override
    public void process() {
        Save currentSave = Farmland.get().getLoadedSave();
        boolean exists = currentSave.getPlayerByName(playerName) != null;
        Farmland.get().getServer().respond(new PlayerExistsResponse(exists), this);
    }
}
