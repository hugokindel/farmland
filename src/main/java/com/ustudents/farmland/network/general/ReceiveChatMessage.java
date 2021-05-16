package com.ustudents.farmland.network.general;

import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.tools.console.Console;
import com.ustudents.engine.network.messages.Message;
import com.ustudents.farmland.Farmland;

// PROCESSED ON SERVER
@JsonSerializable
public class ReceiveChatMessage extends Message {
    @JsonSerializable
    public String senderName;

    @JsonSerializable
    public String text;

    public ReceiveChatMessage() {

    }

    public ReceiveChatMessage(String sendName, String text) {
        this.senderName = sendName;
        this.text = text;
    }

    @Override
    public void process() {
        if (!senderName.equals(Farmland.get().getLoadedSave().getLocalPlayer().name) && Console.exists()) {
            if (senderName.equals("server")) {
                Console.println(Resources.getLocalizedText("consoleServer") + ": " + text);
            } else {
                Console.println(senderName + ": " + text);
            }
        }
    }

    @Override
    public boolean shouldBeHandledOnMainThread() {
        return true;
    }

    @Override
    public ProcessingSide getProcessingSide() {
        return ProcessingSide.Client;
    }
}
