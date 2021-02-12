package com.ustudents.engine.ecs.component;

import com.ustudents.engine.ecs.Component;
import com.ustudents.engine.graphic.Font;
import com.ustudents.engine.graphic.imgui.annotation.Editable;

public class TextComponent extends Component {
    @Editable
    public String text;

    @Editable
    public Font font;

    public TextComponent(String text, Font font) {
        this.text = text;
        this.font = font;
    }

    @Override
    public String toString() {
        return "TextComponent{" +
                "text='" + text + '\'' +
                ", font=" + font +
                '}';
    }
}
