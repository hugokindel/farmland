package com.ustudents.farmland.network.actions;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.network.messages.Message;
import com.ustudents.farmland.Farmland;
import org.joml.Vector2i;

@JsonSerializable
public class TakeLoanMessage extends Message {
    @JsonSerializable
    Integer amount;

    public TakeLoanMessage() {

    }

    public TakeLoanMessage(int amount) {
        this.amount = amount;
    }

    @Override
    public void process() {
        Farmland.get().getLoadedSave().getCurrentPlayer().takeLoan(amount);
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
