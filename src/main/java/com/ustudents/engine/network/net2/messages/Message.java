package com.ustudents.engine.network.net2.messages;

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
    protected String encodedPayload;

    @JsonSerializable
    protected String type;

    private Map<String, Object> payload;

    public Message() {
        payload = new LinkedHashMap<>();
        encodedPayload = "";
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

    public void setEncodedPayload(String encodedPayload) {
        this.encodedPayload = encodedPayload;
    }

    public int getEncodedPayloadLength() {
        return encodedPayload.length();
    }

    public String getEncodedPayload() {
        return encodedPayload;
    }

    public Map<String, Object> getPayload() {
        if (payload.isEmpty() && !encodedPayload.isEmpty()) {
            payload = JsonReader.readMap(new ByteArrayInputStream(encodedPayload.getBytes(StandardCharsets.UTF_8)));
        }

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
                ", payload='" + encodedPayload + '\'' +
                ", decodedPayload=" + payload +
                '}';
    }
}
