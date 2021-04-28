package com.ustudents.farmland.core.player;

import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.graphic.Color;

@JsonSerializable
public class Avatar {
    public static final Color DEFAULT_BRACES_COLOR = new Color(0.7098f, 0.2745f, 0.3921f, 1f);
    public static final Color DEFAULT_SHIRT_COLOR = new Color(0.2627f, 0.5607f, 0.4392f, 1f);
    public static final Color DEFAULT_HAT_COLOR = new Color(0.7098f, 0.2745f, 0.3921f, 1f);
    public static final Color DEFAULT_BUTTONS_COLOR = new Color(0.9843f, 0.7764f, 0.2117f, 1f);

    @JsonSerializable
    public Color bracesColor;

    @JsonSerializable
    public Color shirtColor;

    @JsonSerializable
    public Color hatColor;

    @JsonSerializable
    public Color buttonsColor;

    public Avatar() {

    }

    public Avatar(Color bracesColor, Color shirtColor, Color hatColor, Color buttonsColor) {
        this.bracesColor = bracesColor;
        this.shirtColor = shirtColor;
        this.hatColor = hatColor;
        this.buttonsColor = buttonsColor;
    }
}
