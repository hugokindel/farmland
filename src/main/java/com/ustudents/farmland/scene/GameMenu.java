package com.ustudents.farmland.scene;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.Timer;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.graphic.imgui.ImGuiUtils;
import com.ustudents.engine.scene.Scene;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.player.Player;
import imgui.ImGui;
import imgui.flag.ImGuiCond;

public class GameMenu extends Scene {
    private static boolean[] isTurnOf;
    private static Player currentPlayerTurn;

    @Override
    public void initialize() {
        if(!Farmland.isInGame()){
            Timer.setCurrentTime(0);
            Farmland.setInGame(true);
        }
        isTurnOf = new boolean[Farmland.numberOfPlayer()];
        if(Farmland.getKindOfGame().equals("SinglePlayer")){
            int randomNum = (int) (Math.random() * isTurnOf.length);
            Out.println(randomNum);
            isTurnOf[randomNum] = true;
            currentPlayerTurn = Farmland.getPlayers().get(randomNum);
        }
    }

    @Override
    public void update(double dt) {
        if(Timer.getCurrentTime() >= Timer.getTimerPerPlayer()){
            Timer.setCurrentTime(0);
            for(int i = 0; i < isTurnOf.length; i++){
                if(isTurnOf[i]){
                    isTurnOf[i] = false;
                    isTurnOf[(i+1)%isTurnOf.length] = true;
                    currentPlayerTurn = Farmland.getPlayers().get((i+1)%isTurnOf.length);
                    break;
                }
            }
        }
    }

    @Override
    public void render() {
        Timer.increaseCurrentTime();
    }

    public static void timerAction(){
        int printTime = (Timer.getTimerPerPlayer()-Timer.getCurrentTime())/10000;
        ImGui.text("Timer : " + printTime/60 + "min" + printTime%60 + "s");
    }

    public static void printThePlayerTurn(){
        ImGui.text("\n");
        ImGui.text("is Playing: " + currentPlayerTurn.getUserName());
        ImGui.text("\n");
    }

    public void printForPlayerTurn(){
        if(isTurnOf[0]){
            ImGui.text("Player options : \n");
            if (ImGui.button("Inventory")){
                Game.get().getSceneManager().changeScene(PlayerInventory.class);
            }
            ImGui.text("\n");
            if (ImGui.button("Finish your turn")){
                Timer.setCurrentTime(Timer.getTimerPerPlayer());
            }
            ImGui.sameLine();
        }

    }

    @Override
    public void renderImGui(){
        ImGuiUtils.setNextWindowWithSizeCentered(300, 300, ImGuiCond.Appearing);
        ImGui.begin("Game Menu");
        timerAction();
        printThePlayerTurn();
        ImGui.separator();
        printForPlayerTurn();
        if (ImGui.button("Leave the game")){
            if (Farmland.getKindOfGame().equals("MultiPlayer")){

            }
            Timer.setCurrentTime(0);
            Farmland.setInGame(false);
            Game.get().getSceneManager().changeScene(SinglePlayerMenu.class);
        }
        ImGui.end();
    }

    @Override
    public void destroy() {

    }
}
