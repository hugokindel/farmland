package com.ustudents.farmland.network.general;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.network.messages.Message;
import com.ustudents.engine.scene.Scene;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.core.Save;
import com.ustudents.farmland.core.player.Player;
import com.ustudents.farmland.scene.InGameScene;
import com.ustudents.farmland.scene.menus.MainMenu;

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
        Save currentSave = Farmland.get().getLoadedSave();

        Farmland.get().setPlayerIdForClientId(getSenderId(), playerId);

        if (currentSave.players.get(playerId).type == Player.Type.Bot) {
            currentSave.players.get(playerId).type = Player.Type.Human;
            currentSave.players.get(playerId).name = currentSave.players.get(playerId).name.substring(0, currentSave.players.get(playerId).name.length() - " (Robot)".length());
        }

        Scene currentScene = Game.get().getSceneManager().getCurrentScene();

        if (currentScene instanceof MainMenu && Farmland.get().serverPlayerIdPerClientId.size() == currentSave.capacity) {
            Farmland.get().getServer().broadcast(new PlayerAllPresentsMessage());
        } else if (currentScene instanceof InGameScene) {
            if (((InGameScene)currentScene).inPause) {
                ((InGameScene)currentScene).setPause(false);
            }

            Farmland.get().serverBroadcastSave();
            Farmland.get().getServer().send(getSenderId(), new PlayerAllPresentsMessage());
        }
    }
}
