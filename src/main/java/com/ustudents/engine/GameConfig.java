package com.ustudents.engine;

import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.core.window.Window;
import com.ustudents.engine.input.Action;
import org.joml.Vector2i;

import java.util.*;

@JsonSerializable
public class GameConfig {
    @JsonSerializable(necessary = false)
    public Vector2i windowedSize = new Vector2i(1280, 720);

    @JsonSerializable(necessary = false)
    public Window.Type windowType = Window.Type.Windowed;

    @JsonSerializable(necessary = false)
    public Boolean useVsync = true;

    @JsonSerializable(necessary = false)
    public String language = "fr";

    @JsonSerializable(necessary = false)
    public Map<String, Object> game = new LinkedHashMap<>();

    @JsonSerializable(necessary = false)
    public Map<String, Action> commands = new LinkedHashMap<>();
}
