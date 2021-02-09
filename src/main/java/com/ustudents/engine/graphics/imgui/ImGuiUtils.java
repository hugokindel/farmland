package com.ustudents.engine.graphics.imgui;

import com.ustudents.farmland.Farmland;
import imgui.ImGui;
import imgui.flag.ImGuiStyleVar;
import imgui.internal.flag.ImGuiItemFlags;
import org.joml.Vector2i;

public class ImGuiUtils {
    public static void setNextWindowPos(int x, int y, int cond) {
        Vector2i windowPos = Farmland.get().getWindow().getPosition();

        ImGui.setNextWindowPos(windowPos.x + x, windowPos.y + y + 19, cond);
    }

    public static void setNextWindowPosFromEnd(int x, int y, int cond) {
        Vector2i windowSize = Farmland.get().getWindow().getSize();

        setNextWindowPos(windowSize.x + x, y, cond);
    }

    public static void setNextWindowWithSizeCentered(float width, float height, int cond) {
        ImGui.setNextWindowSize(width, height, cond);

        Vector2i windowPos = Farmland.get().getWindow().getPosition();
        Vector2i windowSize = Farmland.get().getWindow().getSize();

        ImGui.setNextWindowPos(
                windowPos.x + (float)windowSize.x / 2 - width / 2,
                windowPos.y + (float)windowSize.y / 2 - height / 2,
                cond
        );
    }

    public static void setEnabled(boolean enabled) {
        if (enabled) {
            imgui.internal.ImGui.popItemFlag();
            ImGui.popStyleVar();
        } else {
            imgui.internal.ImGui.pushItemFlag(ImGuiItemFlags.Disabled, true);
            ImGui.pushStyleVar(ImGuiStyleVar.Alpha, ImGui.getStyle().getAlpha() * 0.5f);
        }
    }
}
