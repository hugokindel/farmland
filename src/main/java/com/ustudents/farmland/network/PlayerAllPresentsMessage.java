package com.ustudents.farmland.network;

import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.network.messages.Message;
import com.ustudents.farmland.Farmland;

// PROCESSED ON CLIENT
public class PlayerAllPresentsMessage extends Message {
    @Override
    public void process() {
        Farmland.get().allPlayersPresents.set(true);
    }
}
