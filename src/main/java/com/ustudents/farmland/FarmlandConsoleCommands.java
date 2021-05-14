package com.ustudents.farmland;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.cli.option.annotation.Option;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.graphic.imgui.console.Console;
import com.ustudents.engine.graphic.imgui.console.ConsoleCommand;
import com.ustudents.engine.graphic.imgui.console.ConsoleCommands;
import com.ustudents.engine.network.NetMode;
import com.ustudents.engine.scene.SceneManager;
import com.ustudents.farmland.network.general.PauseMessage;
import com.ustudents.farmland.network.general.ReceiveChatMessage;
import com.ustudents.farmland.network.general.SendChatMessage;
import com.ustudents.farmland.scene.InGameScene;
import com.ustudents.farmland.scene.menus.ResultMenu;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class FarmlandConsoleCommands extends ConsoleCommands {
    public static boolean checkForSave() {
        boolean noSaveLoaded = Farmland.get().getLoadedSave() == null;

        if (noSaveLoaded) {
            Console.printlnWarning("You need to load a save to run this command!");
        }

        return noSaveLoaded;
    }

    @ConsoleCommand(description = "Adds a specific amount of money", argsDescription = "x", authority = {NetMode.Standalone})
    public void addMoney(Integer quantity) {
        if (checkForSave()) {
            return;
        }

        Farmland.get().getLoadedSave().getLocalPlayer().addMoney(quantity);
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

    @ConsoleCommand(description = "Say something", argsDescription = {"text..."})
    public void say(Object... text) {
        if (checkForSave()) {
            return;
        }

        StringBuilder total = new StringBuilder();

        for (Object el : text) {
            total.append((String)el).append(" ");
        }

        if (total.length() > 0) {
            total = new StringBuilder(total.substring(0, total.length() - 1));
        }

        if (Game.get().getSceneManager().getCurrentScene() instanceof InGameScene) {
            Console.println(Resources.getLocalizedText("you") + ": " + total.toString());

            if (Game.get().getNetMode() == NetMode.Client) {
                Game.get().getClient().send(new SendChatMessage(Farmland.get().getLoadedSave().getLocalPlayer().name, total.toString()));
            } else if (Game.get().getNetMode() == NetMode.DedicatedServer) {
                Game.get().getServer().broadcast(new ReceiveChatMessage("server", total.toString()));
            }
        }
    }
}
