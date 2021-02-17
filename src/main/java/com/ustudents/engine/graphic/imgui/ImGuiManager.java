package com.ustudents.engine.graphic.imgui;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;

import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;

// FIXME: Bug where an ImGui window becomes unresponsive when closing imgui with F1 when an ImGui window is outside the game window.
public class ImGuiManager {
    private ImGuiImplGlfw imGuiGlfw;
    private ImGuiImplGl3 imGuiGl3;

    public ImGuiManager() {
        imGuiGlfw = new ImGuiImplGlfw();
        imGuiGl3 = new ImGuiImplGl3();
    }

    public void initialize(long windowHandle, String glslVersion) {
        ImGui.createContext();

        final ImGuiIO io = ImGui.getIO();
        io.setIniFilename(null);
        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);
        //io.addConfigFlags(ImGuiConfigFlags.DockingEnable);
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);
        io.setConfigViewportsNoTaskBarIcon(true);
        imGuiGlfw.init(windowHandle, true);
        imGuiGl3.init(glslVersion);
    }

    public void startFrame() {
        imGuiGlfw.newFrame();
        ImGui.newFrame();
    }

    public void endFrame() {
        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long backupWindowPtr = glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            glfwMakeContextCurrent(backupWindowPtr);
        }
    }

    public void destroy() {
        imGuiGl3.dispose();
        imGuiGlfw.dispose();

        ImGui.destroyContext();
    }

    public ImGuiImplGlfw getImGuiGlfw() {
        return imGuiGlfw;
    }

    public ImGuiImplGl3 getImGuiGl3() {
        return imGuiGl3;
    }
}
