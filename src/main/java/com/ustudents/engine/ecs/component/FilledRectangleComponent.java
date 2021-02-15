package com.ustudents.engine.ecs.component;

import com.ustudents.engine.ecs.Component;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.graphic.imgui.annotation.Editable;
import org.joml.Vector2f;

public class FilledRectangleComponent extends Component {
    /** The size. */
    @Editable
    public Vector2f size;

    /** The color. */
    @Editable
    public Color color;

    /** The origin. */
    @Editable
    public Vector2f origin;

    /**
     * Class constructor.
     *
     * @param size The size.
     */
    public FilledRectangleComponent(Vector2f size) {
        this(
                size,
                Color.WHITE,
                new Vector2f(0.0f, 0.0f)
        );
    }

    /**
     * Class constructor.
     *
     * @param size The size.
     * @param color The color.
     */
    public FilledRectangleComponent(Vector2f size, Color color) {
        this(
                size,
                color,
                new Vector2f(0.0f, 0.0f)
        );
    }

    /**
     * Class constructor.
     *
     * @param size The size.
     * @param color The color.
     * @param origin The origin.
     */
    public FilledRectangleComponent(Vector2f size, Color color, Vector2f origin) {
        this.size = size;
        this.color = color;
        this.origin = origin;
    }
}
