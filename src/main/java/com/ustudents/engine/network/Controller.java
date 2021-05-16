package com.ustudents.engine.network;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.core.json.Json;
import com.ustudents.engine.core.json.JsonReader;
import com.ustudents.engine.core.json.JsonWriter;
import com.ustudents.engine.network.messages.BroadcastMessage;
import com.ustudents.engine.network.messages.DisconnectMessage;
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

    public static final int DEFAULT_SO_TIMEOUT = 1000;

    protected final Queue<Pair<Integer, String>> messagesToRead = new ConcurrentLinkedQueue<>();

    protected final Queue<Message> messagesToSend = new ConcurrentLinkedQueue<>();

    protected final Queue<Message> messagesToHandleOnMainThread = new ConcurrentLinkedQueue<>();

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

    public Queue<Message> getMessagesToHandleOnMainThread() {
        return messagesToHandleOnMainThread;
    }

    public boolean hasMessagesToSend() {
        return !messagesToSend.isEmpty();
    }

    protected abstract Connection findConnectionToSendMessage(Message message);

    protected void handleMessageIfNecessary(Message message) {

    }

    protected void onDisconnect() {

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

        if (Game.isDebugging()) {
            Out.println("read: " + data);
        }

        assert json != null;

        try {
            return (Message)Json.deserialize(json, Class.forName((String)json.get("_type")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void internalStart() {
        readerThread = runThread("Reader", new ReaderRunnable());
        senderThread = runThread("Sender", new SenderRunnable());
    }

    private void internalStop() {
        messagesToRead.clear();
        messagesToSend.clear();
        messagesToHandleOnMainThread.clear();
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

                    if (data != null) {
                        Message message = readMessage(data.getObject2());
                        message.setSenderId(data.getObject1());
                        handleMessageIfNecessary(message);

                        if (message.getProcessingSide() == Message.ProcessingSide.Everywhere ||
                                (getType() == Type.Server && message.getProcessingSide() == Message.ProcessingSide.Server) ||
                                (getType() == Type.Client && message.getProcessingSide() == Message.ProcessingSide.Client)) {
                            if (message.shouldBeHandledOnMainThread()) {
                                messagesToHandleOnMainThread.add(message);
                            } else {
                                message.process();
                            }
                        }

                        if (mainThreadWaitingForResponse.get() && message.getClass() == mainThreadWaitingForResponseType.get()) {
                            if (Game.isDebugging()) {
                                Out.println("Response received");
                            }

                            mainThreadWaitingForResponse.set(false);
                            mainThreadWaitingForResponseType.set(null);
                            responseForMainThread.set(message);
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
    }

    protected class SenderRunnable implements Runnable {
        @Override
        public void run() {
            while (isAlive()) {
                if (!messagesToSend.isEmpty()) {
                    Message message = messagesToSend.poll();

                    if (message instanceof BroadcastMessage) {
                        if (getType() == Type.Client) {
                            Out.printlnError("A client cannot broadcast a message!");
                        } else {
                            BroadcastMessage broadcastMessage = (BroadcastMessage)message;

                            for (int clientId : broadcastMessage.receiverIds) {
                                broadcastMessage.messageToSend.setReceiverId(clientId);
                                send(broadcastMessage.messageToSend);
                            }
                        }
                    } else {
                        send(message);

                        if (message instanceof DisconnectMessage) {
                            onDisconnect();
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

        private void send(Message message) {
            Connection connection = findConnectionToSendMessage(message);

            if (connection != null) {
                message.setType(message.getClass().getName());
                String minifiedMessage = JsonWriter.writeToString(Json.serialize(message), false, false, false);

                if (Game.isDebugging()) {
                    Out.println("sent: " + minifiedMessage);
                }

                connection.writer.println(Objects.requireNonNull(minifiedMessage));

                if (Game.isDebugging()) {
                    if (getType() == Type.Server) {
                        Out.println("Message sent to client " + message.getReceiverId() + ": ");
                    } else {
                        Out.println("Message sent: " + minifiedMessage);
                    }
                }
            }
        }
    }
}
