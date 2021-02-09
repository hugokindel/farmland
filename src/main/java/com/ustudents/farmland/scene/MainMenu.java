package com.ustudents.farmland.scene;

import com.ustudents.engine.Game;
import com.ustudents.engine.scene.Scene;
import com.ustudents.engine.graphics.imgui.ImGuiUtils;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.type.ImBoolean;
import imgui.type.ImString;

public class MainMenu extends Scene {
    private ImString str = new ImString(5);
    private float[] flt = new float[1];
    private int count = 0;
    private static ImBoolean showDemo;
    private static boolean vsyncCurrentState;
    private static ImBoolean useVsync;

    @Override
    public void initialize() {
        str = new ImString(5);
        flt = new float[1];
        showDemo = new ImBoolean(false);
        vsyncCurrentState = Game.get().getVsync();
        useVsync = new ImBoolean(Game.get().getVsync());

        //Shader shader = Resources.loadShader("spritebatch");
    }

    @Override
    public void update(double dt) {
        if (vsyncCurrentState != useVsync.get()) {
            vsyncCurrentState = useVsync.get();
            Game.get().setVsync(vsyncCurrentState);
        }
    }

    @Override
    public void render() {

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
            System.exit(0);
        }

        ImGui.end();
    }

    @Override
    public void destroy() {

    }
}
