package com.ustudents.farmland.network.actions;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.network.messages.Message;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.core.item.Item;

// PROCESSED ON SERVER
@JsonSerializable
public class BuyItemMessage extends Message {
    @JsonSerializable
    String itemId;

    public BuyItemMessage() {

    }

    public BuyItemMessage(String itemId) {
        this.itemId = itemId;
    }

    public String getItemId() {
        return itemId;
    }

    public Item getItem() {
        return Farmland.get().getItem(getItemId());
    }

    @Override
    public void process() {
        Item item = getItem();

        if (Game.isDebugging()) {
            Out.printlnDebug("Client " + Farmland.get().getPlayerId(getSenderId()) + ", bought " + item.nameId + " (x1)");
        }

        Farmland.get().getLoadedSave().getCurrentPlayer().buyItem(item, 1);
    }

    @Override
    public boolean shouldBeHandledOnMainThread() {
        return true;
    }

    @Override
    public ProcessingSide getProcessingSide() {
        return ProcessingSide.Server;
    }
}
