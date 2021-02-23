package com.ustudents.engine.scene.component.graphics;

import com.ustudents.engine.scene.component.core.TransformComponent;
import com.ustudents.engine.graphic.Spritebatch;

public interface RenderableComponent {
    default void render(Spritebatch spritebatch, RendererComponent rendererComponent,
                        TransformComponent transformComponent) {

    }
}
