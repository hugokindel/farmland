package com.ustudents.farmland.game.component;

import com.ustudents.farmland.ecs.Component;
import com.ustudents.farmland.graphics.tools.annotation.Editable;
import imgui.ImGui;
import imgui.type.ImFloat;
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
