package com.ustudents.farmland;

import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.network.Server;
import com.ustudents.farmland.core.player.Bot;

import java.util.ArrayList;
import java.util.List;

@JsonSerializable
public class FarmlandServerConfig {
    @JsonSerializable
    public String name;

    @JsonSerializable
    public Integer capacity;

    @JsonSerializable(necessary = false)
    public Integer numberOfBots = 0;

    @JsonSerializable(necessary = false)
    public Integer maximumLoanValue = 100;

    @JsonSerializable(necessary = false)
    public Integer debtRate = 3;

    @JsonSerializable(necessary = false)
    public Bot.Difficulty difficulty = Bot.Difficulty.Normal;

    @JsonSerializable(necessary = false)
    public String password = "";

    @JsonSerializable(necessary = false)
    public List<String> whitelist = new ArrayList<>();

    @JsonSerializable(necessary = false)
    public List<String> blacklist = new ArrayList<>();

    public FarmlandServerConfig() {

    }

    public FarmlandServerConfig(String name, Integer capacity) {
        this.name = name;
        this.capacity = capacity;
    }
}
