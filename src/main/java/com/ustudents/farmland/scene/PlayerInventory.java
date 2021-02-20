package com.ustudents.farmland.scene;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.Timer;
import com.ustudents.engine.graphic.imgui.ImGuiUtils;
import com.ustudents.engine.scene.Scene;
import com.ustudents.farmland.component.TimerComponent;
import imgui.ImGui;
import imgui.flag.ImGuiCond;

public class PlayerInventory extends Scene {
    private final static int freeSlot = 15;

    @Override
    public void initialize() {

    }

    @Override
    public void update(float dt) {
        TimerComponent.increaseCurrentTime(dt);
        if(TimerComponent.getCurrentTime() >= TimerComponent.getTimerPerPlayer()){
            Game.get().getSceneManager().changeScene(InGameScene.class);
        }
    }

    @Override
    public void render() {

    }

    @Override
    public void renderImGui(){
        ImGuiUtils.setNextWindowWithSizeCentered(300, 300, ImGuiCond.Appearing);
        ImGui.begin("Player Inventory");
        InGameScene.timerAction();
        InGameScene.printThePlayerTurn();
        ImGui.separator();
        ImGui.text("Player options : \n");
        if (ImGui.button("Leave Inventory")) {
            Game.get().getSceneManager().changeScene(InGameScene.class);
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
