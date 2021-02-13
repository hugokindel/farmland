package com.ustudents.farmland.scene;

import com.ustudents.engine.Game;
import com.ustudents.engine.scene.Scene;
import com.ustudents.engine.graphic.imgui.ImGuiUtils;
import com.ustudents.farmland.Farmland;
import imgui.ImGui;
import imgui.flag.ImGuiCond;

public class MultiPlayerMenu extends Scene {
    @Override
    public void initialize() {
        Farmland.setKindOfGame("MultiPlayer");
    }

    @Override
    public void update(double dt) {

    }

    @Override
    public void render() {

    }

    @Override
    public void renderImGui() {
        ImGuiUtils.setNextWindowWithSizeCentered(300, 300, ImGuiCond.Appearing);
        ImGui.begin("Multiplayer Menu");

        if (ImGui.button("Main Menu")) {
            Game.get().getSceneManager().changeScene(MainMenu.class);
        }

        if (ImGui.button("Define User Menu")) {
            Game.get().getSceneManager().changeScene(DefineUser.class);
        }

        ImGui.end();

    }

    @Override
    public void destroy() {

    }
}
