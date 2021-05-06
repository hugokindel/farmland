package com.ustudents.farmland.network.general;

import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.network.messages.Message;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.core.Save;
import com.ustudents.farmland.core.player.Player;

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

    @JsonSerializable
    Color bracesColor;

    @JsonSerializable
    Color shirtColor;

    @JsonSerializable
    Color hatColor;

    @JsonSerializable
    Color buttonsColor;

    public PlayerCreateMessage() {

    }

    public PlayerCreateMessage(int playerId, String playerName, String villageName, Color bannerColor, Color bracesColor, Color shirtColor, Color hatColor, Color buttonsColor) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.villageName = villageName;
        this.bannerColor = bannerColor;
        this.bracesColor = bracesColor;
        this.shirtColor = shirtColor;
        this.hatColor = hatColor;
        this.buttonsColor = buttonsColor;
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
        currentSave.players.get(playerId).bannerColor = getBannerColor();
        currentSave.players.get(playerId).avatar.bracesColor = bracesColor;
        currentSave.players.get(playerId).avatar.shirtColor = shirtColor;
        currentSave.players.get(playerId).avatar.hatColor = hatColor;
        currentSave.players.get(playerId).avatar.buttonsColor = buttonsColor;
        currentSave.players.get(playerId).type = Player.Type.Human;

        PlayerAddMessage message = new PlayerAddMessage(playerId);
        message.setSenderId(getSenderId());
        message.process();
    }
}
