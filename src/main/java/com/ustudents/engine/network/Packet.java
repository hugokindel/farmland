package com.ustudents.engine.network;

import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Map;

public class Packet {
    public Map<String, Object> data;
    public InetAddress address;
    public DatagramPacket originalPacket;

    public Packet(Map<String, Object> data, InetAddress address, DatagramPacket originalPacket) {
        this.data = data;
        this.address = address;
        this.originalPacket = originalPacket;
    }
}
