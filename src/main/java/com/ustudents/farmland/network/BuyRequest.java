package com.ustudents.farmland.network;

import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.network.messages.Message;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.core.SaveGame;
import com.ustudents.farmland.core.item.Item;

// PROCESSED ON SERVER
public class BuyRequest extends Message {
    public BuyRequest() {

    }

    public BuyRequest(String itemId) {
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
        SaveGame currentSave = Farmland.get().getCurrentSave();
        int playerId = Farmland.get().getPlayerId(getSenderId());
        Item item = getItem();

        Out.println("Client " + playerId + ", bought " + item.name + " (x1)");

        // TODO: BUY FUNCTION
        currentSave.getCurrentPlayer().setMoney(currentSave.getCurrentPlayer().money - item.value);
        currentSave.getCurrentPlayer().addToInventory(item, "Buy");
        Farmland.get().getCurrentSave().itemsTurn.add(item);

        Farmland.get().getServer().broadcast(new LoadSaveResponse(Farmland.get().getCurrentSave()));
    }
}
