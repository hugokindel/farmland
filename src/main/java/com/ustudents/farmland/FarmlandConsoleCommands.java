package com.ustudents.farmland;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.tools.console.Console;
import com.ustudents.engine.tools.console.ConsoleCommand;
import com.ustudents.engine.tools.console.ConsoleCommands;
import com.ustudents.engine.network.NetMode;
import com.ustudents.engine.scene.SceneManager;
import com.ustudents.farmland.core.player.Player;
import com.ustudents.farmland.network.actions.EndGameMessage;
import com.ustudents.farmland.network.general.LoadSaveResponse;
import com.ustudents.farmland.network.general.PauseMessage;
import com.ustudents.farmland.network.general.ReceiveChatMessage;
import com.ustudents.farmland.network.general.SendChatMessage;
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

    public static boolean checkForPlayerName(String playerName) {
        boolean notFound = Farmland.get().getLoadedSave().getPlayerByName(playerName) == null;

        if (notFound) {
            Console.printlnWarning("You need to specify an existing player!");
        }

        return notFound;
    }

    @ConsoleCommand(description = "Set your money", argsDescription = "quantity", authority = {NetMode.Standalone})
    public void setMoney(Integer quantity) {
        if (checkForSave()) {
            return;
        }

        Farmland.get().getLoadedSave().getLocalPlayer().setMoney(quantity);
    }

    @ConsoleCommand(description = "Set the money of a given player", argsDescription = "playerName quantity", authority = {NetMode.Standalone, NetMode.DedicatedServer})
    public void setMoney(String playerName, Integer quantity) {
        if (checkForSave()) {
            return;
        }

        if (checkForPlayerName(playerName)) {
            return;
        }

        Farmland.get().getLoadedSave().getPlayerByName(playerName).setMoney(quantity);

        if (Farmland.get().getNetMode() == NetMode.DedicatedServer) {
            Out.println("Money of `" + playerName + "` changed to " + quantity);
            Farmland.get().getServer().broadcast(new LoadSaveResponse(Farmland.get().getLoadedSave()));
        }
    }

    @ConsoleCommand(description = "Win the game", authority = {NetMode.Standalone})
    public void win() {
        if (checkForSave()) {
            return;
        }

        ResultMenu resultMenu = new ResultMenu();
        resultMenu.currentSave = Farmland.get().getLoadedSave();
        resultMenu.isWin = true;
        Farmland.get().unloadSave();
        SceneManager.get().changeScene(resultMenu);
    }

    @ConsoleCommand(description = "Make a given player win the game", argsDescription = "playerName", authority = {NetMode.Standalone, NetMode.DedicatedServer})
    public void win(String playerName) {
        if (checkForSave()) {
            return;
        }

        if (checkForPlayerName(playerName)) {
            return;
        }

        if (Farmland.get().getNetMode() == NetMode.Standalone) {
            ResultMenu resultMenu = new ResultMenu();
            resultMenu.currentSave = Farmland.get().getLoadedSave();
            resultMenu.isWin = Farmland.get().getLoadedSave().getLocalPlayer() == Farmland.get().getLoadedSave().getPlayerByName(playerName);
            Farmland.get().unloadSave();
            SceneManager.get().changeScene(resultMenu);
        } else {
            for (Player player : Farmland.get().getLoadedSave().players) {
                if (player.isHuman()) {
                    Farmland.get().getServer().send(Farmland.get().getClientId(player.getId()),
                            new EndGameMessage(player.name.equals(playerName)));
                }
            }

            Farmland.get().serverGameEnded();
        }
    }

    @ConsoleCommand(description = "Kill yourself", authority = {NetMode.Standalone})
    public void kill() {
        if (checkForSave()) {
            return;
        }

        ResultMenu resultMenu = new ResultMenu();
        resultMenu.currentSave = Farmland.get().getLoadedSave();
        resultMenu.isWin = false;
        Farmland.get().unloadSave();
        SceneManager.get().changeScene(resultMenu);
    }

    @ConsoleCommand(description = "Kill a given player", argsDescription = "playerName", authority = {NetMode.Standalone, NetMode.DedicatedServer})
    public void kill(String playerName) {
        if (checkForSave()) {
            return;
        }

        if (checkForPlayerName(playerName)) {
            return;
        }

        if (Game.get().getNetMode() != NetMode.DedicatedServer) {
            if (Farmland.get().getLoadedSave().getLocalPlayer() == Farmland.get().getLoadedSave().getPlayerByName(playerName)) {
                ResultMenu resultMenu = new ResultMenu();
                resultMenu.currentSave = Farmland.get().getLoadedSave();
                resultMenu.isWin = false;
                Farmland.get().unloadSave();
                SceneManager.get().changeScene(resultMenu);
            } else {
                Farmland.get().getLoadedSave().kill(Farmland.get().getLoadedSave().getPlayerByName(playerName));
                ((InGameScene) SceneManager.get().getCurrentScene()).updateUi();
            }
        } else {
            Player deadPlayer = Farmland.get().getLoadedSave().getPlayerByName(playerName);

            if (deadPlayer.isHuman()) {
                Game.get().getServer().send(Farmland.get().getClientId(deadPlayer.getId()), new EndGameMessage(false));
            }

            Farmland.get().getLoadedSave().kill(Farmland.get().getLoadedSave().getPlayerByName(playerName));

            if (Farmland.get().getLoadedSave().areAllPlayersDead()) {
                Farmland.get().serverGameEnded();
            } else {
                for (Player player : Farmland.get().getLoadedSave().players) {
                    if (player.isHuman() && !player.isDead()) {
                        Farmland.get().getServer().send(Farmland.get().getClientId(player.getId()),
                                new LoadSaveResponse(Farmland.get().getLoadedSave()));
                    }
                }
            }
        }
    }

    @ConsoleCommand(description = "Pause/unpause the game", authority = {NetMode.Standalone, NetMode.ListenServer, NetMode.DedicatedServer})
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
