package com.ustudents.farmland.core.system;

import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.json.annotation.JsonSerializable;

@JsonSerializable
public class Research {
    public enum Type {
        Undefined,
        Farmer,
        Breeder
    }

    @JsonSerializable
    public Type type;

    @JsonSerializable
    public Integer level;

    @JsonSerializable
    public Integer price;

    @JsonSerializable
    public Integer effect;

    public Research(){
        this(Type.Undefined);
    }

    public Research(Type t){
        this(t,1,10,0);
    }

    public Research(Type t, Integer l, Integer p, Integer e){
        type = t;
        level = l;
        price = p;
        effect = e;
    }

    public void levelUp(Integer p, Integer e){
        level += 1;
        price += p;
        effect += e;
    }

    public Type getType(){
        return type;
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

    public String getLocalizedName() {
        return Resources.getLocalizedText(type.name().toLowerCase());
    }
}
