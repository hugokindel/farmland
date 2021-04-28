package com.ustudents.engine.ecs.component.graphic;

import com.ustudents.engine.ecs.Component;
import com.ustudents.engine.ecs.component.core.TransformComponent;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.graphic.Spritebatch;
import com.ustudents.engine.graphic.imgui.annotation.Viewable;

@Viewable
public class PointComponent extends Component implements RenderableComponent {
    /** The color. */
    @Viewable
    public Color color;

    /** Class constructor. */
    public PointComponent() {
        this.color = Color.WHITE;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public void render(Spritebatch spritebatch, RendererComponent rendererComponent,
                       TransformComponent transformComponent) {
        Spritebatch.PointData pointData = new Spritebatch.PointData(transformComponent.position);
        pointData.zIndex = rendererComponent.zIndex;
        pointData.color = color;

        spritebatch.drawPoint(pointData);
    }
}
