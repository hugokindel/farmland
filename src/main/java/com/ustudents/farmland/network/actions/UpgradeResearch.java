package com.ustudents.farmland.network.actions;

import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.network.messages.Message;
import com.ustudents.farmland.Farmland;

public class UpgradeResearch extends Message {
    @JsonSerializable
    String researchName;

    public UpgradeResearch() {

    }

    public UpgradeResearch(String researchName) {
        this.researchName = researchName;
    }

    @Override
    public void process() {
        Farmland.get().getLoadedSave().getCurrentPlayer().upgradeResearch(researchName);
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
