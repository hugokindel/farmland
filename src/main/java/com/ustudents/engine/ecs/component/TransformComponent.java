package com.ustudents.engine.ecs.component;

import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.ecs.Component;
import com.ustudents.engine.graphic.imgui.annotation.Editable;
import org.joml.Vector2f;

@JsonSerializable
public class TransformComponent extends Component {
    /** The position in the world (in world coordinates). */
    @JsonSerializable
    @Editable
    public Vector2f position;

    /** The scale to use (default is x1,x1). */
    @JsonSerializable
    @Editable
    public Vector2f scale;

    /** The rotation. */
    @JsonSerializable
    @Editable
    public Float rotation;

    /** Class constructor. */
    public TransformComponent() {
        this(new Vector2f(0.0f, 0.0f), new Vector2f(1.0f, 1.0f), 0.0f);
    }

    /**
     * Class constructor.
     *
     * @param position The position.
     */
    public TransformComponent(Vector2f position) {
        this(position, new Vector2f(1.0f, 1.0f), 0.0f);
    }

    /**
     * Class constructor.
     *
     * @param position The position.
     * @param scale The scale.
     */
    public TransformComponent(Vector2f position, Vector2f scale) {
        this(position, scale, 0.0f);
    }

    /**
     * Class constructor.
     *
     * @param position The position.
     * @param scale The scale.
     * @param rotation The rotation (in degrees).
     */
    public TransformComponent(Vector2f position, Vector2f scale, Float rotation) {
        this.position = position;
        this.scale = scale;
        this.rotation = rotation;
    }
}
