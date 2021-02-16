package com.ustudents.engine.ecs.component;

import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.ecs.Component;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.graphic.imgui.annotation.Editable;
import org.joml.Vector2f;

@JsonSerializable
public class FilledRectangleComponent extends Component {
    /** The size. */
    @JsonSerializable
    @Editable
    public Vector2f size;

    /** The color. */
    @JsonSerializable
    @Editable
    public Color color;

    /** The origin. */
    @JsonSerializable
    @Editable
    public Vector2f origin;

    public FilledRectangleComponent() {
        this(null, null, null);
    }

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
