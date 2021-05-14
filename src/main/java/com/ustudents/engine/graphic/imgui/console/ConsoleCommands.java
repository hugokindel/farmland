package com.ustudents.engine.graphic.imgui.console;

import com.ustudents.engine.Game;

public abstract class ConsoleCommands {
    public static boolean show = false;

    @ConsoleCommand(description = "Shows this help message")
    public void help() {
        for (ConsoleCommandData commandData : Console.getListOfCommands()) {
            if (commandData.name.equals("help") || commandData.name.equals("quit") || commandData.name.equals("clear") || show) {
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
