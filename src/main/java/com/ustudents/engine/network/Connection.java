package com.ustudents.engine.network;

import java.io.*;
import java.net.Socket;

public class Connection {
    public static final boolean DEFAULT_TCP_NO_DELAY = true;

    public static final boolean DEFAULT_AUTO_FLUSH = true;

    public Connection(Socket socket) {
        try {
            this.socket = socket;
            this.socket.setTcpNoDelay(DEFAULT_TCP_NO_DELAY);
            this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.writer = new PrintWriter(this.socket.getOutputStream(), DEFAULT_AUTO_FLUSH);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            this.socket.close();
            this.reader.close();
            this.writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isAlive() {
        return socket != null && !socket.isClosed() && socket.isConnected();
    }

    public Socket socket;

    public BufferedReader reader;

    public PrintWriter writer;
}
