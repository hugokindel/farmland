package com.ustudents;

import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.network.Client;
import com.ustudents.engine.network.Server;
import com.ustudents.engine.network.messages.Message;

import java.util.concurrent.atomic.AtomicInteger;

public class NewTestClientServer {
    public static final AtomicInteger received = new AtomicInteger(0);

    public static class PrintMessage extends Message {
        public PrintMessage() {

        }

        public PrintMessage(String message) {
            getPayload().put("message", message);
        }

        @Override
        public void process() {
            Out.println(getPayload().getOrDefault("message", "Empty message"));
            received.incrementAndGet();
        }
    }

    public static void main(String[] args) {
        Out.start(args, true, true);

        Server server = new Server();
        Client client = new Client();

        server.start();
        client.start();

        server.broadcast(new PrintMessage("Hello client!"));
        client.send(new PrintMessage("Hello server!"));

        while (received.get() < 2) {
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
