package com.ustudents.farmland.network.actions;

import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.network.messages.Message;

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
