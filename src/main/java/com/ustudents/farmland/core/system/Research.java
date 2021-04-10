package com.ustudents.farmland.core.system;

import com.ustudents.engine.core.json.annotation.JsonSerializable;

@JsonSerializable
public class Research {

    @JsonSerializable
    public String name;

    @JsonSerializable
    public Integer level;

    @JsonSerializable
    public Integer price;

    @JsonSerializable
    public Integer effect;

    public Research(){
        this("Sans nom");
    }

    public Research(String n){
        this(n,1,10,0);
    }

    public Research(String n, Integer l, Integer p, Integer e){
        name = n;
        level = l;
        price = p;
        effect = e;
    }

    public void levelUp(Integer p, Integer e){
        level += 1;
        price += p;
        effect += e;
    }

    public String getName(){
        return name;
    }

    public Integer getPrice() {
        return price;
    }

    public Integer getLevel() {
        return level;
    }

    public Integer getEffect() {
        return effect;
    }
}
