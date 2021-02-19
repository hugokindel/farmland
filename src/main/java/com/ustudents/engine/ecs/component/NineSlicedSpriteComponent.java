package com.ustudents.engine.ecs.component;

import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.graphic.NineSlicedSprite;
import com.ustudents.engine.graphic.Sprite;
import com.ustudents.engine.ecs.Component;
import com.ustudents.engine.graphic.imgui.annotation.Editable;
import org.joml.Vector2f;

@JsonSerializable
public class NineSlicedSpriteComponent extends Component {
    /** The texture. */
    @JsonSerializable
    @Editable
    public NineSlicedSprite sprite;

    /** The size. */
    @JsonSerializable
    @Editable
    public Vector2f size;

    /** The color tint to apply on the texture. */
    @JsonSerializable
    @Editable
    public Color tint;

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
     * @param sprite The sprite.
     */
    public NineSlicedSpriteComponent(NineSlicedSprite sprite, Vector2f size) {
        this(
                sprite,
                size,
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
    public NineSlicedSpriteComponent(NineSlicedSprite sprite, Vector2f size, Color tint) {
        this(
                sprite,
                size,
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
    public NineSlicedSpriteComponent(NineSlicedSprite sprite, Vector2f size, Color tint, Vector2f origin) {
        this.sprite = sprite;
        this.size = size;
        this.tint = tint;
        this.origin = origin;
    }
}
