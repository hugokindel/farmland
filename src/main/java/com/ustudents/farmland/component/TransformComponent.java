package com.ustudents.farmland.component;

import com.ustudents.engine.scene.ecs.Component;
import com.ustudents.engine.graphic.imgui.annotation.Editable;
import org.joml.Vector2f;


public class TransformComponent extends Component {
    @Editable
    public Vector2f position;

    @Editable
    public Vector2f scale;

    @Editable
    public Float rotation;

    public TransformComponent(Vector2f position, Vector2f scale, Float rotation) {
        this.position = position;
        this.scale = scale;
        this.rotation = rotation;
    }

    public TransformComponent(Vector2f position, Vector2f scale) {
        this(position, scale, 0.0f);
    }

    public TransformComponent(Vector2f position) {
        this(position, new Vector2f(), 0.0f);
    }

    public TransformComponent() {
        this(new Vector2f(), new Vector2f(), 0.0f);
    }

    @Override
    public String toString() {
        return "TransformComponent{" +
                "position=" + position +
                ", scale=" + scale +
                ", rotation=" + rotation +
                '}';
    }
}
