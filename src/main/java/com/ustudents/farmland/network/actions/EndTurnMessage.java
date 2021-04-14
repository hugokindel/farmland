package com.ustudents.farmland.network.actions;

import com.ustudents.engine.network.messages.Message;
import com.ustudents.farmland.Farmland;

public class EndTurnMessage extends Message {
    @Override
    public void process() {
        Farmland.get().getLoadedSave().endTurn();
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
