package com.ustudents.farmland.game.scene;

import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.core.Scene;
import imgui.ImGui;

public class MultiPlayerMenu extends Scene {
    @Override
    public void initialize() {

    }

    @Override
    public void processInput() {

    }

    @Override
    public void update(double dt) {

    }

    @Override
    public void render() {
        ImGui.separator();
        ImGui.text("Menu Selector");
        if (ImGui.button("Main Menu")) {
            Farmland.get().getSceneManager().changeScene(MainMenu.class);
        }
        if (ImGui.button("Waiting Room")) {
            Farmland.get().getSceneManager().changeScene(WaitingRoom.class);
        }
    }

    @Override
    public void destroy() {

    }
}