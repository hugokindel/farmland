package com.ustudents.farmland.network.actions;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.network.messages.Message;
import com.ustudents.farmland.Farmland;

@JsonSerializable
public class SelectItemMessage extends Message {
    @JsonSerializable
    String itemId;

    public SelectItemMessage() {

    }

    public SelectItemMessage(String itemId) {
        this.itemId = itemId;
    }

    @Override
    public void process() {
        if (Game.isDebugging()) {
            Out.printlnDebug("Client " + Farmland.get().getPlayerId(getSenderId()) +
                    ", selected item " + itemId);
        }

        Farmland.get().getLoadedSave().getCurrentPlayer().selectItem(itemId);
    }

    @Override
    public boolean shouldBeHandledOnMainThread() {
        return true;
    }

    @Override
    public Message.ProcessingSide getProcessingSide() {
        return Message.ProcessingSide.Server;
    }
}
