package com.ustudents.engine.graphic;

import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.graphic.imgui.annotation.Viewable;
import org.joml.Vector4f;

@Viewable
@JsonSerializable
public class Sprite {
    @Viewable
    @JsonSerializable
    private Texture texture;

    @Viewable
    @JsonSerializable
    private Vector4f region;

    public Sprite() {
        this.texture = null;
        this.region = null;
    }

    public Sprite(Texture texture) {
        this.texture = texture;
        this.region = new Vector4f(0, 0, texture.getWidth(), texture.getHeight());
    }

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
