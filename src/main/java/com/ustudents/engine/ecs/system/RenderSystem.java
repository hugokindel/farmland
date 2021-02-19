package com.ustudents.engine.ecs.system;

import com.ustudents.engine.ecs.Entity;
import com.ustudents.engine.ecs.Registry;
import com.ustudents.engine.ecs.System;
import com.ustudents.engine.ecs.component.*;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.graphic.Spritebatch;
import org.joml.Vector2f;

public abstract class RenderSystem extends System {
    public RenderSystem(Registry registry) {
        super(registry);

        requireComponent(TransformComponent.class);
        requireComponent(RenderableComponent.class);
    }

    // TOOD: Optimize
    protected void renderElement(Spritebatch spritebatch, Entity entity) {
        TransformComponent transformComponent = entity.getComponent(TransformComponent.class);
        RenderableComponent renderableComponent = entity.getComponent(RenderableComponent.class);

        if (entity.hasComponent(ButtonComponent.class)) {
            ButtonComponent buttonComponent = entity.getComponent(ButtonComponent.class);

            spritebatch.drawNineSlicedSprite(
                    buttonComponent.sprite.parts,
                    transformComponent.position,
                    buttonComponent.label.getTextSize(),
                    renderableComponent.zIndex,
                    Color.WHITE,
                    transformComponent.rotation,
                    transformComponent.scale,
                    buttonComponent.sprite.origin
            );

            spritebatch.drawText(
                    buttonComponent.label.text,
                    buttonComponent.label.font,
                    transformComponent.position,
                    renderableComponent.zIndex,
                    Color.WHITE,
                    transformComponent.rotation,
                    transformComponent.scale,
                    new Vector2f(
                            buttonComponent.getTextOrigin().x,
                            buttonComponent.getTextOrigin().y
                    )
            );
        }

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

        if (entity.hasComponent(NineSlicedSpriteComponent.class)) {
            NineSlicedSpriteComponent spriteComponent = entity.getComponent(NineSlicedSpriteComponent.class);

            spritebatch.drawNineSlicedSprite(
                    spriteComponent.parts,
                    transformComponent.position,
                    spriteComponent.size,
                    renderableComponent.zIndex,
                    spriteComponent.tint,
                    transformComponent.rotation,
                    transformComponent.scale,
                    spriteComponent.origin
            );
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

            spritebatch.drawSprite(
                    spriteComponent.sprite,
                    transformComponent.position,
                    renderableComponent.zIndex,
                    spriteComponent.tint,
                    transformComponent.rotation,
                    transformComponent.scale,
                    spriteComponent.origin
            );
        }

        if (entity.hasComponent(LabelComponent.class)) {
            LabelComponent labelComponent = entity.getComponent(LabelComponent.class);

            spritebatch.drawText(
                    labelComponent.text,
                    labelComponent.font,
                    transformComponent.position,
                    renderableComponent.zIndex,
                    labelComponent.color
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
