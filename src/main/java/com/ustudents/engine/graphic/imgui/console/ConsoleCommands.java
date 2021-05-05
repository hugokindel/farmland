package com.ustudents.engine.graphic.imgui.console;

public abstract class ConsoleCommands {
    @ConsoleCommand(description = "Shows this help message")
    public void help() {
        for (ConsoleCommandData commandData : Console.getListOfCommands()) {
            Console.println(commandData.name + ": " + commandData.description);
        }
    }
}
