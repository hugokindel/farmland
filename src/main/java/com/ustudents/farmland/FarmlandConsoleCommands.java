package com.ustudents.farmland;

import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.graphic.imgui.console.Console;
import com.ustudents.engine.graphic.imgui.console.ConsoleCommand;
import com.ustudents.engine.graphic.imgui.console.ConsoleCommands;
import com.ustudents.engine.network.NetMode;
import com.ustudents.engine.scene.SceneManager;
import com.ustudents.farmland.scene.menus.ResultMenu;

public class FarmlandConsoleCommands extends ConsoleCommands {
    public static boolean checkForSave() {
        boolean noSaveLoaded = Farmland.get().getLoadedSave() == null;

        if (noSaveLoaded) {
            Console.printlnWarning("You need to load a save to run this command!");
        }

        return noSaveLoaded;
    }

    public static boolean checkStandaloneOnly() {
        boolean connectedToServer = Farmland.get().isConnectedToServer();

        if (connectedToServer) {
            Console.printlnWarning("You do not have the authority to run this command!");
        }

        return connectedToServer;
    }

    @ConsoleCommand(description = "Forcefully win the current game", authority = {NetMode.Standalone})
    public void win() {
        if (checkForSave() || checkStandaloneOnly()) {
            return;
        }

        ResultMenu resultMenu = new ResultMenu();
        resultMenu.currentPlayer = Farmland.get().getLoadedSave().getLocalPlayer();
        resultMenu.currentSave = Farmland.get().getLoadedSave();
        resultMenu.isWin = true;
        Farmland.get().unloadSave();
        SceneManager.get().changeScene(resultMenu);
    }
}
