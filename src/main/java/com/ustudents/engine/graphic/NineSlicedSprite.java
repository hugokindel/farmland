package com.ustudents.engine.graphic;

import org.joml.Vector2f;

public class NineSlicedSprite {
    public Sprite topLeft;
    public Sprite topMiddle;
    public Sprite topRight;
    public Sprite middleLeft;
    public Sprite middle;
    public Sprite middleRight;
    public Sprite bottomLeft;
    public Sprite bottomMiddle;
    public Sprite bottomRight;

    public NineSlicedSprite(Sprite topLeft, Sprite topMiddle, Sprite topRight, Sprite middleLeft, Sprite middle, Sprite middleRight, Sprite bottomLeft, Sprite bottomMiddle, Sprite bottomRight) {
        this.topLeft = topLeft;
        this.topMiddle = topMiddle;
        this.topRight = topRight;
        this.middleLeft = middleLeft;
        this.middle = middle;
        this.middleRight = middleRight;
        this.bottomLeft = bottomLeft;
        this.bottomMiddle = bottomMiddle;
        this.bottomRight = bottomRight;
    }

    public NineSlicedSprite(Spritesheet spritesheet) {
        this.topLeft = spritesheet.getSprite("topLeft");
        this.topMiddle = spritesheet.getSprite("topMiddle");
        this.topRight = spritesheet.getSprite("topRight");
        this.middleLeft = spritesheet.getSprite("middleLeft");
        this.middle = spritesheet.getSprite("middle");
        this.middleRight = spritesheet.getSprite("middleRight");
        this.bottomLeft = spritesheet.getSprite("bottomLeft");
        this.bottomMiddle = spritesheet.getSprite("bottomMiddle");
        this.bottomRight = spritesheet.getSprite("bottomRight");
    }

    public Vector2f getCompleteNeededSize(Vector2f size) {
        Vector2f realSize = new Vector2f(size.x == 0 ? 1 : size.x, size.y == 0 ? 1 : size.y);

        return new Vector2f(
                topLeft.getRegion().z + realSize.x + topRight.getRegion().z,
                topLeft.getRegion().w + realSize.y + bottomLeft.getRegion().w
        );
    }
}
