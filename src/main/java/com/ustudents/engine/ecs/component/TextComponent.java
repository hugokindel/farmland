package com.ustudents.engine.ecs.component;

import com.ustudents.engine.ecs.Component;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.graphic.Font;
import com.ustudents.engine.graphic.imgui.annotation.Editable;

public class TextComponent extends Component {
    /** The text. */
    @Editable
    public String text;

    /** The font. */
    @Editable
    public Font font;

    /** The color. */
    @Editable
    public Color color;

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
