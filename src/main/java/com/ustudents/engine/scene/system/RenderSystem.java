package com.ustudents.engine.scene.system;

import com.ustudents.engine.scene.ecs.Entity;
import com.ustudents.engine.scene.ecs.System;
import com.ustudents.engine.scene.component.graphics.RenderableComponent;
import com.ustudents.engine.scene.component.graphics.RendererComponent;
import com.ustudents.engine.scene.component.core.TransformComponent;
import com.ustudents.engine.graphic.Spritebatch;

public abstract class RenderSystem extends System {
    public RenderSystem() {
        requireComponent(TransformComponent.class);
    }

    protected <T extends RendererComponent> void renderElement(Spritebatch spritebatch, Entity entity, Class<T> rendererType) {
        TransformComponent transformComponent = entity.getComponent(TransformComponent.class);
        RendererComponent rendererComponent = entity.getComponent(rendererType);

        for (RenderableComponent renderableComponent : entity.getRenderableComponents()) {
            renderableComponent.render(spritebatch, rendererComponent, transformComponent);
        }
    }
}
