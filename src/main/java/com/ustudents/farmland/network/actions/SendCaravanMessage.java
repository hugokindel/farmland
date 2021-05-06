package com.ustudents.farmland.network.actions;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.network.messages.Message;
import com.ustudents.farmland.Farmland;

@JsonSerializable
public class SendCaravanMessage extends Message {
    @JsonSerializable
    Integer travelPrice;

    @JsonSerializable
    Integer travelTime;

    @JsonSerializable
    Integer sellValue;

    @JsonSerializable
    String itemId;

    public SendCaravanMessage() {

    }

    public SendCaravanMessage(int travelPrice, int travelTime, int sellValue, String itemId) {
        this.travelPrice = travelPrice;
        this.travelTime = travelTime;
        this.sellValue = sellValue;
        this.itemId = itemId;
    }

    @Override
    public void process() {
        Farmland.get().getLoadedSave().getCurrentPlayer().sendCaravan(travelPrice, travelTime, sellValue, itemId);
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
