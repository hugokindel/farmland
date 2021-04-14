package com.ustudents.farmland.network.general;

import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.network.messages.Message;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.core.Save;

// PROCESSED ON SERVER
@JsonSerializable
public class PlayerCreateMessage extends Message {
    @JsonSerializable
    Integer playerId;

    @JsonSerializable
    String playerName;

    @JsonSerializable
    String villageName;

    @JsonSerializable
    Color bannerColor;

    public PlayerCreateMessage() {

    }

    public PlayerCreateMessage(int playerId, String playerName, String villageName, Color bannerColor) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.villageName = villageName;
        this.bannerColor = bannerColor;
    }

    public int getPlayerId() {
        return playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getVillageName() {
        return villageName;
    }

    public Color getBannerColor() {
        return bannerColor;
    }

    @Override
    public void process() {
        int playerId = getPlayerId();
        Save currentSave = Farmland.get().getLoadedSave();

        currentSave.players.get(playerId).name = getPlayerName();
        currentSave.players.get(playerId).village.name = getVillageName();
        currentSave.players.get(playerId).color = getBannerColor();

        PlayerAddMessage message = new PlayerAddMessage(playerId);
        message.setSenderId(getSenderId());
        message.process();
    }
}
