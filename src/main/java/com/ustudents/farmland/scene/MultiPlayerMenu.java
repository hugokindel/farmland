package com.ustudents.farmland.scene;

import com.ustudents.engine.Game;
import com.ustudents.engine.scene.Scene;
import com.ustudents.engine.graphics.imgui.ImGuiUtils;
import imgui.ImGui;
import imgui.flag.ImGuiCond;

public class MultiPlayerMenu extends Scene {
    @Override
    public void initialize() {

    }

    @Override
    public void update(double dt) {

    }

    @Override
    public void render() {
        ImGuiUtils.setNextWindowWithSizeCentered(300, 300, ImGuiCond.Appearing);
        ImGui.begin("Multiplayer Menu");

        if (ImGui.button("Main Menu")) {
            Game.get().getSceneManager().changeScene(MainMenu.class);
        }
        if (ImGui.button("Waiting Room")) {
            Game.get().getSceneManager().changeScene(WaitingRoom.class);
        }

        ImGui.end();
    }

    @Override
    public void destroy() {

    }
}
