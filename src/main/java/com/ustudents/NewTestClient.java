package com.ustudents;

import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.network.Client;

public class NewTestClient {
    public static void main(String[] args) {
        Out.start(args, true, true);

        Client client = new Client();

        if (client.start()) {
            Out.println("Client started");

            client.stop();
        } else {
            Out.println("Server not started");
        }

        Out.end();
    }
}
