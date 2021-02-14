package com.ustudents.farmland.scene;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.graphic.imgui.ImGuiUtils;
import com.ustudents.engine.scene.Scene;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.player.Player;
import imgui.ImGui;
import imgui.flag.ImGuiCond;

public class GameMenu extends Scene {
    private static boolean[] isTurnOf;
    private Player currentPlayerTurn;
    private int currentTime;
    private final static int timerPerPlayer = 90*10000;

    @Override
    public void initialize() {
        currentTime =  90*10000-10000;
        isTurnOf = new boolean[Farmland.numberOfPlayer()];
        isTurnOf[0] = true;
        currentPlayerTurn = Farmland.getPlayers().get(0);
        if(Farmland.getKindOfGame().equals("SinglePlayer")){
            Out.println(Farmland.getPlayers().toString());
        }
    }

    @Override
    public void update(double dt) {
        if(currentTime >= timerPerPlayer){
            currentTime = 0;
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
        currentTime++;
    }

    private void timerAction(){
        int printTime = (timerPerPlayer-currentTime)/10000;
        ImGui.text("Timer : " + printTime/60 + "min" + printTime%60 + "s");
    }

    private void printThePlayerTurn(){
        ImGui.text("\n");
        ImGui.text("is Playing: " + currentPlayerTurn.getUserName());
        ImGui.text("\n");
    }

    @Override
    public void renderImGui(){
        ImGuiUtils.setNextWindowWithSizeCentered(300, 300, ImGuiCond.Appearing);
        ImGui.begin("Game Menu");
        timerAction();
        printThePlayerTurn();
        ImGui.separator();
        ImGui.text("Player options : \n");
        if (ImGui.button("Inventory")){
            Game.get().getSceneManager().changeScene(PlayerInventory.class);
        }
        ImGui.text("\n");
        if (ImGui.button("Finish your turn")){
            currentTime = timerPerPlayer;
        }
        ImGui.sameLine();
        if (ImGui.button("Leave the game")){
            if (Farmland.getKindOfGame().equals("MultiPlayer")){

            }
            Game.get().getSceneManager().changeScene(SinglePlayerMenu.class);
        }
        ImGui.end();
    }

    @Override
    public void destroy() {

    }
}
