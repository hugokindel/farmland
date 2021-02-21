package com.ustudents.engine.graphic;

import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.core.json.annotation.JsonSerializableConstructor;
import org.joml.Vector4f;
import org.joml.Vector4i;

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

    @JsonSerializable
    private Map<String, Vector4f> sprites;

    private Texture texture;

    private Map<String, Sprite> spritePerName;

    private Map<String, SpriteAnimation> animations;

    @JsonSerializableConstructor
    public void deserialize(Map<String, Object> json) {
        sprites = new HashMap<>(sprites);
        spritePerName = new HashMap<>();
        texture = Resources.loadTexture(path);
        animations = new HashMap<>();

        for (Map.Entry<String, Vector4f> entry : sprites.entrySet()) {
            Map<String, Integer> value = (Map<String, Integer>)entry.getValue();
            sprites.put(entry.getKey(), new Vector4f(value.get("x"), value.get("y"), value.get("z"), value.get("w")));
        }

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
