package com.ustudents.farmland.network.actions;

import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.network.messages.Message;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.core.system.Research;

@JsonSerializable
public class UpgradeResearch extends Message {
    @JsonSerializable
    Research.Type researchName;

    public UpgradeResearch() {

    }

    public UpgradeResearch(Research.Type researchName) {
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
