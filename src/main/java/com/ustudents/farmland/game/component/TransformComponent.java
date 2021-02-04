package com.ustudents.farmland.game.component;

import com.ustudents.farmland.ecs.Component;
import org.joml.Vector2f;


public class TransformComponent extends Component {
    public Vector2f position;
    public Vector2f scale;
    public Double rotation;

    public TransformComponent(Vector2f position, Vector2f scale, Double rotation) {
        this.position = position;
        this.scale = scale;
        this.rotation = rotation;
    }

    public TransformComponent(Vector2f position, Vector2f scale) {
        this(position, scale, 0.0);
    }

    public TransformComponent(Vector2f position) {
        this(position, new Vector2f(), 0.0);
    }

    public TransformComponent() {
        this(new Vector2f(), new Vector2f(), 0.0);
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
