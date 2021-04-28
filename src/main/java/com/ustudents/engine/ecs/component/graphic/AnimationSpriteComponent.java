package com.ustudents.engine.ecs.component.graphic;

import com.ustudents.engine.ecs.component.core.BehaviourComponent;
import com.ustudents.engine.ecs.component.core.TransformComponent;
import com.ustudents.engine.graphic.*;
import com.ustudents.engine.graphic.imgui.annotation.Viewable;
import org.joml.Vector2f;

/** Component for a sprite to show on screen. */
@Viewable
public class AnimationSpriteComponent extends BehaviourComponent implements RenderableComponent {
    /** The sprite. */
    @Viewable
    public Spritesheet spritesheet;

    private SpriteAnimation currentAnimation;

    private float currentNeededDuration;

    private float currentDuration;

    private int currentFrame;

    private Sprite sprite;

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
    public AnimationSpriteComponent(Spritesheet sprite, String animationName) {
        this.spritesheet = sprite;
        this.currentAnimation = spritesheet.getAnimation(animationName);
        this.currentFrame = 0;
        this.currentNeededDuration = currentAnimation.getFrames().get(currentFrame).getDuration();
        currentDuration = 0;
        this.sprite = currentAnimation.getFrames().get(currentFrame).getSprite();
        this.tint = Color.WHITE;
        this.origin = new Vector2f();
    }

    /**
     * Sets the new sprite.
     *
     */
    public void setSpritesheet(Spritesheet spritesheet) {
        this.spritesheet = spritesheet;
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
    public void update(float dt) {
        currentDuration += dt;

        if (currentDuration >= currentNeededDuration) {
            if (currentFrame != currentAnimation.getFrames().size() - 1) {
                currentFrame++;
            } else if (currentAnimation.isLoopEnabled()) {
                currentFrame = 0;
            }

            currentNeededDuration = currentAnimation.getFrames().get(currentFrame).getDuration();
            sprite = currentAnimation.getFrames().get(currentFrame).getSprite();
            currentDuration = 0;
        }
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
