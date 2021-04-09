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
        Client client1 = new Client();

        server.start();
        client.start();
        client1.start();

        server.broadcast(new PrintMessage("Hello client!"));
        client.send(new PrintMessage("Hello server!"));
        client.send(new PrintMessage("Hello server1!"));

        while (received.get() < 3) {
            try {
                Thread.sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        client1.stop();
        client.stop();
        server.stop();

        Out.end();
    }
}
