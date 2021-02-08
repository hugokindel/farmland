package com.ustudents.farmland.game.scene;

import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.core.Scene;
import com.ustudents.farmland.graphics.tools.ImGuiUtils;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiInputTextFlags;
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
        vsyncCurrentState = Farmland.get().getVsync();
        useVsync = new ImBoolean(Farmland.get().getVsync());

        //Shader shader = Resources.loadShader("spritebatch");
    }

    @Override
    public void update(double dt) {
        if (vsyncCurrentState != useVsync.get()) {
            vsyncCurrentState = useVsync.get();
            Farmland.get().setVsync(vsyncCurrentState);
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
            Farmland.get().getSceneManager().changeScene(SinglePlayerMenu.class);
        }
        if (ImGui.button("Multi Player Menu")) {
            Farmland.get().getSceneManager().changeScene(MultiPlayerMenu.class);
        }
        if (ImGui.button("Option Menu")) {
            Farmland.get().getSceneManager().changeScene(OptionMenu.class);
        }
        if (ImGui.button("Credit Menu")) {
            Farmland.get().getSceneManager().changeScene(CreditMenu.class);
        }
        if (ImGui.button("Example Scene")) {
            Farmland.get().getSceneManager().changeScene(ExampleScene.class);
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
