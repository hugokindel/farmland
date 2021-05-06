package com.ustudents.engine.graphic.imgui.console;

import com.ustudents.engine.Game;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public abstract class ConsoleCommands {
    @ConsoleCommand(description = "Shows this help message")
    public void help() {
        for (ConsoleCommandData commandData : Console.getListOfCommands()) {
            StringBuilder args = new StringBuilder();

            for (String arg : commandData.argDesc) {
                args.append(arg).append(" ");
            }

            if (args.length() > 0) {
                args.deleteCharAt(args.length() - 1);
            }

            Console.println(commandData.name + (args.length() > 0 ? " " + args.toString() : "") + ": " + commandData.description);
        }
    }

    @ConsoleCommand(description = "Saves data and closes the game")
    public void quit() {
        Console.println("Quitting...");
        Game.get().quit();
    }

    @ConsoleCommand(description = "Clear the console")
    public void clear() {
        Console.clear();
    }
}
