package com.ustudents.farmland.game.scene;

import com.ustudents.farmland.core.Scene;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImBoolean;
import imgui.type.ImString;

public class ExampleScene extends Scene {
    private final ImString str = new ImString(5);
    private final float[] flt = new float[1];
    private int count = 0;
    private static final ImBoolean SHOW_DEMO_WINDOW = new ImBoolean(false);

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

    }

    @Override
    public void renderImGui() {
        ImGui.text("Hello, World!");
        if (ImGui.button("Save")) {
            count++;
        }
        ImGui.sameLine();
        ImGui.text(String.valueOf(count));
        ImGui.inputText("string", str, ImGuiInputTextFlags.CallbackResize);
        ImGui.sliderFloat("float", flt, 0, 1);
        ImGui.separator();
        ImGui.text("Extra");
        ImGui.checkbox("Show Demo Window", SHOW_DEMO_WINDOW);
        if (SHOW_DEMO_WINDOW.get()) {
            ImGui.showDemoWindow(SHOW_DEMO_WINDOW);
        }
    }

    @Override
    public void destroy() {

    }
}
