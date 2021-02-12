package com.ustudents.farmland.scene;

import com.ustudents.engine.Game;
import com.ustudents.engine.scene.Scene;
import com.ustudents.engine.graphic.imgui.ImGuiUtils;
import imgui.ImGui;
import imgui.flag.ImGuiCond;

public class WaitingRoom extends Scene {
    @Override
    public void initialize() {

    }

    @Override
    public void update(double dt) {

    }

    @Override
    public void render() {
        ImGuiUtils.setNextWindowWithSizeCentered(300, 300, ImGuiCond.Appearing);
        ImGui.begin("Waiting Room");

        if (ImGui.button("Multiplayer Mode")) {
            Game.get().getSceneManager().changeScene(MultiPlayerMenu.class);
        }

        ImGui.end();
    }

    @Override
    public void destroy() {

    }
}
