package com.ustudents.farmland.network;

import com.ustudents.engine.network.messages.Message;
import com.ustudents.farmland.Farmland;

import java.util.List;

// PROCESSED ON SERVER
public class GameInformationsRequest extends Message {
    @Override
    public void process() {
        String name = (String)Farmland.get().serverConfig.get("serverName");
        int capacity = ((Long)Farmland.get().serverConfig.get("maxNumberPlayers")).intValue();
        List<Integer> connectedPlayers = Farmland.get().getListOfConnectedPlayers();
        Farmland.get().getServer().send(senderAddress, new GameInformationsResponse(name, capacity, connectedPlayers));
    }
}
