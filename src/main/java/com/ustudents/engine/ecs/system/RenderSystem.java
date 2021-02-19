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

            spritebatch.drawNineSlicedSprite(new Spritebatch.NineSlicedSpriteData(
                    buttonComponent.sprite.parts,
                    transformComponent.position,
                    buttonComponent.label.getTextSize()
            ) {{
                zIndex = renderableComponent.zIndex;
                tint = Color.WHITE;
                rotation = transformComponent.rotation;
                scale = transformComponent.scale;
                origin = buttonComponent.sprite.origin;
            }});

            spritebatch.drawText(new Spritebatch.TextData(
                    buttonComponent.label.text,
                    buttonComponent.label.font,
                    transformComponent.position
            ) {{
                zIndex = renderableComponent.zIndex;
                color = buttonComponent.label.color;
                rotation = transformComponent.rotation;
                scale = transformComponent.scale;
                origin = new Vector2f(buttonComponent.getTextOrigin().x, buttonComponent.getTextOrigin().y);
            }});
        }

        if (entity.hasComponent(CircleComponent.class)) {
            CircleComponent circleComponent = entity.getComponent(CircleComponent.class);

            spritebatch.drawCircle(new Spritebatch.CircleData(
                    transformComponent.position,
                    circleComponent.radius,
                    circleComponent.sides
            ) {{
                zIndex = renderableComponent.zIndex;
                color = circleComponent.color;
                thickness = circleComponent.thickness;
            }});
        }

        if (entity.hasComponent(FilledRectangleComponent.class)) {
            FilledRectangleComponent filledRectangleComponent = entity.getComponent(FilledRectangleComponent.class);

            spritebatch.drawRectangle(new Spritebatch.RectangleData(
                    transformComponent.position,
                    filledRectangleComponent.size
            ) {{
                zIndex = renderableComponent.zIndex;
                color = filledRectangleComponent.color;
                rotation = transformComponent.rotation;
                scale = transformComponent.scale;
                origin = filledRectangleComponent.origin;
                filled = true;
            }});
        }

        if (entity.hasComponent(LineComponent.class)) {
            LineComponent lineComponent = entity.getComponent(LineComponent.class);

            spritebatch.drawLine(new Spritebatch.LineData(transformComponent.position, lineComponent.point2) {{
                zIndex = renderableComponent.zIndex;
                color = lineComponent.color;
                thickness = lineComponent.thickness;
            }});
        }

        if (entity.hasComponent(NineSlicedSpriteComponent.class)) {
            NineSlicedSpriteComponent spriteComponent = entity.getComponent(NineSlicedSpriteComponent.class);

            spritebatch.drawNineSlicedSprite(new Spritebatch.NineSlicedSpriteData(
                    spriteComponent.parts,
                    transformComponent.position,
                    spriteComponent.size
            ) {{
                zIndex = renderableComponent.zIndex;
                tint = spriteComponent.tint;
                rotation = transformComponent.rotation;
                scale = transformComponent.scale;
                origin = spriteComponent.origin;
            }});
        }

        if (entity.hasComponent(PointComponent.class)) {
            PointComponent pointComponent = entity.getComponent(PointComponent.class);

            spritebatch.drawPoint(new Spritebatch.PointData(transformComponent.position) {{
                zIndex = renderableComponent.zIndex;
                color = pointComponent.color;
            }});
        }

        if (entity.hasComponent(RectangleComponent.class)) {
            RectangleComponent rectangleComponent = entity.getComponent(RectangleComponent.class);

            spritebatch.drawRectangle(new Spritebatch.RectangleData(
                    transformComponent.position,
                    rectangleComponent.size
            ) {{
                zIndex = renderableComponent.zIndex;
                color = rectangleComponent.color;
                rotation = transformComponent.rotation;
                scale = transformComponent.scale;
                origin = rectangleComponent.origin;
                filled = true;
                thickness = rectangleComponent.thickness;
            }});
        }

        if (entity.hasComponent(SpriteComponent.class)) {
            SpriteComponent spriteComponent = entity.getComponent(SpriteComponent.class);

            spritebatch.drawSprite(new Spritebatch.SpriteData(spriteComponent.sprite, transformComponent.position) {{
                zIndex = renderableComponent.zIndex;
                tint = spriteComponent.tint;
                rotation = transformComponent.rotation;
                scale = transformComponent.scale;
                origin = spriteComponent.origin;
            }});
        }

        if (entity.hasComponent(LabelComponent.class)) {
            LabelComponent labelComponent = entity.getComponent(LabelComponent.class);

            spritebatch.drawText(new Spritebatch.TextData(
                    labelComponent.text,
                    labelComponent.font,
                    transformComponent.position
            ) {{
                zIndex = renderableComponent.zIndex;
                color = labelComponent.color;
            }});
        }

        if (entity.hasComponent(TextureComponent.class)) {
            TextureComponent textureComponent = entity.getComponent(TextureComponent.class);

            spritebatch.drawTexture(new Spritebatch.TextureData(
                    textureComponent.texture,
                    transformComponent.position
            ) {{
                region = textureComponent.region;
                zIndex = renderableComponent.zIndex;
                tint = textureComponent.tint;
                rotation = transformComponent.rotation;
                scale = transformComponent.scale;
                origin = textureComponent.origin;
            }});
        }
    }
}
