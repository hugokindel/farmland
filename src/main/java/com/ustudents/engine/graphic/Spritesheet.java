package com.ustudents.engine.graphic;

import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.core.json.annotation.JsonSerializableConstructor;
import com.ustudents.engine.core.json.annotation.JsonSerializableType;
import org.joml.Vector4f;

import java.util.HashMap;
import java.util.Map;

@JsonSerializable
@SuppressWarnings({"unchecked"})
public class Spritesheet {
    public enum Format {
        NineSlicedSprite,
        AnimatedSprite
    }

    @JsonSerializable
    private String path;

    @JsonSerializable(type = JsonSerializableType.DeserializableOnly)
    private Map<String, Vector4f> sprites;

    private Texture texture;

    private Map<String, Sprite> spritePerName;

    private Map<String, SpriteAnimation> animations;

    @JsonSerializableConstructor
    public void deserialize(Map<String, Object> json) {
        spritePerName = new HashMap<>();
        texture = Resources.loadTexture(path);
        animations = new HashMap<>();

        for (Map.Entry<String, Vector4f> entry : sprites.entrySet()) {
            spritePerName.put(entry.getKey(), new Sprite(texture, entry.getValue()));
        }

        if (json.containsKey("animations")) {
            for (Map.Entry<String, Object> entry : ((Map<String, Object>)json.get("animations")).entrySet()) {
                animations.put(entry.getKey(), SpriteAnimation.deserialize(this, (Map<String, Object>)entry.getValue()));
            }
        }
    }

    public Texture getTexture() {
        return texture;
    }

    public Sprite getSprite(String name) {
        return spritePerName.get(name);
    }

    public SpriteAnimation getAnimation(String name) {
        return animations.get(name);
    }
}
