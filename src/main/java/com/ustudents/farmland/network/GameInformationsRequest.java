package com.ustudents.farmland.network;

import com.ustudents.engine.network.messages.Message;
import com.ustudents.farmland.Farmland;

import java.util.List;

// PROCESSED ON SERVER
public class GameInformationsRequest extends Message {
    @Override
    public void process() {
        String name = (String)Farmland.get().serverSettings.get("serverName");
        int capacity = ((Long)Farmland.get().serverSettings.get("maxNumberPlayers")).intValue();
        List<Integer> connectedPlayers = Farmland.get().getListOfConnectedPlayers();
        Farmland.get().getServer().send(senderId, new GameInformationsResponse(name, capacity, connectedPlayers));
    }
}
