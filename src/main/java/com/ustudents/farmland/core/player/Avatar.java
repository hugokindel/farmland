package com.ustudents.farmland.core.player;

import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.graphic.Color;

@JsonSerializable
public class Avatar {
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
