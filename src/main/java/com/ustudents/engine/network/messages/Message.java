package com.ustudents.engine.network.messages;

import com.ustudents.engine.core.json.JsonReader;
import com.ustudents.engine.core.json.annotation.JsonSerializable;

import java.io.ByteArrayInputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

@JsonSerializable
public class Message {
    public enum Reliability {
        Unreliable,
        Reliable,
        OrderedAndReliable
    }

    public InetSocketAddress senderAddress;

    public InetSocketAddress receiverAddress;

    @JsonSerializable
    protected Long id;

    @JsonSerializable
    protected Integer senderId;

    @JsonSerializable
    protected Integer receiverId;

    @JsonSerializable
    protected Map<String, Object> payload;

    @JsonSerializable
    protected String type;

    public Message() {
        payload = new LinkedHashMap<>();
    }

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public Reliability getReliability() {
        return Reliability.OrderedAndReliable;
    }

    public void process() {

    }

    @Override
    public String toString() {
        return "Message{" +
                "senderAddress=" + senderAddress +
                ", receiverAddress=" + receiverAddress +
                ", id=" + id +
                ", senderId=" + senderId +
                ", receiverId=" + receiverId +
                ", payload='" + payload + '\'' +
                '}';
    }

    public static Message clone(Message message) {
        try {
            Message clone = (Message)message.getClass().getConstructors()[0].newInstance();
            clone.id = message.id;
            clone.senderId = message.senderId;
            clone.receiverId = message.receiverId;
            clone.payload = new LinkedHashMap<>(message.payload);
            return clone;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
