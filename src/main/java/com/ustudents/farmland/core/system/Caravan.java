package com.ustudents.farmland.core.system;

import com.ustudents.engine.core.json.annotation.JsonSerializable;

@JsonSerializable
public class Caravan {

    @JsonSerializable
    public Integer reward;

    @JsonSerializable
    public Integer travelTurn;

    @JsonSerializable
    public Integer totalTurn;

    @JsonSerializable
    public String product;

    public Caravan(){
        this(0,"nothing");
    }

    public Caravan(Integer r, String p){
        this(r,0,4,p);
    }

    public Caravan(Integer r, Integer to, String p){
        this(r,0,to,p);
    }

    public Caravan(Integer r, Integer tr, Integer to, String p){
        reward = r;
        travelTurn = tr;
        totalTurn = to;
        product = p;
    }


    public Integer getTravelTurn() {
        return travelTurn;
    }

    public Integer getReward() {
        return reward;
    }

    public Integer getTotalTurn() {
        return totalTurn;
    }

    public String getProduct() {
        return product;
    }

    public void turnDone(){
        travelTurn += 1;
    }

    public Boolean hasArrived(){
        return travelTurn >= totalTurn;
    }

}
