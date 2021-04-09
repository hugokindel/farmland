package com.ustudents;

import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.network.Server;

public class NewTestServer {
    public static void main(String[] args) {
        Out.start(args, true, true);

        Server server = new Server();

        server.start();

        Out.println("server started");

        try {
            Thread.sleep(100000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        server.stop();

        Out.end();
    }
}
