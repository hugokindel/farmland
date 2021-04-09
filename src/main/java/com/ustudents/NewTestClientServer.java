package com.ustudents;

import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.network.net3.Client;
import com.ustudents.engine.network.net3.Server;

public class NewTestClientServer {
    public static void main(String[] args) {
        Out.start(args, true, true);

        Server server = new Server();
        Client client = new Client();

        server.start();
        client.start();

        server.broadcast(new ClientServerTest.PrintMessage("Hello client!"));
        client.send(new ClientServerTest.PrintMessage("Hello server!"));

        while (ClientServerTest.received.get() < 2) {
            try {
                Thread.sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        client.stop();
        server.stop();

        Out.end();
    }
}
