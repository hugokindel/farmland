package com.ustudents.engine.network;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.core.json.Json;
import com.ustudents.engine.core.json.JsonReader;
import com.ustudents.engine.core.json.JsonWriter;

import java.io.ByteArrayInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Objects;

public class ServerThread extends Thread {
    public static int nextClientId = 0;

    public static Deque<Integer> freeIds = new ArrayDeque<>();

    public static DatagramSocket socket;

    @Override
    public void run() {
        try {
            createSocket();

            Game.get().onServerStarted();
            Out.println("Server initialized.");

            while(socket.isBound())
            {
                Packet request = receive();
                if (request != null) {
                    Packet answer = handleRequest(request);
                    if (answer != null) {
                        send(answer);
                    }
                }
            }
        } catch (Exception e) {
            if (!e.getMessage().toLowerCase().contains("socket closed")) {
                e.printStackTrace();
            }
        }

        closeSocket();

        Game.get().onServerDestroyed();
        Out.println("Server destroyed.");
    }

    private static void createSocket() {
        try {
            socket = new DatagramSocket(Network.port);
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

    private static Packet receive() {
        try {
            byte[] buffer = new byte[Network.maximumPacketSize];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            Packet received = new Packet(JsonReader.readMap(new ByteArrayInputStream(packet.getData())), packet.getAddress(), packet);
            if (Game.isDebugging()) {
                Out.printlnDebug("Received from " + received.address + ", data: " + received.data);
            }
            return received;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // TODO: Pack big packets (in n packets of maximum packet size)
    private static void send(Packet packet) {
        try {
            packet.data.put("from", "server");
            ByteBuffer buffer = ByteBuffer.allocate(Network.maximumPacketSize);
            buffer.put(Json.minify(Objects.requireNonNull(JsonWriter.writeToString(packet.data))).getBytes(StandardCharsets.UTF_8));
            packet.originalPacket.setData(buffer.array());
            socket.send(packet.originalPacket);
            if (Game.isDebugging()) {
                Out.printlnDebug("Sending to " + packet.address + ", data: " + packet.data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Packet handleRequest(Packet packet) {
        Packet answer = new Packet(new LinkedHashMap<>(), packet.address, packet.originalPacket);

        if (packet.data.containsKey("command")) {
            answer.data.put("command", packet.data.get("command"));
        }

        if (packet.data.get("command").equals("exists")) {
            return answer;
        } else if (packet.data.get("command").equals("connect")) {
            answer.data.put("clientId", getFreeId());
            return answer;
        } else if (packet.data.get("command").equals("disconnect")) {
            freeIds.add(((Long)packet.data.get("from")).intValue());
            return answer;
        }

        return null;
    }

    private static int getFreeId() {
        return freeIds.isEmpty() ? nextClientId++ : freeIds.pop();
    }
}
