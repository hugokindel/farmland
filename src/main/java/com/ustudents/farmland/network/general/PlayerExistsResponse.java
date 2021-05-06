package com.ustudents.farmland.network.general;

import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.network.messages.Message;

// PROCESSED ON CLIENT
@JsonSerializable
public class PlayerExistsResponse extends Message {
    @JsonSerializable
    Boolean exists;

    public PlayerExistsResponse() {

    }

    public PlayerExistsResponse(boolean exists) {
        this.exists = exists;
    }

    public boolean exists() {
        return exists;
    }
}
