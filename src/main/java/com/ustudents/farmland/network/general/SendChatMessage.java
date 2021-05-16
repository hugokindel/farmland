package com.ustudents.farmland.network.general;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.tools.console.Console;
import com.ustudents.engine.network.messages.Message;

// PROCESSED ON SERVER
@JsonSerializable
public class SendChatMessage extends Message {
    @JsonSerializable
    public String senderName;

    @JsonSerializable
    public String text;

    public SendChatMessage() {

    }

    public SendChatMessage(String sendName, String text) {
        this.senderName = sendName;
        this.text = text;
    }

    @Override
    public void process() {
        Console.println(senderName + ": " + text);
        Game.get().getServer().broadcast(new ReceiveChatMessage(senderName, text));
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
