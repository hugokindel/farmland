package com.ustudents.farmland.network.general;

import com.ustudents.engine.network.messages.Message;
import com.ustudents.farmland.Farmland;

import java.util.List;

// PROCESSED ON SERVER
public class GameInformationsRequest extends Message {
    @Override
    public void process() {
        String name = Farmland.get().serverConfig.name;
        int capacity = Farmland.get().serverConfig.capacity;
        List<Integer> connectedPlayers = Farmland.get().getListOfConnectedPlayers();
        Farmland.get().getServer().send(senderId, new GameInformationsResponse(name, capacity, connectedPlayers));
    }
}
