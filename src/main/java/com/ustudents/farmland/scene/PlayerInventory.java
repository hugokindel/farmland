package com.ustudents.farmland.scene;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.Timer;
import com.ustudents.engine.graphic.imgui.ImGuiUtils;
import com.ustudents.engine.scene.Scene;
import imgui.ImGui;
import imgui.flag.ImGuiCond;

public class PlayerInventory extends Scene {
    private final static int freeSlot = 15;

    @Override
    public void initialize() {

    }

    @Override
    public void update(double dt) {
        if(Timer.getCurrentTime() >= Timer.getTimerPerPlayer()){
            Game.get().getSceneManager().changeScene(GameMenu.class);
        }
    }

    @Override
    public void render() {
        Timer.increaseCurrentTime();
    }

    @Override
    public void renderImGui(){
        ImGuiUtils.setNextWindowWithSizeCentered(300, 300, ImGuiCond.Appearing);
        ImGui.begin("Player Inventory");
        GameMenu.timerAction();
        GameMenu.printThePlayerTurn();
        ImGui.separator();
        ImGui.text("Player options : \n");
        if (ImGui.button("Leave Inventory")) {
            Game.get().getSceneManager().changeScene(GameMenu.class);
        }
        ImGui.text("\n");
        ImGui.separator();
        ImGui.text("Player Inventory : \n");
        for(int i=0;i<15;i++){

        }
        ImGui.end();

    }

    @Override
    public void destroy() {

    }
}
