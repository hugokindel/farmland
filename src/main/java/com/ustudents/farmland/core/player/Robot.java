package com.ustudents.farmland.core.player;

import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.json.Json;
import com.ustudents.engine.core.json.annotation.JsonSerializable;

@JsonSerializable
public class Robot extends Player{

    public Robot(){}

    public Robot(String name, String villageName) {
        super(name, villageName);
    }

    @Override
    void serializePlayer(Player current) {
        String path = Resources.getKindPlayerDirectoryName("robot");
        Json.serialize(path+"/"+super.getUserName(),current);
    }

    public static Player deserializePlayer(String path, String userName){
        return Json.deserialize(path+"/"+userName,Robot.class);
    }
}
