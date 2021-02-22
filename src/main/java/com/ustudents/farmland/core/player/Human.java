package com.ustudents.farmland.core.player;

import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.json.Json;
import com.ustudents.engine.core.json.annotation.JsonSerializable;

@JsonSerializable
public class Human extends Player{

    public Human(){}

    public Human(String name, String villageName) {
        super(name, villageName);
        int[] res = getNbTotalPlayer("human");
        Player.increaseTotalId(res[1]+1);
        super.setId(Player.getLastId());
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
