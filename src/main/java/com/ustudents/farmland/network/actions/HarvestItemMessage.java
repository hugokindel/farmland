package com.ustudents.farmland.network.actions;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.network.messages.Message;
import com.ustudents.farmland.Farmland;
import org.joml.Vector2i;

@JsonSerializable
public class HarvestItemMessage extends Message {
    @JsonSerializable
    Vector2i position;

    public HarvestItemMessage() {

    }

    public HarvestItemMessage(Vector2i position) {
        this.position = position;
    }

    @Override
    public void process() {
        if (Game.isDebugging()) {
            Out.printlnDebug("Client " + Farmland.get().getPlayerId(getSenderId()) +
                    ", harvested item at position (" + position.x + ", " + position.y + ")");
        }

        Farmland.get().getLoadedSave().getCurrentPlayer().harvestSelectedItem(position);
    }

    @Override
    public boolean shouldBeHandledOnMainThread() {
        return true;
    }

    @Override
    public ProcessingSide getProcessingSide() {
        return ProcessingSide.Server;
    }
}
