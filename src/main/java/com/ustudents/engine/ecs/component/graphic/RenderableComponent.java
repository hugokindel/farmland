package com.ustudents.engine.ecs.component.graphic;

import com.ustudents.engine.ecs.component.core.TransformComponent;
import com.ustudents.engine.graphic.Spritebatch;

public interface RenderableComponent {
    default void render(Spritebatch spritebatch, RendererComponent rendererComponent,
                        TransformComponent transformComponent) {

    }
}
