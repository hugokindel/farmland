package com.ustudents.engine.network.net2;

import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.network.net2.messages.ConnectMessage;
import com.ustudents.engine.network.net2.messages.Message;

import java.net.InetSocketAddress;

public class Client extends Controller {
    protected InetSocketAddress serverAddress = new InetSocketAddress(DEFAULT_ADDRESS, DEFAULT_PORT);

    protected int clientId = -1;

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

        send(new ConnectMessage());

        return true;
    }

    public void blockUntilConnectedToServer() {
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
        super.stop();
    }

    @Override
    public void receive(Message message) {
        if (message instanceof ConnectMessage) {
            connected.set(true);
            clientId = message.getReceiverId();
            Out.printlnInfo("Connected with id " + clientId);
        }
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

    @Override
    public Type getType() {
        return Type.Client;
    }
}
