package com.ustudents.farmland.network.general;

import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.network.messages.Message;

import java.util.ArrayList;
import java.util.List;

// PROCESSED ON CLIENT
@JsonSerializable
public class GameInformationsResponse extends Message {
    @JsonSerializable
    String name;

    @JsonSerializable
    Integer capacity;

    @JsonSerializable
    List<Integer> connectedPlayerIds;

    public GameInformationsResponse() {

    }

    public GameInformationsResponse(String name, int capacity, List<Integer> connectedPlayerIds) {
        this.name = name;
        this.capacity = capacity;
        this.connectedPlayerIds = connectedPlayerIds;
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    public List<Integer> getConnectedPlayerIds() {
        return connectedPlayerIds;
    }

    public int getNumberOfConnectedPlayers() {
        return getConnectedPlayerIds().size();
    }

    @Override
    public String toString() {
        return "GameInformationsResponse{" +
                "name='" + name + '\'' +
                ", capacity=" + capacity +
                ", connectedPlayerIds=" + connectedPlayerIds +
                '}';
    }
}
