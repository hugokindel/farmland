package com.ustudents.farmland;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.cli.option.annotation.Command;
import com.ustudents.farmland.player.Player;
import com.ustudents.farmland.scene.MainMenu;

import java.util.ArrayList;

/** The main class of the project. */
@Command(name = "farmland", version = "1.0.0", description = "A management game about farming.")
public class Farmland extends Game {
    private static ArrayList<Player> players;

    private static String kindOfGame;

    @Override
    protected void initialize() {
        sceneManager.changeScene(MainMenu.class);
        players = new ArrayList<>();
    }

    @Override
    protected void update(float dt) {

    }

    @Override
    protected void render() {

    }

    @Override
    protected void destroy() {

    }

    public static ArrayList<Player> getPlayers() {
        return players;
    }

    public static void setPlayers(Player player) {
        if(players.size()<4){
            players.add(player);
        }
    }

    public static String getKindOfGame() {
        return kindOfGame;
    }

    public static void setKindOfGame(String kindOfGame) {
        Farmland.kindOfGame = kindOfGame;
    }
}
