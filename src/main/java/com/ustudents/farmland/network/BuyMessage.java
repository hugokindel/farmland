package com.ustudents.farmland.network;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.network.messages.Message;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.core.item.Item;

// PROCESSED ON SERVER
public class BuyMessage extends Message {
    public BuyMessage() {

    }

    public BuyMessage(String itemId) {
        getPayload().put("item", itemId);
    }

    public String getItemId() {
        return (String) getPayload().get("item");
    }

    public Item getItem() {
        return Farmland.get().getItem(getItemId());
    }

    @Override
    public void process() {
        Item item = getItem();

        if (Game.isDebugging()) {
            Out.println("Client " + Farmland.get().getPlayerId(getSenderId()) + ", bought " + item.name + " (x1)");
        }

        Farmland.get().getLoadedSave().getCurrentPlayer().buy(item, 1);

        Farmland.get().getServer().broadcast(new LoadSaveResponse(Farmland.get().getLoadedSave()));
    }
}
