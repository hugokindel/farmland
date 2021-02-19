package com.ustudents.farmland;

import com.ustudents.engine.Game;
import com.ustudents.engine.audio.SoundManager;
import com.ustudents.engine.core.cli.option.annotation.Command;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.core.json.Json;
import com.ustudents.engine.graphic.Spritesheet;
import com.ustudents.farmland.component.GoalComponent;
import com.ustudents.farmland.player.Player;
import com.ustudents.farmland.scene.MainMenu;

import java.util.ArrayList;

/** The main class of the project. */
@Command(name = "farmland", version = "1.0.0", description = "A management game about farming.")
public class Farmland extends Game {
    private static ArrayList<Player> players;

    private static boolean playersIsInit;

    private static String kindOfGame;

    private static boolean isInGame;

    private static GoalComponent goal;

    @Override
    protected void initialize() {
        changeIcon("logo.png");
        changeCursor("cursor.png");

        players = new ArrayList<>();
        goal = new GoalComponent();

        sceneManager.changeScene(new MainMenu());
    }

    public static ArrayList<Player> getPlayers() {
        return players;
    }

    public static void setPlayers(Player player) {
        if(players.size()<4){
            players.add(player);
        }
    }

    public static void throwPlayer(){
        while(players.size()>1){
            players.remove(players.size()-1);
        }
    }

    public static void throwBot(int i){
        players.remove(i);
    }

    public static int numberOfPlayer(){
        return players.size();
    }

    public static String getKindOfGame() {
        return kindOfGame;
    }

    public static void setKindOfGame(String kindOfGame) {
        Farmland.kindOfGame = kindOfGame;
    }

    public static boolean isPlayersIsInit() {
        return playersIsInit;
    }

    public static void setPlayersIsInit(boolean bool) {
        playersIsInit = bool;
    }

    public static boolean isInGame() {
        return isInGame;
    }

    public static void setInGame(boolean inGame) {
        isInGame = inGame;
    }

    public static GoalComponent getGoal() {
        return goal;
    }

    public static void setGoal(GoalComponent goal) {
        Farmland.goal = goal;
    }
}
