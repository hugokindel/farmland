package com.ustudents.engine.network;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.cli.print.Out;

import java.util.Scanner;

public class ServerCliThread extends Thread {
    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);

        Out.println("To stop the server, press 'quit'.");

        while (!Game.get().shouldQuit()) {
            if (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if (line.equals("quit")) {
                    Out.println("Quit command intercepted.");
                    Game.get().quit();
                    break;
                }
            }
        }

        scanner.close();
    }
}
