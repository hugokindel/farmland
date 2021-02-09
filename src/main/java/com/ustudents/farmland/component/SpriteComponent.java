package com.ustudents.farmland.component;

import com.ustudents.engine.graphic.Texture;
import com.ustudents.engine.core.ecs.Component;
import com.ustudents.engine.graphic.imgui.annotation.Editable;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.joml.Vector4i;

public class SpriteComponent extends Component {
    @Editable
    public Texture texture;
    public Vector4i textureRegion;
    public int zIndex;

    public SpriteComponent(Texture texture) {
        this(texture, new Vector4i(0, 0, texture.getWidth(), texture.getHeight()), 0);
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
