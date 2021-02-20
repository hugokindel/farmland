package com.ustudents.engine.ecs.component.core;

import com.ustudents.engine.ecs.Component;
import com.ustudents.engine.graphic.imgui.annotation.Viewable;
import org.joml.Vector2f;

/** A component for transformations. */
@Viewable
public class TransformComponent extends Component {
    /** The position in the world (in world coordinates). */
    @Viewable
    public Vector2f position;

    /** The scale to use (default is x1,x1). */
    @Viewable
    public Vector2f scale;

    /** The rotation. */
    @Viewable
    public Float rotation;

    /** Class constructor. */
    public TransformComponent() {
        this.position = new Vector2f();
        this.scale = new Vector2f(1.0f, 1.0f);
        this.rotation = 0.0f;
    }

    /** Class constructor. */
    public TransformComponent(Vector2f position) {
        this.position = position;
        this.scale = new Vector2f(1.0f, 1.0f);
        this.rotation = 0.0f;
    }

    /** Class constructor. */
    public TransformComponent(Vector2f position, Vector2f scale) {
        this.position = position;
        this.scale = scale;
        this.rotation = 0.0f;
    }

    /** Class constructor. */
    public TransformComponent(Vector2f position, Vector2f scale, float rotation) {
        this.position = position;
        this.scale = scale;
        this.rotation = rotation;
    }

    /**
     * Sets the position.
     *
     * @param position The new position.
     */
    public void setPosition(Vector2f position) {
        this.position = position;
    }

    /**
     * Sets the scale.
     *
     * @param scale The new scale.
     */
    public void setScale(Vector2f scale) {
        this.scale = scale;
    }

    /**
     * Sets the rotation.
     *
     * @param rotation The new rotation.
     */
    public void setRotation(Float rotation) {
        this.rotation = rotation;
    }
}
