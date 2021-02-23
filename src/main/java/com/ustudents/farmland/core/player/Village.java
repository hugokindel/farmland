package com.ustudents.farmland.core.player;

import com.ustudents.engine.core.json.annotation.JsonSerializable;
import org.joml.Vector2f;

@JsonSerializable
public class Village {
    @JsonSerializable
    public String name;

    @JsonSerializable
    public Vector2f position;

    public Village() {

    }

    public Village(String name) {
        this.name = name;
    }

    public void setPosition(Vector2f position) {
        this.position = position;
    }
}
