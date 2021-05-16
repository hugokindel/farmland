package com.ustudents.farmland;

import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.network.Server;
import com.ustudents.farmland.core.player.Bot;
import com.ustudents.farmland.core.system.Research;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

@JsonSerializable
public class FarmlandServerConfig {
    @JsonSerializable(necessary = false)
    public String name = Resources.getLocalizedText("defaultServerName");

    @JsonSerializable(necessary = false)
    public Integer port = 8533;

    // TODO: Put at 2
    @JsonSerializable(necessary = false)
    public Integer capacity = 2;

    @JsonSerializable(necessary = false)
    public Vector2i mapSize = new Vector2i(16, 16);

    @JsonSerializable(necessary = false)
    public Long seed = System.currentTimeMillis();

    @JsonSerializable(necessary = false)
    public Integer numberOfBots = 0;

    @JsonSerializable(necessary = false)
    public Integer maximumLoanValue = 100;

    @JsonSerializable(necessary = false)
    public Integer debtRate = 3;

    @JsonSerializable(necessary = false)
    public Bot.Difficulty difficulty = Bot.Difficulty.Normal;

    //@JsonSerializable(necessary = false)
    //public String password = "";

    //@JsonSerializable(necessary = false)
    //public List<String> whitelist = new ArrayList<>();

    //@JsonSerializable(necessary = false)
    //public List<String> blacklist = new ArrayList<>();

    //@JsonSerializable(necessary = false)
    //public List<String> admin = new ArrayList<>();

    public FarmlandServerConfig() {

    }
}
