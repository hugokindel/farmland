package com.ustudents.engine.network;

import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.core.json.Json;
import com.ustudents.engine.core.json.JsonReader;
import com.ustudents.engine.core.json.JsonWriter;
import com.ustudents.engine.network.messages.Message;
import com.ustudents.engine.network.messages.PackMessage;
import com.ustudents.engine.network.messages.AliveMessage;
import com.ustudents.engine.network.messages.ReceivedMessage;
import com.ustudents.engine.utility.Pair;

import java.io.ByteArrayInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@SuppressWarnings("unchecked")
public abstract class Controller {
    public enum Type {
        Server,
        Client
    }

    public static final String DEFAULT_ADDRESS = "82.65.148.99";

    public static final int DEFAULT_PORT = 8533;

    public static final int DEFAULT_MESSAGE_RECEIVER_SIZE = 65507;

    public static final int DEFAULT_MESSAGE_SENDER_SIZE = 65507;

    public static final int DEFAULT_MESSAGE_SEND_TIMEOUT = 1000;

    protected DatagramSocket socket;

    protected Thread receivingThread;

    protected Thread sendingThread;

    protected Thread reliabilityThread;

    protected AtomicBoolean connected = new AtomicBoolean(false);

    protected AtomicLong lastMessageId = new AtomicLong(-1L);

    protected List<Pair<Message, Integer>> reliableMessageIdsToSend = new CopyOnWriteArrayList<>();

    protected List<Long> reliableAndOrderedMessageIds = new CopyOnWriteArrayList<>();

    protected Queue<Pair<Message, Boolean>> messagesToSend = new ConcurrentLinkedDeque<>();

    protected List<byte[]> partsAvailable = new CopyOnWriteArrayList<>();

    protected Queue<PackMessage> packMessagesToComplete = new ConcurrentLinkedDeque<>();

    protected AtomicBoolean waitingForAnswer = new AtomicBoolean(false);

    protected Message answer;

    protected Class answerType;

    public boolean start() {
        return internalStart();
    }

    public void stop() {
        internalStop();
    }

    abstract public boolean receive(Message message);

    public void aliveStateChanged() {

    }

    abstract public Type getType();

    public void send(Message message) {
        send(message, false);
    }

    public void send(Message message, boolean force) {
        messagesToSend.add(new Pair<>(message, force));
    }

    public void respond(Message response, Message request) {
        response.setReceiverId(request.getSenderId());
        response.receiverAddress = request.senderAddress;
        send(response);
    }

    public <T extends Message> T request(Message message, Class<T> classType) {
        send(message);

        waitingForAnswer.set(true);
        answerType = classType;

        while (waitingForAnswer.get()) {
            try {
                Thread.sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Message response = answer;
        answer = null;
        answerType = null;
        return (T)response;
    }

    public boolean isConnected() {
        return connected.get();
    }

    protected Long getNewMessageId() {
        return lastMessageId.incrementAndGet();
    }

    private void createSocket() throws SocketException {
        if (getType() == Type.Server) {
            socket = new DatagramSocket(DEFAULT_PORT);
        } else {
            socket = new DatagramSocket();
        }

        socket.setSendBufferSize(DEFAULT_MESSAGE_SENDER_SIZE);
        socket.setReceiveBufferSize(DEFAULT_MESSAGE_RECEIVER_SIZE);
    }

    private boolean isSocketClosed() {
        return socket == null || socket.isClosed();
    }

    private boolean internalStart() {
        try {
            stop();
            createSocket();

            receivingThread = new Thread(new MessageReceiverRunnable());

            if (getType() == Type.Server) {
                receivingThread.setName("ServerNetworkReceiver");
            } else {
                receivingThread.setName("ClientNetworkReceiver");
            }

            receivingThread.start();

            sendingThread = new Thread(new MessageSenderRunnable());

            if (getType() == Type.Server) {
                sendingThread.setName("ServerNetworkSender");
            } else {
                sendingThread.setName("ClientNetworkSender");
            }

            sendingThread.start();

            reliabilityThread = new Thread(new ReliabilityCheckerRunnable());

            if (getType() == Type.Server) {
                reliabilityThread.setName("ServerNetworkReliabilityChecker");
            } else {
                reliabilityThread.setName("ClientNetworkReliabilityChecker");
            }

            reliabilityThread.start();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void internalStop() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }

            socket = null;

            if (receivingThread != null && receivingThread.isAlive()) {
                receivingThread.join(1000);
            }

            receivingThread = null;

            if (sendingThread != null && sendingThread.isAlive()) {
                sendingThread.join(1000);
            }

            sendingThread = null;

            if (reliabilityThread != null && reliabilityThread.isAlive()) {
                reliabilityThread.join(1000);
            }

            reliabilityThread = null;

            connected.set(false);
            lastMessageId.set(-1L);
            reliableAndOrderedMessageIds.clear();
            messagesToSend.clear();
            partsAvailable.clear();
            packMessagesToComplete.clear();
            waitingForAnswer.set(false);
            answer = null;
            answerType = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void internalReceive(DatagramPacket packet) {
        boolean isPart = false;
        Map<String, Object> json = null;

        try {
            json = JsonReader.readMapThatThrow(new ByteArrayInputStream(packet.getData()));
        } catch (Exception e) {
            isPart = true;
        }

        if (!isPart) {
            Long id = (Long)json.get("id");
            Integer senderId = json.get("senderId") == null ? -1 : ((Long)json.get("senderId")).intValue();
            Integer receiverId = json.get("receiverId") == null ? -1 : ((Long)json.get("receiverId")).intValue();
            String payload = (String)json.get("encodedPayload");
            String type = (String)json.get("type");

            try {
                Class classType = Class.forName(type);
                Message message = (Message)classType.getConstructors()[0].newInstance();

                message.setId(id);
                message.setSenderId(senderId);
                message.setReceiverId(receiverId);
                message.setEncodedPayload(payload);
                message.senderAddress = new InetSocketAddress(packet.getAddress(), packet.getPort());

                if (message instanceof PackMessage) {
                    if (getType() == Type.Server) {
                        Out.printlnInfo("Pack message incoming from client " + senderId);
                    } else {
                        Out.printlnInfo("pack message incoming from server");
                    }

                    packMessagesToComplete.add((PackMessage)message);
                } else if (message instanceof ReceivedMessage) {
                    if (getType() == Type.Server) {
                        Out.printlnInfo("Received message notification " + message.getId() + " (for " + message.getPayload().get("receivedId") + ") from client " + senderId);
                    } else {
                        Out.printlnInfo("Received message notification " + message.getId() + " (for " + message.getPayload().get("receivedId") + ") from server");
                    }

                    reliableMessageIdsToSend.remove(getMessageAwaitingForReturn(((ReceivedMessage)message).getReceivedId()));
                    reliableAndOrderedMessageIds.remove(((ReceivedMessage)message).getReceivedId());
                } else {
                    if (getType() == Type.Server) {
                        if (senderId == -1) {
                            Out.printlnInfo("Message " + message.getId() + " received from unknown client");
                        } else {
                            Out.printlnInfo("Message " + message.getId() + " received from client " + senderId);
                        }
                    } else {
                        Out.printlnInfo("Message " + message.getId() + " received from server");
                    }

                    if (message.getReliability() != Message.Reliability.Unreliable) {
                        Message backMessage = new ReceivedMessage(message.getId());
                        backMessage.receiverAddress = message.senderAddress;
                        backMessage.setReceiverId(message.getSenderId());
                        internalSend(backMessage, true, true);
                    }

                    if (receive(message)) {
                        message.process();
                    }

                    if (waitingForAnswer.get() && message.getClass() == answerType) {
                        answer = message;
                        waitingForAnswer.set(false);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (packMessagesToComplete.isEmpty()) {
                Out.printlnWarning("Received an invalid packet");
                return;
            }

            if (getType() == Type.Server) {
                Out.printlnInfo("Pack message's part " + (partsAvailable.size() + 1) + " received from client " + packMessagesToComplete.peek().getSenderId());
            } else {
                Out.printlnInfo("pack message's part " + (partsAvailable.size() + 1) + " received from server");
            }

            partsAvailable.add(Arrays.copyOf(packet.getData(), packet.getData().length));

            if (packMessagesToComplete.peek().getNumberOfParts() == partsAvailable.size()) {
                int length = 0;

                for (byte[] part : partsAvailable) {
                    length += part.length;
                }

                byte[] data = new byte[length - partsAvailable.size()];

                int i = 0;
                for (byte[] part : partsAvailable) {
                    for (int j = 1; j < part.length; j++) {
                        byte element = part[j];
                        data[i] = element;
                        i++;
                    }
                }

                partsAvailable.clear();

                try {
                    packMessagesToComplete.poll();

                    json = JsonReader.readMapThatThrow(new ByteArrayInputStream(data));

                    Long id = (Long)json.get("id");
                    Integer senderId = json.get("senderId") == null ? -1 : ((Long)json.get("senderId")).intValue();
                    Integer receiverId = json.get("receiverId") == null ? -1 : ((Long)json.get("receiverId")).intValue();
                    String payload = (String)json.get("encodedPayload");
                    String type = (String)json.get("type");

                    try {
                        Class classType = Class.forName(type);
                        Message message = (Message)classType.getConstructors()[0].newInstance();

                        message.setId(id);
                        message.setSenderId(senderId);
                        message.setReceiverId(receiverId);
                        message.setEncodedPayload(payload);
                        message.senderAddress = new InetSocketAddress(packet.getAddress(), packet.getPort());

                        if (getType() == Type.Server) {
                            if (senderId == -1) {
                                Out.printlnInfo("Message received from unknown client");
                            } else {
                                Out.printlnInfo("Message received from client " + senderId);
                            }
                        } else {
                            Out.printlnInfo("Message received from server");
                        }

                        if (message.getReliability() != Message.Reliability.Unreliable) {
                            Message backMessage = new ReceivedMessage(message.getId());
                            backMessage.receiverAddress = message.senderAddress;
                            backMessage.setReceiverId(message.getSenderId());
                            internalSend(backMessage, true, true);
                        }

                        if (receive(message)) {
                            message.process();
                        }

                        if (waitingForAnswer.get() && message.getClass() == answerType) {
                            answer = message;
                            waitingForAnswer.set(false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    Out.printlnWarning("One of the part of a pack must be invalid");
                }
            }
        }
    }

    private boolean internalSend(Message message, boolean setId, boolean forceSend) {
        if (!forceSend) {
            while (!reliableAndOrderedMessageIds.isEmpty()) {
                try {
                    Thread.sleep(10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (message.receiverAddress == null) {
            Out.println("No recipient");
            return false;
        }

        if (setId) {
            message.setId(getNewMessageId());
        }

        message.setEncodedPayload(JsonWriter.writeToString(message.getPayload()));
        message.setType(message.getClass().getName());
        byte[] data = Json.minify(Objects.requireNonNull(JsonWriter.writeToString(Json.serialize(message)))).getBytes(StandardCharsets.UTF_8);

        if (data.length > DEFAULT_MESSAGE_SENDER_SIZE) {
            int numberOfParts = (int)(Math.ceil((float)data.length / DEFAULT_MESSAGE_SENDER_SIZE));

            Message packMessage = new PackMessage(numberOfParts);
            packMessage.setReceiverId(message.getReceiverId());
            packMessage.setSenderId(message.getSenderId());
            packMessage.receiverAddress = message.receiverAddress;
            packMessage.senderAddress = message.senderAddress;
            internalSend(packMessage, true, false);

            byte[][] packedData = packData(data, DEFAULT_MESSAGE_SENDER_SIZE, numberOfParts);

            for (byte[] pack : packedData) {
                DatagramPacket packet = new DatagramPacket(pack, pack.length, message.receiverAddress);

                if (socket == null || socket.isClosed()) {
                    Out.printlnError("Socket already closed");
                    return false;
                }

                try {
                    socket.send(packet);
                    Thread.sleep(5);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }

            if ((message.getReliability() == Message.Reliability.Reliable || message.getReliability() == Message.Reliability.OrderedAndReliable) && getMessageAwaitingForReturn(message.getId()) == null) {
                reliableMessageIdsToSend.add(new Pair<>(message, 0));
            }

            if (message.getReliability() == Message.Reliability.OrderedAndReliable && !reliableAndOrderedMessageIds.contains(message.getId())) {
                reliableAndOrderedMessageIds.add(message.getId());
            }
        } else {
            DatagramPacket packet = new DatagramPacket(data, data.length, message.receiverAddress);

            if (socket == null || socket.isClosed()) {
                Out.printlnError("Socket already closed");
                return false;
            }

            try {
                socket.send(packet);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            if ((message.getReliability() == Message.Reliability.Reliable || message.getReliability() == Message.Reliability.OrderedAndReliable) && getMessageAwaitingForReturn(message.getId()) == null) {
                reliableMessageIdsToSend.add(new Pair<>(message, 0));
            }

            if (message.getReliability() == Message.Reliability.OrderedAndReliable && !reliableAndOrderedMessageIds.contains(message.getId())) {
                reliableAndOrderedMessageIds.add(message.getId());
            }

            if (message instanceof ReceivedMessage) {
                if (getType() == Type.Server) {
                    Out.printlnInfo("Message " + message.getId() + " (for " + message.getPayload().get("receivedId") + ") received notification sent from server");
                } else {
                    Out.printlnInfo("Message " + message.getId() + " (for " + message.getPayload().get("receivedId") + ") received notification sent from client");
                }
            } else {
                if (getType() == Type.Server) {
                    Out.printlnInfo("Message " + message.getId() + " sent from server");
                } else {
                    Out.printlnInfo("Message " + message.getId() + " sent from client");
                }
            }
        }

        return true;
    }

    private static byte[][] packData(byte[] data, int size, int numParts) {
        byte[][] ret = new byte[(int)Math.ceil((data.length + numParts) / (double)size)][size];

        int start = 0;

        for(int i = 0; i < ret.length; i++) {
            byte[] bytes = Arrays.copyOfRange(data, start, start + size - 1);
            ret[i] = new byte[bytes.length + 1];
            ret[i][0] = 'P';
            System.arraycopy(bytes, 0, ret[i], 1, bytes.length);
            start += size;
        }

        return ret;
    }

    private Pair<Message, Integer> getMessageAwaitingForReturn(Long id) {
        for (Pair<Message, Integer> element : reliableMessageIdsToSend) {
            if (element.getObject1().getId().equals(id)) {
                return element;
            }
        }

        return null;
    }

    protected class MessageSenderRunnable implements Runnable {
        @Override
        public void run() {
            final byte[] data = new byte[DEFAULT_MESSAGE_RECEIVER_SIZE];
            final DatagramPacket packet = new DatagramPacket(data, data.length);

            if (getType() == Type.Server) {
                Out.printlnInfo("Server sender thread started");
            } else {
                Out.printlnInfo("Client sender thread started");
            }

            while (!isSocketClosed()) {
                if (!messagesToSend.isEmpty()) {
                    Pair<Message, Boolean> message = messagesToSend.poll();
                    if (!internalSend(message.getObject1(), true, message.getObject2())) {
                        Out.printlnWarning("Error while sending packet.");
                    }
                }
            }

            if (getType() == Type.Server) {
                Out.printlnInfo("Server sender thread ended");
            } else {
                Out.printlnInfo("Client sender thread ended");
            }
        }
    }

    protected class MessageReceiverRunnable implements Runnable {
        @Override
        public void run() {
            final byte[] data = new byte[DEFAULT_MESSAGE_RECEIVER_SIZE];
            final DatagramPacket packet = new DatagramPacket(data, data.length);

            if (getType() == Type.Server) {
                Out.printlnInfo("Server receiver thread started");
            } else {
                Out.printlnInfo("Client receiver thread started");
            }

            while(true) {
                try {
                    if (isSocketClosed()) {
                        break;
                    }

                    socket.receive(packet);
                    internalReceive(packet);
                } catch (Exception e) {
                    if (!isSocketClosed()) {
                        Out.printlnWarning("Error while receiving packet.");
                    }
                }
            }

            if (getType() == Type.Server) {
                Out.printlnInfo("Server receiver thread ended");
            } else {
                Out.printlnInfo("Client receiver thread ended");
            }
        }
    }

    protected class ReliabilityCheckerRunnable implements Runnable {
        @Override
        public void run() {
            if (getType() == Type.Server) {
                Out.printlnInfo("Server reliability checker thread started");
            } else {
                Out.printlnInfo("Client reliability checker thread started");
            }

            while (!isSocketClosed()) {
                List<Pair<Message, Integer>> copy = new CopyOnWriteArrayList<>(reliableMessageIdsToSend);

                for (Pair<Message, Integer> element : copy) {
                    if (element.getObject2() >= DEFAULT_MESSAGE_SEND_TIMEOUT) {
                        if (element.getObject1() instanceof AliveMessage) {
                            aliveStateChanged();
                        } else {
                            if (getType() == Type.Server) {
                                Out.printlnInfo("Resending unreceived message " + element.getObject1().getId() + " from server");
                            } else {
                                Out.printlnInfo("Resending unreceived message " + element.getObject1().getId() + " from client");
                            }
                            internalSend(element.getObject1(), false, true);
                            element.setObject2(0);
                        }
                    } else {
                        element.setObject2(element.getObject2() + 10);
                    }
                }

                try {
                    Thread.sleep(10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (getType() == Type.Server) {
                Out.printlnInfo("Server reliability checker thread ended");
            } else {
                Out.printlnInfo("Client reliability checker thread ended");
            }
        }
    }
}
