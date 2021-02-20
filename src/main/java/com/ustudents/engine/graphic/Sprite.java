package com.ustudents.engine.graphic;

import com.ustudents.engine.graphic.imgui.annotation.Viewable;
import org.joml.Vector4f;

@Viewable
public class Sprite {
    @Viewable
    private final Texture texture;

    @Viewable
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
