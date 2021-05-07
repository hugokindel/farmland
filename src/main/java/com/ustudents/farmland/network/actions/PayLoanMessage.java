package com.ustudents.farmland.network.actions;

import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.network.messages.Message;
import com.ustudents.farmland.Farmland;

@JsonSerializable
public class PayLoanMessage extends Message {
    @JsonSerializable
    Integer amount;

    public PayLoanMessage() {

    }

    public PayLoanMessage(int amount) {
        this.amount = amount;
    }

    @Override
    public void process() {
        Farmland.get().getLoadedSave().getCurrentPlayer().payLoan(amount);
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
