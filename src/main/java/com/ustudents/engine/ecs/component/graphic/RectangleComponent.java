package com.ustudents.engine.ecs.component.graphic;

import com.ustudents.engine.ecs.Component;
import com.ustudents.engine.ecs.component.core.TransformComponent;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.graphic.Spritebatch;
import com.ustudents.engine.graphic.imgui.annotation.Viewable;
import org.joml.Vector2f;

@Viewable
public class RectangleComponent extends Component implements RenderableComponent {
    /** The size. */
    @Viewable
    public Vector2f size;

    /** The color. */
    @Viewable
    public Color color;

    /** The thickness of the lines. */
    @Viewable
    public Integer thickness;

    /** The origin. */
    @Viewable
    public Vector2f origin;

    /** Defines if the rectangle is filled. */
    @Viewable
    public Boolean filled;

    /**
     * Class constructor.
     *
     * @param size The size.
     */
    public RectangleComponent(Vector2f size) {
        this.size = size;
        this.color = Color.WHITE;
        this.thickness = 1;
        this.origin = new Vector2f();
        this.filled = false;
    }

    public void setSize(Vector2f size) {
        this.size = size;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setThickness(Integer thickness) {
        this.thickness = thickness;
    }

    public void setOrigin(Vector2f origin) {
        this.origin = origin;
    }

    public void setFilled(Boolean filled) {
        this.filled = filled;
    }

    @Override
    public void render(Spritebatch spritebatch, RendererComponent rendererComponent,
                       TransformComponent transformComponent) {
        Spritebatch.RectangleData rectangleData = new Spritebatch.RectangleData(transformComponent.position, size);
        rectangleData.zIndex = rendererComponent.zIndex;
        rectangleData.color = color;
        rectangleData.rotation = transformComponent.rotation;
        rectangleData.scale = transformComponent.scale;
        rectangleData.origin = origin;
        rectangleData.filled = filled;
        rectangleData.thickness = thickness;

        spritebatch.drawRectangle(rectangleData);
    }
}
