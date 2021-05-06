package com.ustudents.engine.ecs.component.graphic;

import com.ustudents.engine.ecs.Component;
import com.ustudents.engine.ecs.component.core.TransformComponent;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.graphic.Spritebatch;
import com.ustudents.engine.graphic.imgui.annotation.Viewable;
import org.joml.Vector2f;

@Viewable
public class LineComponent extends Component implements RenderableComponent {
    /** The color. */
    @Viewable
    public Color color;

    /** The thickness. */
    @Viewable
    public Integer thickness;

    /** The point. */
    @Viewable
    public Vector2f point2;

    /**
     * Class constructor.
     *
     * @param point2 The point.
     */
    public LineComponent(Vector2f point2) {
        this.point2 = point2;
        this.color = Color.WHITE;
        this.thickness = 1;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setThickness(Integer thickness) {
        this.thickness = thickness;
    }

    public void setPoint2(Vector2f point2) {
        this.point2 = point2;
    }

    @Override
    public void render(Spritebatch spritebatch, RendererComponent rendererComponent,
                       TransformComponent transformComponent) {
        Spritebatch.LineData lineData = new Spritebatch.LineData(transformComponent.position, point2);
        lineData.zIndex = rendererComponent.zIndex;
        lineData.color = color;
        lineData.thickness = thickness;

        spritebatch.drawLine(lineData);
    }
}
