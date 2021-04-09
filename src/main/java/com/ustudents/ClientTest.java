package com.ustudents;

import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.network.Client;

public class ClientTest {
    public static void main(String[] args) {
        Out.start(args, true, true);

        Client client = new Client();

        client.start();

        if (client.isServerAlive()) {
            client.blockUntilConnectedToServer();
        }

        client.send(new ClientServerTest.PrintMessage("Hello Server!"));

        try {
            Thread.sleep(25);
        } catch (Exception e) {
            e.printStackTrace();
        }

        client.stop();

        Out.end();
    }
}
