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
    @JsonSerializable
    private String texturePath;

    @JsonSerializable
    private Map<String, Vector4f> spriteRegions;

    private Texture texture;

    private Map<String, Sprite> sprites;

    @JsonSerializableConstructor
    public void deserialize() {
        spriteRegions = new HashMap<>(spriteRegions);
        sprites = new HashMap<>();
        texture = Resources.loadTexture(texturePath);

        for (Map.Entry<String, Vector4f> entry : spriteRegions.entrySet()) {
            Map<String, Integer> value = (Map<String, Integer>)entry.getValue();
            spriteRegions.put(entry.getKey(), new Vector4f(value.get("x"), value.get("y"), value.get("z"), value.get("w")));
        }

        for (Map.Entry<String, Vector4f> entry : spriteRegions.entrySet()) {
            sprites.put(entry.getKey(), new Sprite(texture, entry.getValue()));
        }
    }

    public Texture getTexture() {
        return texture;
    }

    public Sprite getSprite(String name) {
        return sprites.get(name);
    }
}
