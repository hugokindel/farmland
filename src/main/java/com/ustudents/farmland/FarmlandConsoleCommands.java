package com.ustudents.farmland;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.graphic.imgui.console.Console;
import com.ustudents.engine.graphic.imgui.console.ConsoleCommand;
import com.ustudents.engine.graphic.imgui.console.ConsoleCommands;
import com.ustudents.engine.network.NetMode;
import com.ustudents.engine.scene.SceneManager;
import com.ustudents.farmland.network.general.PauseMessage;
import com.ustudents.farmland.scene.InGameScene;
import com.ustudents.farmland.scene.menus.ResultMenu;

public class FarmlandConsoleCommands extends ConsoleCommands {
    public static boolean checkForSave() {
        boolean noSaveLoaded = Farmland.get().getLoadedSave() == null;

        if (noSaveLoaded) {
            Console.printlnWarning("You need to load a save to run this command!");
        }

        return noSaveLoaded;
    }

    @ConsoleCommand(description = "Forcefully win the current game", authority = {NetMode.Standalone})
    public void win() {
        if (checkForSave()) {
            return;
        }

        ResultMenu resultMenu = new ResultMenu();
        resultMenu.currentPlayer = Farmland.get().getLoadedSave().getLocalPlayer();
        resultMenu.currentSave = Farmland.get().getLoadedSave();
        resultMenu.isWin = true;
        Farmland.get().unloadSave();
        SceneManager.get().changeScene(resultMenu);
    }

    @ConsoleCommand(description = "Pause the game", authority = {NetMode.Standalone, NetMode.ListenServer, NetMode.DedicatedServer})
    public void pause() {
        if (checkForSave()) {
            return;
        }

        if (Game.get().getSceneManager().getCurrentScene() instanceof InGameScene) {
            InGameScene scene = ((InGameScene)Game.get().getSceneManager().getCurrentScene());

            scene.setPause(!scene.inPause);

            if (Game.get().getNetMode() != NetMode.Standalone) {
                Game.get().getServer().broadcast(new PauseMessage(scene.inPause));
            }
        } else {
            Console.printlnWarning("No active game!");
        }
    }
}
