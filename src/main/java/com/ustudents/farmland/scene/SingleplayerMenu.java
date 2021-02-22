package com.ustudents.farmland.scene;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.Window;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.ecs.Entity;
import com.ustudents.engine.ecs.component.core.TransformComponent;
import com.ustudents.engine.ecs.component.graphics.*;
import com.ustudents.engine.graphic.*;
import com.ustudents.engine.gui.GuiBuilder;
import com.ustudents.engine.scene.Scene;
import com.ustudents.engine.graphic.imgui.ImGuiUtils;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.component.GridComponent;
import com.ustudents.farmland.component.PlayerMovementComponent;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import org.joml.Vector2f;
import org.joml.Vector2i;

public class SingleplayerMenu extends Scene {
    @Override
    public void initialize() {
        getWorldCamera().enableInput(false);

        Farmland.setKindOfGame("SinglePlayer");
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void render() {

    }

    @Override
    public void renderImGui() {
        ImGuiUtils.setNextWindowWithSizeCentered(300, 300, ImGuiCond.Appearing);
        ImGui.begin("Singleplayer Menu");

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
