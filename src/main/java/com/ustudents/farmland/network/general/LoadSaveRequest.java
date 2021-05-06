package com.ustudents.farmland.network.general;

import com.ustudents.engine.network.messages.Message;
import com.ustudents.farmland.Farmland;

// PROCESSED ON SERVER
public class LoadSaveRequest extends Message {
    @Override
    public void process() {
        Farmland.get().getServer().send(getSenderId(), new LoadSaveResponse(Farmland.get().getLoadedSave()));
    }
}
