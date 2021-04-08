package com.ustudents.farmland.network;

import com.ustudents.engine.core.json.Json;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.network.messages.Message;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.core.SaveGame;

import java.util.Map;

// PROCESSED ON SERVER
@SuppressWarnings("unchecked")
public class PlayerAddRequest extends Message {
    public PlayerAddRequest() {

    }

    public PlayerAddRequest(int playerId, String playerName, String villageName, Color bannerColor) {
        getPayload().put("playerId", playerId);
        getPayload().put("playerName", playerName);
        getPayload().put("villageName", villageName);
        getPayload().put("bannerColor", bannerColor);
    }

    public int getPlayerId() {
        return ((Long)getPayload().get("playerId")).intValue();
    }

    public String getPlayerName() {
        return (String)getPayload().get("playerName");
    }

    public String getVillageName() {
        return (String)getPayload().get("villageName");
    }

    public Color getBannerColor() {
        return Json.deserialize((Map<String, Object>)getPayload().get("bannerColor"), Color.class);
    }

    @Override
    public void process() {
        int playerId = getPlayerId();
        SaveGame currentSave = Farmland.get().getCurrentSave();

        currentSave.players.get(playerId).name = getPlayerName();
        currentSave.players.get(playerId).village.name = getVillageName();
        currentSave.players.get(playerId).color = getBannerColor();
        Farmland.get().setPlayerIdForClientId(getSenderId(), playerId);

        if (Farmland.get().serverPlayerIdPerClientId.size() == currentSave.maxNumberPlayers) {
            Farmland.get().getServer().broadcast(new PlayerAllPresentsMessage());
        }
    }
}
