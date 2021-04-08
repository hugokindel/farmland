package com.ustudents;

import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.network.Server;

import java.util.concurrent.atomic.AtomicInteger;

public class ServerTest {
    private static final AtomicInteger received = new AtomicInteger(0);

    public static void main(String[] args) {
        Out.start(args, true, true);

        Server server = new Server();

        server.start();

        while (server.getNumberOfClients() < 1) {
            try {
                Thread.sleep(25);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        while (server.getNumberOfClients() >= 1) {
            try {
                Thread.sleep(25);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        server.stop();

        Out.end();
    }
}
