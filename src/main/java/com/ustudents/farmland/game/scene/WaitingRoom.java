package com.ustudents.farmland.game.scene;

import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.core.Scene;
import com.ustudents.farmland.graphics.tools.ImGuiUtils;
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
            Farmland.get().getSceneManager().changeScene(MultiPlayerMenu.class);
        }

        ImGui.end();
    }

    @Override
    public void destroy() {

    }
}
