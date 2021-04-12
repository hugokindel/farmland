package com.ustudents.farmland.network.general;

import com.ustudents.engine.network.messages.Message;
import com.ustudents.farmland.Farmland;

// PROCESSED ON CLIENT
public class PlayerAllPresentsMessage extends Message {
    @Override
    public void process() {
        Farmland.get().clientAllPlayersPresents.set(true);
    }
}
