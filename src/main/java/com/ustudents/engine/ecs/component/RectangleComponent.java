package com.ustudents.engine.ecs.component;

import com.ustudents.engine.ecs.Component;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.graphic.imgui.annotation.Editable;
import org.joml.Vector2f;

public class RectangleComponent extends Component {
    /** The size. */
    @Editable
    public Vector2f size;

    /** The color. */
    @Editable
    public Color color;

    /** The thickness of the lines. */
    @Editable
    public Integer thickness;

    /** The origin. */
    @Editable
    public Vector2f origin;

    /**
     * Class constructor.
     *
     * @param size The size.
     */
    public RectangleComponent(Vector2f size) {
        this(
                size,
                Color.WHITE,
                1,
                new Vector2f(0.0f, 0.0f)
        );
    }

    /**
     * Class constructor.
     *
     * @param size The size.
     * @param color The color.
     */
    public RectangleComponent(Vector2f size, Color color) {
        this(
                size,
                color,
                1,
                new Vector2f(0.0f, 0.0f)
        );
    }

    /**
     * Class constructor.
     *
     * @param size The size.
     * @param color The color.
     * @param thickness The thickness.
     */
    public RectangleComponent(Vector2f size, Color color, Integer thickness) {
        this(
                size,
                color,
                thickness,
                new Vector2f(0.0f, 0.0f)
        );
    }

    /**
     * Class constructor.
     *
     * @param size The size.
     * @param color The color.
     * @param thickness The thickness.
     * @param origin The origin.
     */
    public RectangleComponent(Vector2f size, Color color, Integer thickness, Vector2f origin) {
        this.size = size;
        this.color = color;
        this.thickness = thickness;
        this.origin = origin;
    }
}
