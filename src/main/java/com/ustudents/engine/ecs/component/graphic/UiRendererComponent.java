package com.ustudents.engine.ecs.component.graphic;

import com.ustudents.engine.graphic.imgui.annotation.Viewable;

@Viewable
public class UiRendererComponent extends RendererComponent {
    /** Class constructor. */
    public UiRendererComponent() {
        this(0);
    }

    /**
     * Class constructor.
     *
     * @param zIndex The Z index.
     */
    public UiRendererComponent(Integer zIndex) {
        this.zIndex = zIndex;
    }
}
