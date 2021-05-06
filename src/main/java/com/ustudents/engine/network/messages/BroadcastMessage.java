package com.ustudents.engine.network.messages;

import java.util.List;

public class BroadcastMessage extends Message {
    public List<Integer> receiverIds;

    public Message messageToSend;

    public BroadcastMessage(List<Integer> receiverIds, Message messageToSend) {
        this.receiverIds = receiverIds;
        this.messageToSend = messageToSend;
    }
}