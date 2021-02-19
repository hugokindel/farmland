package com.ustudents.engine.ecs.system;

import com.ustudents.engine.ecs.Entity;
import com.ustudents.engine.ecs.Registry;
import com.ustudents.engine.ecs.System;
import com.ustudents.engine.ecs.component.*;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.graphic.Spritebatch;
import com.ustudents.farmland.Farmland;
import org.joml.Vector2f;
import org.joml.Vector4f;

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

            Vector2f textSize = new Vector2f(
                    buttonComponent.textComponent.font.getTextWidth(buttonComponent.textComponent.text),
                    buttonComponent.textComponent.font.getTextHeight(buttonComponent.textComponent.text)
            );

            Vector2f buttonSize = buttonComponent.nineSlicedSpriteComponent.sprite.getCompleteNeededSize(textSize);

            spritebatch.drawNineSlicedSprite(
                    buttonComponent.nineSlicedSpriteComponent.sprite,
                    transformComponent.position,
                    textSize,
                    renderableComponent.zIndex,
                    Color.WHITE,
                    transformComponent.rotation,
                    transformComponent.scale,
                    new Vector2f(buttonSize.x / 2, buttonSize.y / 2)
            );

            spritebatch.drawText(
                    buttonComponent.textComponent.text,
                    buttonComponent.textComponent.font,
                    transformComponent.position,
                    renderableComponent.zIndex,
                    Color.WHITE,
                    transformComponent.rotation,
                    transformComponent.scale,
                    new Vector2f(
                            buttonComponent.textComponent.font.getTextWidth(buttonComponent.textComponent.text) / 2,
                            buttonComponent.textComponent.font.getTextHeight(buttonComponent.textComponent.text) / 2
                    )
            );

            /*if (Farmland.get().isDebugTexts()) {
                Vector4f buttonViewRect = new Vector4f(
                        transformComponent.position.x - (buttonSize.x / 2),
                        transformComponent.position.y - (buttonSize.y / 2),
                        (transformComponent.position.x - (buttonSize.x / 2)) + buttonSize.x,
                        (transformComponent.position.y - (buttonSize.y / 2)) + buttonSize.x
                );

                spritebatch.drawFilledRectangle(
                        new Vector2f(buttonViewRect.x, buttonViewRect.y),
                        new Vector2f(buttonSize.x, buttonSize.y),
                        renderableComponent.zIndex,
                        new Color(0, 1, 0, 0.3f)
                );
            }*/
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
                    spriteComponent.sprite,
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
