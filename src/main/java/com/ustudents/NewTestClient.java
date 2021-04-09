package com.ustudents;

import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.network.net3.Client;
import com.ustudents.engine.network.net3.Server;

public class NewTestClient {
    public static void main(String[] args) {
        Out.start(args, true, true);

        Client client = new Client();

        client.start();

        Out.println("client started");

        client.stop();

        Out.end();
    }
}
