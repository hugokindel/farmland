package com.ustudents.farmland.network;

import com.ustudents.engine.network.messages.Message;

import java.util.ArrayList;
import java.util.List;

// PROCESSED ON CLIENT
@SuppressWarnings("unchecked")
public class GameInformationsResponse extends Message {
    public GameInformationsResponse() {

    }

    public GameInformationsResponse(String name, int capacity, List<Integer> connectedPlayerIds) {
        getPayload().put("name", name);
        getPayload().put("capacity", capacity);
        getPayload().put("connectedPlayerIds", connectedPlayerIds);
    }

    public String getName() {
        return (String)getPayload().get("name");
    }

    public int getCapacity() {
        return ((Long)getPayload().get("capacity")).intValue();
    }

    public List<Integer> getConnectedPlayerIds() {
        List<Integer> list = new ArrayList<>();

        for (Long element : ((List<Long>)getPayload().get("connectedPlayerIds"))) {
            if (element != null) {
                list.add(element.intValue());
            }
        }

        return list;
    }

    public int getNumberOfConnectedPlayers() {
        return getConnectedPlayerIds().size();
    }
}
