package com.ustudents.farmland.network;

import com.ustudents.engine.network.messages.Message;

// PROCESSED ON CLIENT
public class PlayerExistsResponse extends Message {
    public PlayerExistsResponse() {

    }

    public PlayerExistsResponse(boolean exists) {
        getPayload().put("exists", exists);
    }

    public boolean exists() {
        return (Boolean) getPayload().get("exists");
    }
}
