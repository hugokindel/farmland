package com.ustudents.engine.network;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.core.event.Event;
import com.ustudents.engine.core.event.EventDispatcher;
import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.network.messages.BroadcastMessage;
import com.ustudents.engine.network.messages.DisconnectMessage;
import com.ustudents.engine.network.messages.Message;
import com.ustudents.engine.utility.Pair;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Server extends Controller {
    public static class ClientDisconnected extends Event {
        public int clientId;

        public ClientDisconnected(int clientId) {
            this.clientId = clientId;
        }
    }

    private ServerSocket socket;

    private final Map<Integer, Connection> clients = new ConcurrentHashMap<>();

    private final Map<Integer, Thread> clientThreads = new ConcurrentHashMap<>();

    private final Queue<Integer> freeClientIds = new ConcurrentLinkedQueue<>();

    private final AtomicInteger lastClientId = new AtomicInteger(-1);

    private Thread connectorThread;

    private Thread cliInteractionThread;

    private final AtomicReference<EventDispatcher<ClientDisconnected>> clientDisconnected = new AtomicReference<>(new EventDispatcher<>());

    @Override
    public boolean start() {
        try {
            socket = new ServerSocket(Controller.DEFAULT_PORT);
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }

        connectorThread = runThread("Connector", new ConnectorRunnable());

        if (Game.get() != null && Game.get().getNetMode() == NetMode.DedicatedServer) {
            cliInteractionThread = runThread("CliInteraction", new CliInteractionRunnable());
        }

        return super.start();
    }

    @Override
    public void stop() {
        try {
            socket.close();

            connectorThread = stopThread(connectorThread);

            for (Map.Entry<Integer, Thread> clientThread : clientThreads.entrySet()) {
                stopThread(clientThread.getValue());
            }

            cliInteractionThread = stopThread(cliInteractionThread);

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
            if (Game.isDebugging()) {
                Out.printlnDebug("Send message to client " + message.getReceiverId());
            }
            super.send(message);
        }
    }

    public void send(int clientId, Message message) {
        message.setReceiverId(clientId);
        send(message);
    }

    public void broadcast(Message message) {
        if (Game.isDebugging()) {
            Out.printlnDebug("Message broadcasted");
        }
        super.send(new BroadcastMessage(new ArrayList<>(clients.keySet()), message));
    }

    public void respond(Message response, Message request) {
        send(request.getSenderId(), response);
    }

    @Override
    public boolean isAlive() {
        return socket != null && !socket.isClosed();
    }

    @Override
    public Type getType() {
        return Type.Server;
    }

    public EventDispatcher<ClientDisconnected> getClientDisconnectedDispatcher() {
        return clientDisconnected.get();
    }

    @Override
    protected Connection findConnectionToSendMessage(Message message) {
        if (clients.containsKey(message.getReceiverId())) {
            return clients.get(message.getReceiverId());
        }

        return null;
    }

    @Override
    protected void handleMessageIfNecessary(Message message) {
        if (message instanceof DisconnectMessage) {
            send(message.getSenderId(), new DisconnectMessage());
            clients.remove(message.getSenderId());
            freeClientIds.add(message.getSenderId());
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
                        Out.printlnDebug("New client connected with id " + client);
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
                        Out.printlnDebug("Data received from client " + clientId);
                    }

                    String data = connection.reader.readLine();
                    if (data == null) {
                        if (Game.isDebugging()) {
                            Out.printlnDebug("End of stream received from client " + clientId);
                        }

                        break;
                    }

                    if (Game.isDebugging()) {
                        Out.printlnDebug("Data received from client " + clientId + ": " + data);
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

    protected static class CliInteractionRunnable implements Runnable {
        @Override
        public void run() {
            Scanner scanner = new Scanner(System.in);

            Out.println("To stop the server, press 'quit'.");

            while (!Game.get().shouldQuit()) {
                if (scanner.hasNextLine()) {
                    String line = scanner.nextLine();

                    if (line.equals("quit")) {
                        Out.println("Quit command intercepted.");
                        Game.get().quit();
                        break;
                    }
                }
            }

            scanner.close();
        }
    }
}
