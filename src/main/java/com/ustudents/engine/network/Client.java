package com.ustudents.engine.network;

import com.ustudents.engine.core.json.Json;
import com.ustudents.engine.core.json.JsonReader;
import com.ustudents.engine.core.json.JsonWriter;

import java.io.ByteArrayInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class Client {
    public static int clientId = -1;

    public static DatagramSocket socket;

    public static boolean isSocketBound() {
        return socket != null && socket.isBound();
    }

    public static void createSocket() {
        try {
            socket = new DatagramSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeSocket() {
        try {
            if (socket.isBound()) {
                socket.close();
                socket = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean commandExists() {
        return request("exists", 100) != null;
    }

    public static void commandConnect() {
        Map<String, Object> answer = request("connect");
        clientId = ((Long)answer.get("clientId")).intValue();
    }

    public static void commandDisconnect() {
        request("disconnect");
    }

    // TODO: Pack big packets (in n packets of maximum packet size)
    public static void send(Packet packet) {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(Network.maximumPacketSize);
            buffer.put(Json.minify(Objects.requireNonNull(JsonWriter.writeToString(packet.data))).getBytes(StandardCharsets.UTF_8));
            DatagramPacket data = new DatagramPacket(buffer.array(), buffer.capacity(), packet.address, Network.port);
            socket.send(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Packet receive() {
        return receive(0);
    }

    public static Packet receive(int timeout) {
        try {
            byte[] buffer = new byte[Network.maximumPacketSize];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            if (timeout > 0) {
                socket.setSoTimeout(timeout);
            }
            socket.receive(packet);
            if (timeout > 0) {
                socket.setSoTimeout(0);
            }
            return new Packet(JsonReader.readMap(new ByteArrayInputStream(packet.getData())), packet.getAddress(), packet);
        } catch (Exception e) {
            if (!e.getMessage().contains("timed out")) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static Map<String, Object> request(Map<String, Object> json) {
        return request(json, 0);
    }

    public static Map<String, Object> request(Map<String, Object> json, int timeout) {
        if (!isSocketBound()) {
            createSocket();
        }

        try {
            json.put("from", clientId);
            Packet request = new Packet(json, InetAddress.getByName("localhost"), null);
            send(request);
            Packet answer = receive(timeout);
            if (answer != null) {
                return answer.data;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Map<String, Object> request(String commandName) {
        return request(commandName, 0);
    }

    public static Map<String, Object> request(String commandName, int timeout) {
        Map<String, Object> json = new LinkedHashMap<>();
        json.put("command", commandName);
        return request(json, timeout);
    }

    public static boolean isConnectedToServer() {
        return clientId != -1;
    }
}
