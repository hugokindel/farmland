package com.ustudents.engine.ecs.component;

import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.ecs.Component;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.graphic.Font;
import com.ustudents.engine.graphic.imgui.annotation.Editable;
import org.joml.Vector2f;

@JsonSerializable
public class TextComponent extends Component {
    /** The text. */
    @JsonSerializable
    @Editable
    public String text;

    /** The font. */
    @JsonSerializable
    @Editable
    public Font font;

    /** The color. */
    @JsonSerializable
    @Editable
    public Color color;

    public TextComponent() {
        this(null, null);
    }

    /**
     * Class constructor.
     *
     * @param text The text.
     * @param font The font.
     */
    public TextComponent(String text, Font font) {
        this(text, font, Color.WHITE);
    }

    /**
     * Class constructor.
     *
     * @param text The text.
     * @param font The font.
     * @param color The color.
     */
    public TextComponent(String text, Font font, Color color) {
        this.text = text;
        this.font = font;
        this.color = color;
    }
}
