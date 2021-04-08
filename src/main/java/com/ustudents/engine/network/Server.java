package com.ustudents.engine.network;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.core.event.Event;
import com.ustudents.engine.core.event.EventDispatcher;
import com.ustudents.engine.network.messages.AliveMessage;
import com.ustudents.engine.network.messages.DisconnectMessage;
import com.ustudents.engine.network.messages.Message;
import com.ustudents.engine.network.messages.ConnectMessage;
import org.joml.Vector2i;

import java.net.InetSocketAddress;
import java.util.*;

public class Server extends Controller {
    public static class ClientDisconnected extends Event {
        public int clientId;

        public ClientDisconnected(int clientId) {
            this.clientId = clientId;
        }
    }

    public static int lastClientId = 0;

    public static Deque<Integer> freeIds = new ArrayDeque<>();

    protected Map<Integer, InetSocketAddress> clientsAddresses = new HashMap<>();

    protected Thread cliInteractionThread;

    public EventDispatcher<ClientDisconnected> onClientDisconnected = new EventDispatcher<>();

    @Override
    public boolean start() {
        return start(false);
    }

    public boolean start(boolean dedicated) {
        if (super.start()) {
            connected.set(true);
        }

        if (!connected.get()) {
            Out.printlnError("Error while starting server");
            return false;
        }

        if (Game.get() != null && Game.get().getNetMode() == NetMode.DedicatedServer) {
            cliInteractionThread = new Thread(new CliInteractionRunnable());
            cliInteractionThread.setName("ServerCliInteraction");
            cliInteractionThread.start();
        }

        Out.printlnInfo("Server started");

        return true;
    }

    @Override
    public void stop() {
        if (Game.get() != null && Game.get().getNetMode() == NetMode.DedicatedServer) {
            try {
                if (cliInteractionThread != null && cliInteractionThread.isAlive()) {
                    cliInteractionThread.join(1000);
                }

                cliInteractionThread = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        super.stop();
    }

    @Override
    public boolean receive(Message message) {
        if (message instanceof ConnectMessage) {
            message.setSenderId(getFreeId());
            clientsAddresses.put(message.getSenderId(), message.senderAddress);
            respond(new ConnectMessage(), message);
            Out.printlnInfo("Client " + message.getSenderId() + " connected");
        } else if (message instanceof AliveMessage) {
            respond(new AliveMessage(), message);
            return false;
        } else if (message instanceof DisconnectMessage) {
            freeIds.add(message.getSenderId());
            clientsAddresses.remove(message.getSenderId());
            Out.printlnInfo("Client " + message.getSenderId() + " disconnected");
            onClientDisconnected.dispatch(new ClientDisconnected(message.getSenderId()));
        }

        return true;
    }

    public void send(int clientId, Message message) {
        if (!clientsAddresses.containsKey(clientId)) {
            Out.printlnError("Cannot find clientId");
            return;
        }

        message.receiverAddress = clientsAddresses.get(clientId);
        message.setReceiverId(clientId);

        send(message);
    }

    public void send(InetSocketAddress address, Message message) {
        message.receiverAddress = address;

        send(message);
    }

    @Override
    public void send(Message message) {
        if (message.receiverAddress == null) {
            Integer receiverId = message.getReceiverId();

            if (clientsAddresses.size() == 1) {
                receiverId = clientsAddresses.entrySet().stream().findFirst().get().getKey();
            } else if (receiverId == -1 || !clientsAddresses.containsKey(receiverId)) {
                Out.printlnWarning("No receiver id");
                return;
            }

            message.receiverAddress = clientsAddresses.get(receiverId);
        }

        super.send(message);
    }

    public void broadcast(Message message) {
        for (Map.Entry<Integer, InetSocketAddress> address : clientsAddresses.entrySet()) {
            send(address.getKey(), message);
        }
    }

    @Override
    public Type getType() {
        return Type.Server;
    }

    public int getNumberOfClients() {
        return clientsAddresses.size();
    }

    protected int getFreeId() {
        return freeIds.isEmpty() ? ++lastClientId : freeIds.pop();
    }

    protected class CliInteractionRunnable implements Runnable {
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
