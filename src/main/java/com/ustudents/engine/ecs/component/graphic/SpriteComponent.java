package com.ustudents.engine.ecs.component.graphic;

import com.ustudents.engine.ecs.Component;
import com.ustudents.engine.ecs.component.core.TransformComponent;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.graphic.Sprite;
import com.ustudents.engine.graphic.Spritebatch;
import com.ustudents.engine.graphic.imgui.annotation.Viewable;
import org.joml.Vector2f;

/** Component for a sprite to show on screen. */
@Viewable
public class SpriteComponent extends Component implements RenderableComponent {
    /** The sprite. */
    @Viewable
    public Sprite sprite;

    /** The color tint to apply on the texture. */
    @Viewable
    public Color tint;

    /** The texture origin. */
    @Viewable
    public Vector2f origin;

    /**
     * Class constructor.
     *
     * @param sprite The sprite.
     */
    public SpriteComponent(Sprite sprite) {
        this.sprite = sprite;
        this.tint = Color.WHITE;
        this.origin = new Vector2f();
    }

    /**
     * Class constructor.
     *
     * @param sprite The sprite.
     * @param tint The color tint.
     */
    public SpriteComponent(Sprite sprite, Color tint) {
        this.sprite = sprite;
        this.tint = tint;
        this.origin = new Vector2f();
    }

    /**
     * Sets the new sprite.
     *
     * @param sprite The sprite.
     */
    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    /**
     * Sets the tint color.
     *
     * @param tint The new tint color.
     */
    public void setTint(Color tint) {
        this.tint = tint;
    }

    /**
     * Sets the texture origin.
     *
     * @param origin The new texture origin.
     */
    public void setOrigin(Vector2f origin) {
        this.origin = origin;
    }

    @Override
    public void render(Spritebatch spritebatch, RendererComponent rendererComponent,
                       TransformComponent transformComponent) {
        Spritebatch.SpriteData spriteData = new Spritebatch.SpriteData(sprite, transformComponent.position);
        spriteData.zIndex = rendererComponent.zIndex;
        spriteData.tint = tint;
        spriteData.rotation = transformComponent.rotation;
        spriteData.scale = transformComponent.scale;
        spriteData.origin = origin;

        spritebatch.drawSprite(spriteData);
    }
}
