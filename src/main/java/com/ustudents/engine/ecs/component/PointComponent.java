package com.ustudents.engine.ecs.component;

import com.ustudents.engine.ecs.Component;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.graphic.imgui.annotation.Editable;

public class PointComponent extends Component {
    /** The color. */
    @Editable
    public Color color;

    /** Class constructor. */
    public PointComponent() {
        this(Color.WHITE);
    }

    /**
     * Class constructor.
     *
     * @param color The color.
     */
    public PointComponent(Color color) {
        this.color = color;
    }
}
