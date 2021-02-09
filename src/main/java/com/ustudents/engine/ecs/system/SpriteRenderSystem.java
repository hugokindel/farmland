package com.ustudents.engine.ecs.system;

import com.ustudents.engine.ecs.Entity;
import com.ustudents.engine.ecs.Registry;
import com.ustudents.engine.ecs.System;
import com.ustudents.engine.graphic.Spritebatch;
import com.ustudents.engine.ecs.component.SpriteComponent;
import com.ustudents.engine.ecs.component.TransformComponent;

public class SpriteRenderSystem extends System {
    public SpriteRenderSystem(Registry registry) {
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
