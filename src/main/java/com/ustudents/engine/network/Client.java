package com.ustudents.engine.network;

import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.network.messages.AliveMessage;
import com.ustudents.engine.network.messages.ConnectMessage;
import com.ustudents.engine.network.messages.DisconnectMessage;
import com.ustudents.engine.network.messages.Message;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client extends Controller {
    protected InetSocketAddress serverAddress = new InetSocketAddress(DEFAULT_ADDRESS, DEFAULT_PORT);

    protected int clientId;

    protected AtomicBoolean searchingForServer = new AtomicBoolean(false);

    protected AtomicBoolean serverFound = new AtomicBoolean(false);

    public Client() {

    }

    public Client(String address, int port) {
        serverAddress = new InetSocketAddress(address, port);
    }

    @Override
    public boolean start() {
        if (!super.start()) {
            Out.printlnError("Error while starting client");
            return false;
        }

        Out.printlnInfo("Client started");

        return true;
    }

    public void blockUntilConnectedToServer() {
        send(new ConnectMessage());

        while (!connected.get()) {
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stop() {
        if (connected.get()) {
            disconnect();

            clientId = -1;
            searchingForServer.set(false);
            serverFound.set(false);

            try {
                Thread.sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        super.stop();
    }

    @Override
    public boolean receive(Message message) {
        if (message instanceof ConnectMessage) {
            connected.set(true);
            clientId = message.getReceiverId();
            Out.printlnInfo("Connected with id " + clientId);
        } else if (message instanceof AliveMessage) {
            searchingForServer.set(false);
            serverFound.set(true);
        }

        return true;
    }

    @Override
    public void send(Message message) {
        if (message.receiverAddress == null) {
            message.receiverAddress = serverAddress;
        }

        message.setReceiverId(0);
        message.setSenderId(clientId);

        super.send(message);
    }

    public void disconnect() {
        if (connected.get()) {
            send(new DisconnectMessage());
            clientId = -1;
            connected.set(false);
            Out.printlnInfo("Disconnected");
        }
    }

    public boolean isServerAlive() {
        serverFound.set(false);
        searchingForServer.set(true);

        send(new AliveMessage());

        int waitTime = 0;

        while (searchingForServer.get()) {
            try {
                if (waitTime >= 100) {
                    searchingForServer.set(false);
                    break;
                }

                Thread.sleep(10);
                waitTime += 10;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return serverFound.get();
    }

    @Override
    public Type getType() {
        return Type.Client;
    }

    @Override
    public void aliveStateChanged() {
        searchingForServer.set(false);
    }

    public int getClientId() {
        return clientId;
    }
}
