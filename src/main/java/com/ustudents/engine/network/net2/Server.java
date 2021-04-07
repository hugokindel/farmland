package com.ustudents.engine.network.net2;

import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.network.net2.messages.ConnectMessage;
import com.ustudents.engine.network.net2.messages.Message;

import java.net.InetSocketAddress;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class Server extends Controller {
    public static int lastClientId = 0;

    public static Deque<Integer> freeIds = new ArrayDeque<>();

    protected Map<Integer, InetSocketAddress> clientsAddresses = new HashMap<>();

    @Override
    public boolean start() {
        if (super.start()) {
            connected.set(true);
        }

        if (!connected.get()) {
            Out.printlnError("Error while starting server");
            return false;
        }

        Out.printlnInfo("Server started");

        return true;
    }

    @Override
    public void stop() {
        super.stop();
    }

    @Override
    public void receive(Message message) {
        if (message instanceof ConnectMessage) {
            message.setSenderId(getFreeId());
            clientsAddresses.put(message.getSenderId(), message.senderAddress);
            Message answer = new ConnectMessage();
            answer.setReceiverId(message.getSenderId());
            send(message.getSenderId(), answer);
            Out.printlnInfo("Client " + message.getSenderId() + " connected");
        }
    }

    public void send(int clientId, Message message) {
        if (!clientsAddresses.containsKey(clientId)) {
            return;
        }

        message.setReceiverId(clientId);

        send(message);
    }

    @Override
    public void send(Message message) {
        Integer receiverId = message.getReceiverId();

        if (message.receiverAddress == null) {
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

    protected int getFreeId() {
        return freeIds.isEmpty() ? ++lastClientId : freeIds.pop();
    }
}
