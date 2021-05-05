package com.ustudents.farmland;

import com.ustudents.engine.core.console.Console;
import com.ustudents.engine.core.console.ConsoleCommand;
import com.ustudents.engine.core.console.ConsoleCommands;
import com.ustudents.engine.scene.SceneManager;
import com.ustudents.farmland.scene.menus.ResultMenu;

public class FarmlandConsoleCommands extends ConsoleCommands {
    @ConsoleCommand(description = "Forcefully win the current game")
    public void win() {
        if (Farmland.get().getLoadedSave() == null) {
            Console.printlnWarning("You need to load a save to run this command!");
            return;
        }

        if (Farmland.get().isConnectedToServer()) {
            Console.printlnWarning("You do not have the authority to run this command!");
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
