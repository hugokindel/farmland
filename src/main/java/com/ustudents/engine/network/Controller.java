package com.ustudents.engine.network;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.core.json.Json;
import com.ustudents.engine.core.json.JsonReader;
import com.ustudents.engine.core.json.JsonWriter;
import com.ustudents.engine.network.messages.Message;
import com.ustudents.engine.utility.Pair;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("unchecked")
public abstract class Controller {
    public enum Type {
        Server,
        Client
    }

    public static final String DEFAULT_ADDRESS = "127.0.0.1";

    public static final int DEFAULT_PORT = 8533;

    protected final Queue<Pair<Integer, String>> messagesToRead = new ConcurrentLinkedQueue<>();

    protected final Queue<Message> messagesToSend = new ConcurrentLinkedQueue<>();

    protected final AtomicBoolean mainThreadWaitingForResponse = new AtomicBoolean(false);

    protected final AtomicReference<Class> mainThreadWaitingForResponseType = new AtomicReference<>(null);

    protected final AtomicReference<Message> responseForMainThread = new AtomicReference<>();

    protected Thread readerThread;

    protected Thread senderThread;

    public boolean start() {
        internalStart();

        return true;
    }

    public void stop() {
        internalStop();
    }

    public void send(Message message) {
        messagesToSend.add(message);
    }

    public <T extends Message> T request(T request) {
        return (T)request(request, request.getClass());
    }

    public <T extends Message> T request(Message request, Class<T> type) {
        mainThreadWaitingForResponse.set(true);
        mainThreadWaitingForResponseType.set(type);

        send(request);

        while (mainThreadWaitingForResponse.get()) {
            try {
                Thread.sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Message response = responseForMainThread.get();
        responseForMainThread.set(null);

        return (T)response;
    }

    public abstract boolean isAlive();

    public abstract Type getType();

    protected abstract Connection findConnectionToSendMessage(Message message);

    protected void handleMessageIfNecessary(Message message) {

    }

    protected  Thread runThread(String name, Runnable runnable) {
        Thread thread = new Thread(runnable);

        if (getType() == Type.Server) {
            thread.setName("Server" + name);
        } else {
            thread.setName("Client" + name);
        }

        thread.start();

        return thread;
    }

    protected Thread stopThread(Thread thread) {
        try {
            if (thread != null && thread.isAlive()) {
                thread.join(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    protected Message readMessage(String data) {
        Map<String, Object> json = JsonReader.readMap(new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8)));

        Long id = (Long)json.get("id");
        Integer senderId = json.get("senderId") == null ? -1 : ((Long)json.get("senderId")).intValue();
        Integer receiverId = json.get("receiverId") == null ? -1 : ((Long)json.get("receiverId")).intValue();
        Map<String, Object> payload = (Map<String, Object>)json.get("payload");
        String type = (String)json.get("type");

        try {
            Class classType = Class.forName(type);

            Message message = (Message)classType.getConstructors()[0].newInstance();
            message.setId(id);
            message.setSenderId(senderId);
            message.setReceiverId(receiverId);
            message.setPayload(payload);

            return message;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void internalStart() {
        readerThread = runThread("Reader", new Server.ReaderRunnable());
        senderThread = runThread("Sender", new Server.SenderRunnable());
    }

    private void internalStop() {
        messagesToRead.clear();
        messagesToSend.clear();
        mainThreadWaitingForResponse.set(false);
        mainThreadWaitingForResponseType.set(null);
        responseForMainThread.set(null);
    }

    protected class ReaderRunnable implements Runnable {
        @Override
        public void run() {
            while (isAlive()) {
                if (!messagesToRead.isEmpty()) {
                    if (Game.isDebugging()) {
                        Out.println("Message received");
                    }

                    Pair<Integer, String> data = messagesToRead.poll();
                    assert data != null;
                    Message message = readMessage(data.getObject2());
                    message.setSenderId(data.getObject1());
                    handleMessageIfNecessary(message);
                    message.process();

                    if (mainThreadWaitingForResponse.get() && message.getClass() == mainThreadWaitingForResponseType.get()) {
                        if (Game.isDebugging()) {
                            Out.println("Response received");
                        }

                        mainThreadWaitingForResponse.set(false);
                        mainThreadWaitingForResponseType.set(null);
                        responseForMainThread.set(message);
                    }
                } else {
                    try {
                        Thread.sleep(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    protected class SenderRunnable implements Runnable {
        @Override
        public void run() {
            while (isAlive()) {
                if (!messagesToSend.isEmpty()) {
                    Message message = messagesToSend.poll();
                    message.setType(message.getClass().getName());
                    Connection connection = findConnectionToSendMessage(message);
                    String minifiedMessage = Json.minify(JsonWriter.writeToString(Json.serialize(message), false, false, false));
                    connection.writer.println(Objects.requireNonNull(minifiedMessage));

                    if (Game.isDebugging()) {
                        if (getType() == Type.Server) {
                            Out.println("Message sent to client " + message.getReceiverId() + ": ");
                        } else {
                            Out.println("Message sent: " + minifiedMessage);
                        }
                    }
                } else {
                    try {
                        Thread.sleep(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
