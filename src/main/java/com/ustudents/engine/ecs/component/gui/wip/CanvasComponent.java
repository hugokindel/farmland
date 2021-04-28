package com.ustudents.engine.ecs.component.gui.wip;

import com.ustudents.engine.core.window.Window;
import org.joml.Vector2f;
import org.joml.Vector2i;

public class CanvasComponent extends GuiComponent {
    public CanvasComponent() {
        RectTransformComponent rect = getRect();
        Vector2i windowSize = Window.get().getSize();
        rect.size = new Vector2f(windowSize.x, windowSize.y);
    }
}
