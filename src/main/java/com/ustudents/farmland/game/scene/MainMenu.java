package com.ustudents.farmland.game.scene;

import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.core.Scene;
import imgui.ImGui;
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
    public void processInput() {

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
        ImGui.text("FPS: " + Farmland.get().getTimer().getFPS());
        ImGui.text("Framerate: " + Farmland.get().getTimer().getFrameDuration());
        ImGui.checkbox("use vsync", useVsync);
        ImGui.separator();
        if (ImGui.button("Save")) {
            count++;
        }
        ImGui.sameLine();
        ImGui.text(String.valueOf(count));
        ImGui.inputText("string", str, ImGuiInputTextFlags.CallbackResize);
        ImGui.sliderFloat("float", flt, 0, 1);
        ImGui.separator();
        ImGui.text("Extra");
        ImGui.checkbox("Show Demo Window", showDemo);
        if (showDemo.get()) {
            ImGui.showDemoWindow(showDemo);
        }
        ImGui.separator();
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
        if (ImGui.button("Quit")) {
            System.exit(0);
        }
    }

    @Override
    public void destroy() {

    }
}