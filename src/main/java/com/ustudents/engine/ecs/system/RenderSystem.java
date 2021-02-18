package com.ustudents.engine.ecs.system;

import com.ustudents.engine.ecs.Entity;
import com.ustudents.engine.ecs.Registry;
import com.ustudents.engine.ecs.System;
import com.ustudents.engine.ecs.component.*;
import com.ustudents.engine.graphic.Spritebatch;

public abstract class RenderSystem extends System {
    public RenderSystem(Registry registry) {
        super(registry);

        requireComponent(TransformComponent.class);
        requireComponent(RenderableComponent.class);
    }

    protected void renderElement(Spritebatch spritebatch, Entity entity) {
        TransformComponent transformComponent = entity.getComponent(TransformComponent.class);
        RenderableComponent renderableComponent = entity.getComponent(RenderableComponent.class);

        if (entity.hasComponent(CircleComponent.class)) {
            CircleComponent circleComponent = entity.getComponent(CircleComponent.class);

            spritebatch.drawCircle(
                    transformComponent.position,
                    circleComponent.radius,
                    circleComponent.sides,
                    renderableComponent.zIndex,
                    circleComponent.color,
                    circleComponent.thickness
            );
        }

        if (entity.hasComponent(FilledRectangleComponent.class)) {
            FilledRectangleComponent filledRectangleComponent = entity.getComponent(FilledRectangleComponent.class);

            spritebatch.drawFilledRectangle(
                    transformComponent.position,
                    filledRectangleComponent.size,
                    renderableComponent.zIndex,
                    filledRectangleComponent.color,
                    transformComponent.rotation,
                    transformComponent.scale,
                    filledRectangleComponent.origin
            );
        }

        if (entity.hasComponent(LineComponent.class)) {
            LineComponent lineComponent = entity.getComponent(LineComponent.class);

            if (lineComponent.type == LineComponent.Type.FromLength) {
                spritebatch.drawLine(
                        transformComponent.position,
                        lineComponent.length,
                        transformComponent.rotation,
                        renderableComponent.zIndex,
                        lineComponent.color,
                        lineComponent.thickness
                );
            } else {
                spritebatch.drawLine(
                        transformComponent.position,
                        lineComponent.point2,
                        renderableComponent.zIndex,
                        lineComponent.color,
                        lineComponent.thickness
                );
            }
        }

        if (entity.hasComponent(PointComponent.class)) {
            PointComponent pointComponent = entity.getComponent(PointComponent.class);

            spritebatch.drawPoint(
                    transformComponent.position,
                    renderableComponent.zIndex,
                    pointComponent.color
            );
        }

        if (entity.hasComponent(RectangleComponent.class)) {
            RectangleComponent rectangleComponent = entity.getComponent(RectangleComponent.class);

            spritebatch.drawRectangle(
                    transformComponent.position,
                    rectangleComponent.size,
                    renderableComponent.zIndex,
                    rectangleComponent.color,
                    transformComponent.rotation,
                    rectangleComponent.thickness,
                    transformComponent.scale,
                    rectangleComponent.origin
            );
        }

        if (entity.hasComponent(SpriteComponent.class)) {
            SpriteComponent spriteComponent = entity.getComponent(SpriteComponent.class);

            spritebatch.drawTexture(
                    spriteComponent.sprite.getTexture(),
                    transformComponent.position,
                    spriteComponent.sprite.getRegion(),
                    renderableComponent.zIndex,
                    spriteComponent.tint,
                    transformComponent.rotation,
                    transformComponent.scale,
                    spriteComponent.origin
            );
        }

        if (entity.hasComponent(TextComponent.class)) {
            TextComponent textComponent = entity.getComponent(TextComponent.class);

            spritebatch.drawText(
                    textComponent.text,
                    textComponent.font,
                    transformComponent.position,
                    renderableComponent.zIndex,
                    textComponent.color
            );
        }

        if (entity.hasComponent(TextureComponent.class)) {
            TextureComponent textureComponent = entity.getComponent(TextureComponent.class);

            spritebatch.drawTexture(
                    textureComponent.texture,
                    transformComponent.position,
                    textureComponent.region,
                    renderableComponent.zIndex,
                    textureComponent.tint,
                    transformComponent.rotation,
                    transformComponent.scale,
                    textureComponent.origin
            );
        }
    }
}
