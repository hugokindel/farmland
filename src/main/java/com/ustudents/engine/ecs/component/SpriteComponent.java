package com.ustudents.engine.ecs.component;

import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.graphic.Sprite;
import com.ustudents.engine.ecs.Component;
import com.ustudents.engine.graphic.imgui.annotation.Editable;
import org.joml.Vector2f;

@Editable
@JsonSerializable
public class SpriteComponent extends Component {
    /** The texture. */
    @JsonSerializable
    @Editable
    public Sprite sprite;

    /** The color tint to apply on the texture. */
    @JsonSerializable
    @Editable
    public Color tint;

    /** The origin. */
    @JsonSerializable
    @Editable
    public Vector2f origin;

    /** Class constructor. */
    public SpriteComponent() {
        this(null, null, null);
    }

    /**
     * Class constructor.
     *
     * @param sprite The sprite.
     */
    public SpriteComponent(Sprite sprite) {
        this(
                sprite,
                Color.WHITE,
                new Vector2f(0.0f, 0.0f)
        );
    }

    /**
     * Class constructor.
     *
     * @param sprite The sprite.
     * @param tint The tint color.
     */
    public SpriteComponent(Sprite sprite, Color tint) {
        this(
                sprite,
                tint,
                new Vector2f(0.0f, 0.0f)
        );
    }

    /**
     * Class constructor.
     *
     * @param sprite The sprite.
     * @param tint The tint color.
     * @param origin The origin.
     */
    public SpriteComponent(Sprite sprite, Color tint, Vector2f origin) {
        this.sprite = sprite;
        this.tint = tint;
        this.origin = origin;
    }
}
