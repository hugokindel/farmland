package com.ustudents.engine.ecs.component;

import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.graphic.NineSlicedSprite;
import com.ustudents.engine.ecs.Component;
import com.ustudents.engine.graphic.imgui.annotation.Editable;
import org.joml.Vector2f;

@Editable
@JsonSerializable
public class NineSlicedSpriteComponent extends Component {
    /** The color tint to apply on the texture. */
    @JsonSerializable
    @Editable
    public Color tint;

    /** The texture. */
    @JsonSerializable
    @Editable
    public NineSlicedSprite parts;

    /** The size. */
    @JsonSerializable
    @Editable
    public Vector2f size;

    /** The origin. */
    @JsonSerializable
    @Editable
    public Vector2f origin;

    /** Class constructor. */
    public NineSlicedSpriteComponent() {
        this(null, null, null, null);
    }

    /**
     * Class constructor.
     *
     * @param parts The sprite.
     */
    public NineSlicedSpriteComponent(NineSlicedSprite parts, Vector2f size) {
        this(
                parts,
                size,
                Color.WHITE,
                new Vector2f(0.0f, 0.0f)
        );
    }

    /**
     * Class constructor.
     *
     * @param parts The sprite.
     * @param tint The tint color.
     */
    public NineSlicedSpriteComponent(NineSlicedSprite parts, Vector2f size, Color tint) {
        this(
                parts,
                size,
                tint,
                new Vector2f(0.0f, 0.0f)
        );
    }

    /**
     * Class constructor.
     *
     * @param parts The sprite.
     * @param tint The tint color.
     * @param origin The origin.
     */
    public NineSlicedSpriteComponent(NineSlicedSprite parts, Vector2f size, Color tint, Vector2f origin) {
        this.parts = parts;
        this.size = size;
        this.tint = tint;
        this.origin = origin;
    }
}
