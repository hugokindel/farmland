package com.ustudents.engine.network;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Map;

public class Packet {
    public Map<String, Object> data;
    public InetAddress address;
    public DatagramPacket datagram;

    public Packet(Map<String, Object> data, InetAddress address, DatagramPacket datagram) {
        this.data = data;
        this.address = address;
        this.datagram = datagram;
    }
}
