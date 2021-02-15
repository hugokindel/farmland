package com.ustudents.engine.ecs.component;

import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.graphic.Texture;
import com.ustudents.engine.ecs.Component;
import com.ustudents.engine.graphic.imgui.annotation.Editable;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class SpriteComponent extends Component {
    /** The texture. */
    @Editable
    public Texture texture;

    /**
     * The texture region.
     * - x,y contains the position to show within the texture.
     * - z,w contains the length to show after this position.
     */
    @Editable
    public Vector4f region;

    /** The color tint to apply on the texture. */
    @Editable
    public Color tint;

    /** The origin. */
    @Editable
    public Vector2f origin;

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
