package com.ustudents.engine.graphic;

import org.joml.Vector4f;

public class Sprite {
    private final Texture texture;

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
