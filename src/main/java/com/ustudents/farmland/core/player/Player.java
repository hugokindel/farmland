package com.ustudents.farmland.core.player;

import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.json.JsonReader;
import com.ustudents.engine.core.json.JsonWriter;
import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.graphic.Color;

import java.io.File;
import java.util.Map;

@JsonSerializable
public abstract class Player{

    @JsonSerializable
    private String userName;

    @JsonSerializable
    private String villageName;

    @JsonSerializable
    private int id;

    @JsonSerializable
    private static int lastId;

    private int currentMoney;

    private Color color;

    private int currentActionPlayed;

    public Player(){}

    public Player(String name, String villageName){
        userName = name;
        this.villageName = villageName;
        lastId = id++;
        currentMoney = 0;
    }

    public static int getLastId() {
        return lastId;
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

    public void setId(int id){
        this.id = id;
    }

    public void setVillageName(String villageName) {
        this.villageName = villageName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public static void increaseTotalId(int add) {
        Player.lastId += add;
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

    protected void adaptIdForPlayer(String type){
        File humanPlayer = new File(Resources.getKindPlayerDirectoryName(type));
        if(humanPlayer.exists()){
            File[] list = humanPlayer.listFiles();
            if(list != null){
                for (File player : list){
                    Map<String,Object> json = JsonReader.readMap(player.getPath());
                    if(json != null){
                        json.put("totalId",Player.getLastId());
                        JsonWriter.writeToFile(player.getPath(),json);
                    }
                }
            }
        }
    }

    protected static int[] getNbTotalPlayer(String type){
        File humanPlayer = new File(Resources.getKindPlayerDirectoryName(type));
        int[] res = new int[2];
        if(humanPlayer.exists()){
            File[] list = humanPlayer.listFiles();
            if (list!=null){
                for (File player : list){
                    res[0]++;
                    Map<String,Object> json = JsonReader.readMap(player.getPath());
                    if(json != null && (int) json.get("totalId") > res[1]){
                        res[1] = (int) json.get("totalId");
                    }
                }
            }
        }
        return res;
    }

}
