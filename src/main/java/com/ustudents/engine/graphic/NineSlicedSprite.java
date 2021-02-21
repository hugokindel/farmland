package com.ustudents.engine.graphic;

import com.ustudents.engine.graphic.imgui.annotation.Viewable;
import org.joml.Vector2f;

@Viewable
public class NineSlicedSprite {
    @Viewable
    public Sprite topLeft;

    @Viewable
    public Sprite topMiddle;

    @Viewable
    public Sprite topRight;

    @Viewable
    public Sprite middleLeft;

    @Viewable
    public Sprite middle;

    @Viewable
    public Sprite middleRight;

    @Viewable
    public Sprite bottomLeft;

    @Viewable
    public Sprite bottomMiddle;

    @Viewable
    public Sprite bottomRight;

    public NineSlicedSprite(Sprite topLeft, Sprite topMiddle, Sprite topRight, Sprite middleLeft, Sprite middle,
                            Sprite middleRight, Sprite bottomLeft, Sprite bottomMiddle, Sprite bottomRight) {
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

    public Vector2f getSizeForContent(Vector2f contentSize, Vector2f scale) {
        Vector2f realSize = new Vector2f(
                contentSize.x == 0 ? 1 : contentSize.x / scale.x,
                contentSize.y == 0 ? 1 : contentSize.y / scale.y
        );

        return new Vector2f(
                topLeft.getRegion().z + realSize.x + topRight.getRegion().z,
                topLeft.getRegion().w + realSize.y + bottomLeft.getRegion().w
        );
    }
}
