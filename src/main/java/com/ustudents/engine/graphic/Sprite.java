package com.ustudents.engine.graphic;

import com.ustudents.engine.graphic.imgui.annotation.Editable;
import org.joml.Vector4f;

@Editable
public class Sprite {
    @Editable
    private final Texture texture;

    @Editable
    private final Vector4f region;

    public Sprite(Texture texture, Vector4f region) {
        this.texture = texture;
        this.region = region;
    }

    public Texture getTexture() {
        return texture;
    }

    public Vector4f getRegion() {
        return region;
    }
}
