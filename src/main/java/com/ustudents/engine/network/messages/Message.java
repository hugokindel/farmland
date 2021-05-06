package com.ustudents.engine.network.messages;

import com.ustudents.engine.core.json.annotation.JsonSerializable;

@JsonSerializable
public class Message {
    public enum ProcessingSide {
        Server,
        Client,
        Everywhere
    }

    @JsonSerializable
    private String _type;

    protected Integer senderId;

    protected Integer receiverId;

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public Integer getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Integer receiverId) {
        this.receiverId = receiverId;
    }

    public String getType() {
        return _type;
    }

    public void setType(String type) {
        this._type = type;
    }

    public ProcessingSide getProcessingSide() {
        return ProcessingSide.Everywhere;
    }

    public boolean shouldBeHandledOnMainThread() {
        return false;
    }

    public void process() {

    }
}
