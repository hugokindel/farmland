package com.ustudents.engine.network.net2.messages;

public class ReceivedMessage extends Message {
    public ReceivedMessage() {

    }

    public ReceivedMessage(Long receivedId) {
        getPayload().put("receivedId", receivedId);
    }

    public long getReceivedId() {
        return (Long)getPayload().get("receivedId");
    }

    @Override
    public Reliability getReliability() {
        return Reliability.Unreliable;
    }
}
