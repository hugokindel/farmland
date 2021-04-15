package com.ustudents.farmland.core;

import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.network.Server;

import java.util.ArrayList;
import java.util.List;

@JsonSerializable
public class ServerConfig {
    @JsonSerializable
    public String name;

    @JsonSerializable
    public Integer capacity;

    @JsonSerializable(necessary = false)
    public Integer numberOfBots = 0;

    @JsonSerializable(necessary = false)
    public String password = "";

    @JsonSerializable(necessary = false)
    public List<String> whitelist = new ArrayList<>();

    @JsonSerializable(necessary = false)
    public List<String> blacklist = new ArrayList<>();

    public ServerConfig() {

    }

    public ServerConfig(String name, Integer capacity) {
        this.name = name;
        this.capacity = capacity;
    }
}
