package com.ustudents.engine.ecs.component.graphic;

import com.ustudents.engine.ecs.Component;
import com.ustudents.engine.graphic.imgui.annotation.Viewable;

@Viewable
public abstract class RendererComponent extends Component {
    /** The Z index (sorting position of rendering). */
    @Viewable
    public Integer zIndex;

    /** Class constructor. */
    public RendererComponent() {
        this(0);
    }

    /**
     * Class constructor.
     *
     * @param zIndex The Z index.
     */
    public RendererComponent(Integer zIndex) {
        this.zIndex = 0;
    }

    public void setzIndex(Integer zIndex) {
        this.zIndex = zIndex;
    }
}
