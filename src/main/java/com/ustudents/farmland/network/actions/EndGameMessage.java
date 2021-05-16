package com.ustudents.farmland.network.actions;

import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.network.messages.Message;
import com.ustudents.engine.scene.SceneManager;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.scene.menus.ResultMenu;

// PROCESSED ON CLIENT
@JsonSerializable
public class EndGameMessage extends Message {
    @JsonSerializable
    Boolean hasWon;

    public EndGameMessage() {

    }

    public EndGameMessage(Boolean hasWon) {
        this.hasWon = hasWon;
    }

    @Override
    public void process() {
        ResultMenu resultMenu = new ResultMenu();
        resultMenu.currentSave = Farmland.get().getLoadedSave();
        resultMenu.comeFromServer = true;
        resultMenu.isWin = hasWon;
        Farmland.get().unloadSave();
        SceneManager.get().changeScene(resultMenu);
    }

    @Override
    public boolean shouldBeHandledOnMainThread() {
        return true;
    }

    @Override
    public ProcessingSide getProcessingSide() {
        return ProcessingSide.Client;
    }
}
