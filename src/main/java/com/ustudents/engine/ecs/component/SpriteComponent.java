package com.ustudents.engine.ecs.component;

import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.graphic.Texture;
import com.ustudents.engine.ecs.Component;
import com.ustudents.engine.graphic.imgui.annotation.Editable;
import org.joml.Vector4i;

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
    public Vector4i region;

    /** The Z index permits to sort the layer by priority when rendering. */
    @Editable
    public Integer zIndex;

    @Editable
    public Color tint;

    public SpriteComponent(Texture texture) {
        this(texture, new Vector4i(0, 0, texture.getWidth(), texture.getHeight()), 0, Color.WHITE);
    }

    public SpriteComponent(Texture texture, Vector4i region) {
        this(texture, region, 0, Color.WHITE);
    }

    public SpriteComponent(Texture texture, Vector4i region, Integer zIndex) {
        this(texture, region, zIndex, Color.WHITE);
    }

    public SpriteComponent(Texture texture, Vector4i region, Integer zIndex, Color tint) {
        this.texture = texture;
        this.region = region;
        this.zIndex = zIndex;
        this.tint = tint;
    }

    @Override
    public String toString() {
        return "SpriteComponent{" +
                "texture=" + texture +
                ", region=" + region +
                ", zIndex=" + zIndex +
                ", tint=" + tint +
                '}';
    }
}
