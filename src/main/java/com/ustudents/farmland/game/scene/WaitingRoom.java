package com.ustudents.farmland.game.scene;

import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.core.Scene;
import imgui.ImGui;

public class WaitingRoom extends Scene {
    @Override
    public void initialize() {

    }

    @Override
    public void update(double dt) {

    }

    @Override
    public void render() {
        ImGui.text("Menu Selector");
        if (ImGui.button("Multi Player Mode")) {
            Farmland.get().getSceneManager().changeScene(MultiPlayerMenu.class);
        }
    }

    @Override
    public void destroy() {

    }
}
