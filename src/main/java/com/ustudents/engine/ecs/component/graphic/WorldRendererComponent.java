package com.ustudents.engine.ecs.component.graphic;

import com.ustudents.engine.graphic.imgui.annotation.Viewable;

@Viewable
public class WorldRendererComponent extends RendererComponent {
    /** Class constructor. */
    public WorldRendererComponent() {
        this(0);
    }

    /**
     * Class constructor.
     *
     * @param zIndex The Z index.
     */
    public WorldRendererComponent(Integer zIndex) {
        this.zIndex = zIndex;
    }
}
