package com.ustudents.engine.ecs.component;

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
    public Vector4i textureRegion;

    /** The Z index permits to sort the layer by priority when rendering. */
    public int zIndex;

    public SpriteComponent(Texture texture) {
        this(texture, new Vector4i(0, 0, texture.getWidth(), texture.getHeight()), 0);
    }

    public SpriteComponent(Texture texture, Vector4i textureRegion) {
        this(texture, textureRegion, 0);
    }

    public SpriteComponent(Texture texture, Vector4i textureRegion, int zIndex) {
        this.texture = texture;
        this.textureRegion = textureRegion;
        this.zIndex = zIndex;
    }

    @Override
    public String toString() {
        return "SpriteComponent{" +
                "texture=" + texture +
                ", textureRegion=" + textureRegion +
                ", zIndex=" + zIndex +
                '}';
    }
}
