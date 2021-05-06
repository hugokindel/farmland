package com.ustudents.farmland.network.general;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.network.messages.Message;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.scene.InGameScene;

// PROCESSED ON CLIENT
@JsonSerializable
public class PauseMessage extends Message {
    @JsonSerializable
    boolean inPause;

    public PauseMessage() {

    }

    public PauseMessage(boolean inPause) {
        this.inPause = inPause;
    }

    @Override
    public void process() {
        ((InGameScene)Game.get().getSceneManager().getCurrentScene()).setPause(inPause);
    }
}
