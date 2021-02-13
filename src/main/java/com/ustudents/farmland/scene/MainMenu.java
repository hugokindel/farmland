package com.ustudents.farmland.scene;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.Resources;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.graphic.Texture;
import com.ustudents.engine.scene.Scene;
import com.ustudents.engine.graphic.imgui.ImGuiUtils;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class MainMenu extends Scene {
    Texture texture;

    @Override
    public void initialize() {
        texture = Resources.loadTexture("examples/grass.png");
    }

    @Override
    public void update(double dt) {

    }

    @Override
    public void render() {
        spritebatch.begin();
        //spritebatch.drawCircle(new Vector2f(0, 0), 10, 25, 0, Color.WHITE, 1);
        spritebatch.end();
    }

    @Override
    public void renderImGui() {
        ImGuiUtils.setNextWindowWithSizeCentered(300, 300, ImGuiCond.Appearing);
        ImGui.begin("Main Menu");

        if (ImGui.button("Single Player Menu")) {
            Game.get().getSceneManager().changeScene(SinglePlayerMenu.class);
        }
        if (ImGui.button("Multi Player Menu")) {
            Game.get().getSceneManager().changeScene(MultiPlayerMenu.class);
        }
        if (ImGui.button("Option Menu")) {
            Game.get().getSceneManager().changeScene(OptionMenu.class);
        }
        if (ImGui.button("Credit Menu")) {
            Game.get().getSceneManager().changeScene(CreditMenu.class);
        }
        if (ImGui.button("Example Scene")) {
            Game.get().getSceneManager().changeScene(ExampleScene.class);
        }
        if (ImGui.button("Quit")) {
            Game.get().close();
        }

        ImGui.end();
    }

    @Override
    public void destroy() {

    }
}
