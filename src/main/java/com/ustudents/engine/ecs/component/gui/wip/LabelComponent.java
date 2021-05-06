package com.ustudents.engine.ecs.component.gui.wip;

import com.ustudents.engine.ecs.component.core.TransformComponent;
import com.ustudents.engine.ecs.component.graphic.RenderableComponent;
import com.ustudents.engine.ecs.component.graphic.RendererComponent;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.graphic.Font;
import com.ustudents.engine.graphic.Spritebatch;
import com.ustudents.engine.graphic.imgui.annotation.Viewable;
import org.joml.Vector2f;

@Viewable
public class LabelComponent extends GuiComponent implements RenderableComponent {
    /** The text. */
    @Viewable
    public String text;

    /** The font. */
    @Viewable
    public Font font;

    /** The color. */
    @Viewable
    public Color color;

    /** The text size. */
    @Viewable
    private Vector2f textSize;

    /** The text size. */
    @Viewable
    private Vector2f origin;

    /**
     * Class constructor.
     *
     * @param text The text.
     * @param font The font.
     */
    public LabelComponent(String text, Font font) {
        this.text = text;
        this.font = font;
        this.color = Color.WHITE;
        this.origin = new Vector2f();
        calculateTextSize();
    }

    public Vector2f getTextSize() {
        return textSize;
    }

    public void setText(String text) {
        this.text = text;
        calculateTextSize();
    }

    public void setFont(Font font) {
        this.font = font;
        calculateTextSize();
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setOrigin(Vector2f origin) {
        this.origin = origin;
    }

    private void calculateTextSize() {
        textSize = new Vector2f(
                font.getTextWidth(text),
                font.getTextHeight(text)
        );
    }

    @Override
    public void render(Spritebatch spritebatch, RendererComponent rendererComponent,
                       TransformComponent transformComponent) {
        Spritebatch.TextData textData = new Spritebatch.TextData(text, font, transformComponent.position);
        textData.zIndex = rendererComponent.zIndex;
        textData.color = color;
        textData.rotation = transformComponent.rotation;
        textData.scale = transformComponent.scale;
        textData.origin = origin;

        spritebatch.drawText(textData);
    }
}
