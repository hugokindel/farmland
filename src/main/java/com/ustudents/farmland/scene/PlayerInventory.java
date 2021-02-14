package com.ustudents.farmland.scene;

import com.ustudents.engine.Game;
import com.ustudents.engine.graphic.imgui.ImGuiUtils;
import com.ustudents.engine.scene.Scene;
import imgui.ImGui;
import imgui.flag.ImGuiCond;

public class PlayerInventory extends Scene {
    @Override
    public void initialize() {

    }

    @Override
    public void update(double dt) {

    }

    @Override
    public void render() {

    }

    @Override
    public void renderImGui(){
        ImGuiUtils.setNextWindowWithSizeCentered(300, 300, ImGuiCond.Appearing);
        ImGui.begin("Player Inventory");
        if (ImGui.button("Game Menu")) {
            Game.get().getSceneManager().changeScene(GameMenu.class);
        }
        ImGui.separator();
        for(int i=0;i<15;i++){

        }
        ImGui.end();

    }

    @Override
    public void destroy() {

    }
}
