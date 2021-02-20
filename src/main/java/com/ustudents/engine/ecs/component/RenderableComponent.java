package com.ustudents.engine.ecs.component;

import com.ustudents.engine.graphic.Spritebatch;

public interface RenderableComponent {
    default void render(Spritebatch spritebatch, RendererComponent rendererComponent,
                        TransformComponent transformComponent) {

    }
}
