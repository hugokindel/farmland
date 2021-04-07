package com.ustudents;

import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.network.net2.Client;

import java.util.concurrent.atomic.AtomicInteger;

public class ClientTest {
    private static final AtomicInteger received = new AtomicInteger(0);

    public static void main(String[] args) {
        Out.start(args, true, true);

        Client client = new Client();

        client.start();

        if (client.isServerAlive()) {
            client.blockUntilConnectedToServer();
        }

        client.stop();

        Out.end();
    }
}
