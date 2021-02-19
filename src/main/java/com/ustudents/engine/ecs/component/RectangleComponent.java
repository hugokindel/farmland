package com.ustudents.engine.ecs.component;

import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.ecs.Component;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.graphic.imgui.annotation.Editable;
import org.joml.Vector2f;

@Editable
@JsonSerializable
public class RectangleComponent extends Component {
    /** The size. */
    @JsonSerializable
    @Editable
    public Vector2f size;

    /** The color. */
    @JsonSerializable
    @Editable
    public Color color;

    /** The thickness of the lines. */
    @JsonSerializable
    @Editable
    public Integer thickness;

    /** The origin. */
    @JsonSerializable
    @Editable
    public Vector2f origin;

    public RectangleComponent() {
        this(null, null, null,null);
    }

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
