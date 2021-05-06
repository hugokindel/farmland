package com.ustudents.engine.graphic.imgui.console;

import com.ustudents.engine.Game;

public abstract class ConsoleCommands {
    @ConsoleCommand(description = "Shows this help message")
    public void help() {
        for (ConsoleCommandData commandData : Console.getListOfCommands()) {
            Console.println(commandData.name + ": " + commandData.description);
        }
    }

    @ConsoleCommand(description = "Saves data and closes the game")
    public void quit() {
        Console.println("Quitting...");
        Game.get().quit();
    }
}
