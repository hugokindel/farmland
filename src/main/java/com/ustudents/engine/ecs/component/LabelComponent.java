package com.ustudents.engine.ecs.component;

import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.ecs.Component;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.graphic.Font;
import com.ustudents.engine.graphic.imgui.annotation.Editable;
import org.joml.Vector2f;

@Editable
@JsonSerializable
public class LabelComponent extends Component {
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

    private Vector2f textSize;

    public LabelComponent() {
        this(null, null);
    }

    /**
     * Class constructor.
     *
     * @param text The text.
     * @param font The font.
     */
    public LabelComponent(String text, Font font) {
        this(text, font, Color.WHITE);
    }

    /**
     * Class constructor.
     *
     * @param text The text.
     * @param font The font.
     * @param color The color.
     */
    public LabelComponent(String text, Font font, Color color) {
        this.text = text;
        this.font = font;
        this.color = color;

        textSize = new Vector2f(
                font.getTextWidth(text),
                font.getTextHeight(text)
        );
    }

    public Vector2f getTextSize() {
        return textSize;
    }
}
