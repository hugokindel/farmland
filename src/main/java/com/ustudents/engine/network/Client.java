package com.ustudents.engine.network;

import com.ustudents.engine.network.messages.DisconnectMessage;
import com.ustudents.engine.network.messages.Message;
import com.ustudents.engine.utility.Pair;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client extends Controller {
    private Connection connection;

    private Thread serverReader;

    public boolean start(String address, int port, int timeout) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(InetAddress.getByName(address), port), timeout);
            connection = new Connection(socket);
        } catch (Exception e) {
            if (!e.getMessage().toLowerCase().contains("connection refused") && !e.getMessage().toLowerCase().contains("connect timed out")) {
                e.printStackTrace();
            }

            return false;
        }

        serverReader = runThread("ServerReader", new ServerReaderRunnable());

        return super.start();
    }

    public boolean start(String address, int port) {
        return start(address, port, DEFAULT_SO_TIMEOUT);
    }

    public boolean start(int timeout) {
        return start(DEFAULT_ADDRESS, DEFAULT_PORT, timeout);
    }

    @Override
    public boolean start() {
        return start(DEFAULT_SO_TIMEOUT);
    }

    @Override
    public void stop() {
        try {
            send(new DisconnectMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        serverReader = stopThread(serverReader);

        super.stop();
    }

    @Override
    public boolean isAlive() {
        return connection != null && connection.isAlive();
    }

    @Override
    public Type getType() {
        return Type.Client;
    }

    @Override
    protected Connection findConnectionToSendMessage(Message message) {
        return connection;
    }

    @Override
    protected void onDisconnect() {
        connection.close();
        connection = null;
    }

    private class ServerReaderRunnable implements Runnable {
        public ServerReaderRunnable() {

        }

        @Override
        public void run() {
            try {
                while (isAlive()) {
                    String data = connection.reader.readLine();
                    messagesToRead.add(new Pair<>(0, data));
                }
            } catch (Exception e) {
                if (isAlive()) {
                    e.printStackTrace();
                }
            }
        }
    }
}
