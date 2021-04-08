package com.ustudents;

import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.network.Client;
import com.ustudents.engine.network.Server;
import com.ustudents.engine.network.messages.Message;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientServerTest {
    private static final AtomicInteger received = new AtomicInteger(0);

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

        client.blockUntilConnectedToServer();

        try {
            server.send(new PrintMessage(Files.readString(Path.of("test.txt"))));
            server.send(new PrintMessage("Hello Client!"));
            client.send(new PrintMessage("Hello Server!"));
            client.send(new PrintMessage(Files.readString(Path.of("test.txt"))));
            client.send(new PrintMessage("Hello Server!"));
            client.send(new PrintMessage(Files.readString(Path.of("test.txt"))));
            client.send(new PrintMessage("Hello 1!"));
            client.send(new PrintMessage("Hello 2!"));
            client.send(new PrintMessage("Hello 3!"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (received.get() < 9) {
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        client.stop();
        server.stop();

        Out.end();
    }
}
