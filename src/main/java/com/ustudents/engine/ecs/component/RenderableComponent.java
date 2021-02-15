package com.ustudents.engine.ecs.component;

import com.ustudents.engine.ecs.Component;
import com.ustudents.engine.graphic.imgui.annotation.Editable;

public class RenderableComponent extends Component {
    /** The Z index (sorting position of rendering). */
    @Editable
    public Integer zIndex;

    /** Class constructor. */
    public RenderableComponent() {
        this(0);
    }

    /**
     * Class constructor.
     *
     * @param zIndex The Z index.
     */
    public RenderableComponent(Integer zIndex) {
        this.zIndex = zIndex;
    }
}
