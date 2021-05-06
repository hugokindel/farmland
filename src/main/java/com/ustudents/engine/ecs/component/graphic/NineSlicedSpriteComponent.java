package com.ustudents.engine.ecs.component.graphic;

import com.ustudents.engine.ecs.Component;
import com.ustudents.engine.ecs.component.core.TransformComponent;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.graphic.NineSlicedSprite;
import com.ustudents.engine.graphic.Spritebatch;
import com.ustudents.engine.graphic.imgui.annotation.Viewable;
import org.joml.Vector2f;

@Viewable
public class NineSlicedSpriteComponent extends Component implements RenderableComponent {
    /** The color tint to apply on the texture. */
    @Viewable
    public Color tint;

    /** The texture. */
    @Viewable
    public NineSlicedSprite parts;

    /** The size. */
    @Viewable
    public Vector2f size;

    /** The origin. */
    @Viewable
    public Vector2f origin;

    /**
     * Class constructor.
     *
     * @param parts The sprite.
     */
    public NineSlicedSpriteComponent(NineSlicedSprite parts, Vector2f size) {
        this.parts = parts;
        this.size = size;
        this.tint = Color.WHITE;
        this.origin = new Vector2f();
    }

    public void setTint(Color tint) {
        this.tint = tint;
    }

    public void setParts(NineSlicedSprite parts) {
        this.parts = parts;
    }

    public void setSize(Vector2f size) {
        this.size = size;
    }

    public void setOrigin(Vector2f origin) {
        this.origin = origin;
    }

    @Override
    public void render(Spritebatch spritebatch, RendererComponent rendererComponent,
                       TransformComponent transformComponent) {
        Spritebatch.NineSlicedSpriteData nineSlicedSpriteData = new Spritebatch.NineSlicedSpriteData(parts,
                transformComponent.position, size);
        nineSlicedSpriteData.zIndex = rendererComponent.zIndex;
        nineSlicedSpriteData.tint = tint;
        nineSlicedSpriteData.rotation = transformComponent.rotation;
        nineSlicedSpriteData.scale = transformComponent.scale;
        nineSlicedSpriteData.origin = origin;

        spritebatch.drawNineSlicedSprite(nineSlicedSpriteData);
    }
}
