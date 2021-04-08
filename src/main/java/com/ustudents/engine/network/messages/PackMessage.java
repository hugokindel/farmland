package com.ustudents.engine.network.messages;

public class PackMessage extends Message {
    public PackMessage() {

    }

    public PackMessage(Integer numberOfParts) {
        getPayload().put("numberOfParts", numberOfParts);
    }

    public int getNumberOfParts() {
        return ((Long)getPayload().get("numberOfParts")).intValue();
    }

    @Override
    public Reliability getReliability() {
        return Reliability.Unreliable;
    }
}
