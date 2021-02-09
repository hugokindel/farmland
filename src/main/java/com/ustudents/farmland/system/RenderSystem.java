package com.ustudents.farmland.system;

import com.ustudents.engine.core.ecs.Entity;
import com.ustudents.engine.core.ecs.Registry;
import com.ustudents.engine.core.ecs.System;
import com.ustudents.engine.graphic.Spritebatch;
import com.ustudents.farmland.component.SpriteComponent;
import com.ustudents.farmland.component.TransformComponent;

public class RenderSystem extends System {
    public RenderSystem(Registry registry) {
        super(registry);

        requireComponent(TransformComponent.class);
        requireComponent(SpriteComponent.class);
    }

    @Override
    public void render() {
        if (getEntities().size() == 0) {
            return;
        }

        Spritebatch spriteBatch = getScene().getSpriteBatch();

        spriteBatch.begin();

        for (Entity entity : getEntities()) {
            TransformComponent transformComponent = entity.getComponent(TransformComponent.class);
            SpriteComponent spriteComponent = entity.getComponent(SpriteComponent.class);

            spriteBatch.draw(
                    spriteComponent.texture,
                    spriteComponent.textureRegion,
                    transformComponent.position,
                    spriteComponent.zIndex
            );
        }

        spriteBatch.end();
    }
}
