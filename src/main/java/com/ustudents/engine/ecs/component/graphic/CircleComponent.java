package com.ustudents.engine.ecs.component.graphic;

import com.ustudents.engine.ecs.Component;
import com.ustudents.engine.ecs.component.core.TransformComponent;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.graphic.Spritebatch;
import com.ustudents.engine.graphic.imgui.annotation.Viewable;

@Viewable
public class CircleComponent extends Component implements RenderableComponent {
    /** The radius. */
    @Viewable
    public Float radius;

    /** The number of sides to render. */
    @Viewable
    public Integer sides;

    /** The color. */
    @Viewable
    public Color color;

    /** The thickness of the lines. */
    @Viewable
    public Integer thickness;

    /**
     * Class constructor.
     *
     * @param radius The radius.
     * @param sides The sides.
     */
    public CircleComponent(Float radius, Integer sides) {
        this.radius = radius;
        this.sides = sides;
        this.color = Color.WHITE;
        this.thickness = 1;
    }

    /**
     * Changes the circle radius.
     *
     * @param radius The radius.
     */
    public void setRadius(Float radius) {
        this.radius = radius;
    }

    /**
     * Changes the number of sides.
     *
     * @param sides The sides.
     */
    public void setSides(Integer sides) {
        this.sides = sides;
    }

    /**
     * Changes the color.
     *
     * @param color The color.
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Changes the thickness.
     *
     * @param thickness The thickness.
     */
    public void setThickness(Integer thickness) {
        this.thickness = thickness;
    }

    @Override
    public void render(Spritebatch spritebatch, RendererComponent rendererComponent,
                       TransformComponent transformComponent) {
        Spritebatch.CircleData circleData = new Spritebatch.CircleData(transformComponent.position, radius, sides);
        circleData.zIndex = rendererComponent.zIndex;
        circleData.color = color;
        circleData.thickness = thickness;

        spritebatch.drawCircle(circleData);
    }
}
