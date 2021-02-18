package com.ustudents.engine.ecs.component;

import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.graphic.Texture;
import com.ustudents.engine.ecs.Component;
import com.ustudents.engine.graphic.imgui.annotation.Editable;
import org.joml.Vector2f;
import org.joml.Vector4f;

@JsonSerializable
public class SpriteComponent extends Component {
    /** The texture. */
    @JsonSerializable
    @Editable
    public Texture texture;

    /** The color tint to apply on the texture. */
    @JsonSerializable
    @Editable
    public Color tint;

    /**
     * The texture region.
     * - x,y contains the position to show within the texture.
     * - z,w contains the length to show after this position.
     */
    @JsonSerializable
    @Editable
    public Vector4f region;

    /** The origin. */
    @JsonSerializable
    @Editable
    public Vector2f origin;

    public SpriteComponent() {
        this(null, null, null, null);
    }

    /**
     * Class constructor.
     *
     * @param texture The texture.
     */
    public SpriteComponent(Texture texture) {
        this(
                texture,
                new Vector4f(0, 0, texture.getWidth(), texture.getHeight()),
                Color.WHITE,
                new Vector2f(0.0f, 0.0f)
        );
    }

    /**
     * Class constructor.
     *
     * @param texture The texture.
     * @param region The texture region.
     */
    public SpriteComponent(Texture texture, Vector4f region) {
        this(texture, region, Color.WHITE, new Vector2f(0.0f, 0.0f));
    }

    /**
     * Class constructor.
     *
     * @param texture The texture.
     * @param region The texture region.
     * @param tint The tint color.
     */
    public SpriteComponent(Texture texture, Vector4f region, Color tint) {
        this(texture, region, tint, new Vector2f(0.0f, 0.0f));
    }

    /**
     * Class constructor.
     *
     * @param texture The texture.
     * @param region The texture region.
     * @param tint The tint color.
     * @param origin The origin.
     */
    public SpriteComponent(Texture texture, Vector4f region, Color tint, Vector2f origin) {
        this.texture = texture;
        this.region = region;
        this.tint = tint;
        this.origin = origin;
    }
}
