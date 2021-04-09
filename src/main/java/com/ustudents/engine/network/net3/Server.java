package com.ustudents.engine.network.net3;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.network.messages.DisconnectMessage;
import com.ustudents.engine.network.messages.Message;
import com.ustudents.engine.utility.Pair;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Server extends Controller {
    private ServerSocket socket;

    private final Map<Integer, Connection> clients = new ConcurrentHashMap<>();

    private final Map<Integer, Thread> clientThreads = new ConcurrentHashMap<>();

    private final Queue<Integer> freeClientIds = new ConcurrentLinkedQueue<>();

    private final AtomicInteger lastClientId = new AtomicInteger(-1);

    private Thread connectorThread;

    @Override
    public void start() {
        try {
            socket = new ServerSocket(Controller.DEFAULT_PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        connectorThread = runThread("Connector", new ConnectorRunnable());

        super.start();
    }

    @Override
    public void stop() {
        try {
            socket.close();

            connectorThread = stopThread(connectorThread);

            for (Map.Entry<Integer, Thread> clientThread : clientThreads.entrySet()) {
                stopThread(clientThread.getValue());
            }

            clients.clear();
            clientThreads.clear();
            freeClientIds.clear();
            lastClientId.set(-1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.stop();
    }

    @Override
    public void send(Message message) {
        if (message.getReceiverId() == -1) {
            Out.printlnWarning("Can't send message, missing receiver id.");
        } else {
            super.send(message);
        }
    }

    public void send(int clientId, Message message) {
        message.setReceiverId(clientId);
        send(message);
    }

    public void broadcast(Message message) {
        for (Map.Entry<Integer, Connection> client : clients.entrySet()) {
            send(client.getKey(), message);
        }
    }

    @Override
    public boolean isAlive() {
        return socket != null && !socket.isClosed();
    }

    @Override
    public Type getType() {
        return Type.Server;
    }

    @Override
    protected Connection findConnectionToSendMessage(Message message) {
        return clients.get(message.getReceiverId());
    }

    @Override
    protected void handleMessageIfNecessary(Message message) {
        if (message instanceof DisconnectMessage) {

            send(message.getSenderId(), new DisconnectMessage());
        }
    }

    private int getAvailableClientId() {
        return !freeClientIds.isEmpty() ? freeClientIds.poll() : lastClientId.incrementAndGet();
    }

    private class ConnectorRunnable implements Runnable {
        @Override
        public void run() {
            try {
                while (isAlive()) {
                    Socket client = socket.accept();
                    Connection connection = new Connection(client);
                    int clientId = getAvailableClientId();
                    clients.put(clientId, connection);
                    clientThreads.put(clientId, runThread("ClientReader" + clientId, new ClientReaderRunnable(clientId)));

                    if (Game.isDebugging()) {
                        Out.println("New client connected with id " + client);
                    }
                }
            } catch (Exception e) {
                if (isAlive()) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class ClientReaderRunnable implements Runnable {
        private final int clientId;

        public ClientReaderRunnable(int clientId) {
            this.clientId = clientId;
        }

        @Override
        public void run() {
            try {
                Connection connection = clients.get(clientId);

                while (isAlive() && clients.containsKey(clientId) && connection.isAlive()) {
                    if (Game.isDebugging()) {
                        Out.println("Data received from client " + clientId);
                    }

                    String data = connection.reader.readLine();
                    if (data == null) {
                        if (Game.isDebugging()) {
                            Out.println("End of stream received from client " + clientId);
                        }

                        break;
                    }

                    if (Game.isDebugging()) {
                        Out.println("Data received from client " + clientId + ": " + data);
                    }

                    messagesToRead.add(new Pair<>(clientId, data));
                }
            } catch (Exception e) {
                if (isAlive()) {
                    e.printStackTrace();
                }
            }

            clientThreads.remove(clientId);
        }
    }
}
