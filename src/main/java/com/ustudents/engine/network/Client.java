package com.ustudents.engine.network;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.core.json.Json;
import com.ustudents.engine.core.json.JsonReader;
import com.ustudents.engine.core.json.JsonWriter;

import java.io.ByteArrayInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
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

    public static void send(Packet packet) {
        try {
            packet.data.put("from", clientId);
            byte[] data = Json.minify(Objects.requireNonNull(JsonWriter.writeToString(packet.data))).getBytes(StandardCharsets.UTF_8);
            int numberOfParts = data.length / Network.maximumPacketSize + 1;

            if (Game.isDebugging()) {
                Out.printlnDebug("Sending to " + packet.address + " the following data in " + numberOfParts + " part(s): " + packet.data);
            }

            if (numberOfParts > 1) {
                Map<String, Object> partsJson = new LinkedHashMap<>();
                partsJson.put("parts", numberOfParts);
                ByteBuffer buffer = ByteBuffer.allocate(Network.maximumPacketSize);
                buffer.put(Json.minify(Objects.requireNonNull(JsonWriter.writeToString(partsJson))).getBytes(StandardCharsets.UTF_8));
                DatagramPacket datagram = new DatagramPacket(buffer.array(), buffer.capacity(), packet.address, Network.port);
                socket.send(datagram);
                for (int i = 0; i < numberOfParts; i++) {
                    byte[] slice = Arrays.copyOfRange(data, i * Network.maximumPacketSize, (i + 1) * Network.maximumPacketSize);
                    datagram.setData(slice);
                    socket.send(datagram);
                }
            } else {
                ByteBuffer buffer = ByteBuffer.allocate(Network.maximumPacketSize);
                buffer.put(data);
                DatagramPacket datagram = new DatagramPacket(buffer.array(), buffer.capacity(), packet.address, Network.port);
                socket.send(datagram);
            }
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

            Packet received = new Packet(JsonReader.readMap(new ByteArrayInputStream(packet.getData())), packet.getAddress(), packet);
            int numberOfParts = 1;

            if (received.data.containsKey("parts")) {
                numberOfParts = ((Long)received.data.get("parts")).intValue();
                StringBuilder reconstitutedPacket = new StringBuilder();
                for (int i = 0; i < numberOfParts; i++) {
                    socket.receive(packet);
                    reconstitutedPacket.append(new String(packet.getData(), StandardCharsets.UTF_8));
                }
                received.data = JsonReader.readMap(new ByteArrayInputStream(reconstitutedPacket.toString().getBytes(StandardCharsets.UTF_8)));
            }

            if (Game.isDebugging()) {
                Out.printlnDebug("Received from " + received.address + " the following data in " + numberOfParts + " part(s): " + received.data);
            }

            return received;
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
        return isSocketBound() && clientId != -1;
    }
}
