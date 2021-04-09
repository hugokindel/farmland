package com.ustudents.engine.network.net3;

import com.ustudents.engine.network.messages.DisconnectMessage;
import com.ustudents.engine.network.messages.Message;
import com.ustudents.engine.utility.Pair;

import java.net.InetAddress;
import java.net.Socket;

public class Client extends Controller {
    private Connection connection;

    private Thread serverReader;

    @Override
    public void start() {
        try {
            Socket socket = new Socket(InetAddress.getByName(Controller.DEFAULT_ADDRESS), Controller.DEFAULT_PORT);
            connection = new Connection(socket);
        } catch (Exception e) {
            e.printStackTrace();
        }

        serverReader = runThread("ServerReader", new ServerReaderRunnable());

        super.start();
    }

    @Override
    public void stop() {
        try {
            request(new DisconnectMessage());
            connection.close();
            connection = null;
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
