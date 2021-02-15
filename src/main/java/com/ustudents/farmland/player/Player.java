package com.ustudents.farmland.player;

import com.ustudents.engine.core.json.annotation.JsonSerializable;

@JsonSerializable
public abstract class Player{

    @JsonSerializable
    private String userName;

    @JsonSerializable
    private String villageName;

    @JsonSerializable
    private int id;

    @JsonSerializable
    private static int totalId;

    private int currentMoney;

    private int currentActionPlayed;

    public Player(){}

    public Player(String name, String villageName){
        userName = name;
        this.villageName = villageName;
        totalId = id++;
        currentMoney = 0;
    }

    public static int getTotalId() {
        return totalId;
    }

    public int getId() {
        return id;
    }

    public String getVillageName() {
        return villageName;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public String toString() {
        return "Player{" +
                "userName='" + userName + '\'' +
                ", villageName='" + villageName + '\'' +
                ", id=" + id +
                '}';
    }

    public void setVillageName(String villageName) {
        this.villageName = villageName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public static void setTotalId() {
        Player.totalId -= 1;
    }

    public int getCurrentMoney() {
        return currentMoney;
    }

    public void setCurrentMoney(int currentMoney) {
        this.currentMoney = currentMoney;
    }

    public int getCurrentActionPlayed() {
        return currentActionPlayed;
    }

    public void setCurrentActionPlayed(int currentActionPlayed) {
        this.currentActionPlayed = currentActionPlayed;
    }

    public void increaseCurrentActionPlayed() {
        this.currentActionPlayed += 1;
    }

    abstract void serializePlayer(Player current);

}
