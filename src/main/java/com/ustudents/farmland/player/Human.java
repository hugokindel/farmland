package com.ustudents.farmland.player;

import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.json.Json;
import com.ustudents.engine.core.json.JsonReader;
import com.ustudents.engine.core.json.JsonWriter;
import com.ustudents.engine.core.json.annotation.JsonSerializable;

import java.io.File;
import java.util.Map;

@JsonSerializable
public class Human extends Player{

    public Human(){}

    public Human(String name, String villageName) {
        super(name, villageName);
        int[] res = getNbTotalPlayer("human");
        Player.increaseTotalId(res[1]+1);
        super.setId(Player.getTotalId());
        adaptIdForPlayer("human");
    }

    @Override
    public void serializePlayer(Player current) {
        String path = Resources.getKindPlayerDirectoryName("human");
        Json.serialize(path+"/"+super.getUserName() + ".json",current);
    }

    public static Player deserializePlayer(String path, String userName){
        return Json.deserialize(path+"/"+userName + ".json",Human.class);
    }

}
