package com.ustudents.engine.ecs.system;

import com.ustudents.engine.ecs.Entity;
import com.ustudents.engine.ecs.Registry;
import com.ustudents.engine.ecs.System;
import com.ustudents.engine.ecs.component.TextComponent;
import com.ustudents.engine.graphic.Spritebatch;
import com.ustudents.engine.ecs.component.SpriteComponent;
import com.ustudents.engine.ecs.component.TransformComponent;

public class RenderSystem extends System {
    public RenderSystem(Registry registry) {
        super(registry);

        requireComponent(TransformComponent.class, 0);
        requireComponent(SpriteComponent.class, 0);

        requireComponent(TransformComponent.class, 1);
        requireComponent(TextComponent.class, 1);
    }

    @Override
    public void render() {
        if (getEntities().size() == 0) {
            return;
        }

        Spritebatch spriteBatch = getScene().getSpritebatch();

        spriteBatch.begin();

        for (Entity entity : getEntities()) {
            TransformComponent transformComponent = entity.getComponent(TransformComponent.class);

            if (entity.hasComponent(SpriteComponent.class)) {
                SpriteComponent spriteComponent = entity.getComponent(SpriteComponent.class);

                spriteBatch.draw(
                        spriteComponent.texture,
                        transformComponent.position,
                        spriteComponent.region,
                        spriteComponent.zIndex
                );
            } else if (entity.hasComponent(TextComponent.class)) {
                TextComponent textComponent = entity.getComponent(TextComponent.class);

                spriteBatch.drawText(
                        textComponent.text,
                        transformComponent.position,
                        textComponent.font
                );
            }
        }

        spriteBatch.end();
    }
}
