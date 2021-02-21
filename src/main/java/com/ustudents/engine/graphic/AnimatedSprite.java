package com.ustudents.engine.graphic;

import org.joml.Vector2f;

public class AnimatedSprite {
    public Spritesheet spritesheet;

    public SpriteAnimation currentAnimation;

    public float currentNeededDuration;

    public float currentDuration;

    public int currentFrame;

    public Sprite sprite;

    public Color tint;

    public Vector2f origin;

    public AnimatedSprite(Spritesheet sprite) {
        this(sprite, "default");
    }

    public AnimatedSprite(Spritesheet sprite, String animationName) {
        this.spritesheet = sprite;
        this.currentAnimation = spritesheet.getAnimation(animationName);
        this.currentFrame = 0;
        this.currentNeededDuration = currentAnimation.getFrames().get(currentFrame).getDuration();
        currentDuration = 0;
        this.sprite = currentAnimation.getFrames().get(currentFrame).getSprite();
        this.tint = Color.WHITE;
        this.origin = new Vector2f();
    }

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
}
