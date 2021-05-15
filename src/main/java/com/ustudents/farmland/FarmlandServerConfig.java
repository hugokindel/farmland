package com.ustudents.farmland;

import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.network.Server;
import com.ustudents.farmland.core.player.Bot;
import com.ustudents.farmland.core.system.Research;

import java.util.ArrayList;
import java.util.List;

@JsonSerializable
public class FarmlandServerConfig {
    @JsonSerializable(necessary = false)
    public String name = Resources.getLocalizedText("defaultServerName");

    // TODO: Put at 2
    @JsonSerializable(necessary = false)
    public Integer capacity = 1;

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

    @JsonSerializable(necessary = false)
    public List<String> admin = new ArrayList<>();

    @JsonSerializable(necessary = false)
    public String port = "8533";

    public FarmlandServerConfig() {

    }
}
