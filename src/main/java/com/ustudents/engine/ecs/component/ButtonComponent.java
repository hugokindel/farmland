package com.ustudents.engine.ecs.component;

import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.ecs.Component;
import com.ustudents.engine.graphic.Camera;
import com.ustudents.engine.graphic.NineSlicedSprite;
import com.ustudents.engine.input.Input;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class ButtonComponent extends BehaviourComponent {
    public TextComponent textComponent;

    public NineSlicedSpriteComponent nineSlicedSpriteComponent;

    private boolean focused;

    private boolean changeState;

    public ButtonComponent(String text) {
        this(
                new TextComponent(
                        text,
                        Resources.loadFont("ui/default.ttf", 16)
                )
        );
    }

    public ButtonComponent(TextComponent textComponent) {
        this(
                textComponent,
                new NineSlicedSpriteComponent(
                        new NineSlicedSprite(
                                Resources.loadSpritesheet("ui/button_default.json")),
                        new Vector2f(textComponent.font.getTextWidth(textComponent.text),
                                textComponent.font.getTextHeight(textComponent.text)
                        )
                )
        );
    }

    public ButtonComponent(TextComponent textComponent, NineSlicedSpriteComponent nineSlicedSpriteComponent) {
        Resources.loadSpritesheet("ui/button_default.json");
        Resources.loadSpritesheet("ui/button_focused.json");
        Resources.loadSpritesheet("ui/button_clicked.json");

        this.focused = false;
        this.changeState = false;
        this.textComponent = textComponent;
        this.nineSlicedSpriteComponent = nineSlicedSpriteComponent;
    }

    @Override
    public void update(float dt) {
        Camera camera = getRelatedCamera();
        Vector2f cursorPos = Input.getMousePos();
        Vector2f buttonPos = camera.worldCoordToScreenCoord(entity.getComponent(TransformComponent.class).position);
        Vector2f textSize = new Vector2f(
                textComponent.font.getTextWidth(textComponent.text),
                textComponent.font.getTextHeight(textComponent.text)
        );
        Vector2f buttonSize = nineSlicedSpriteComponent.sprite.getCompleteNeededSize(textSize);
        Vector4f buttonViewRect = new Vector4f(
                buttonPos.x - (buttonSize.x / 2),
                buttonPos.y - (buttonSize.y / 2),
                (buttonPos.x - (buttonSize.x / 2)) + buttonSize.x,
                (buttonPos.y - (buttonSize.y / 2)) + buttonSize.y
        );

        if (cursorPos.x > buttonViewRect.x && cursorPos.x < buttonViewRect.z && cursorPos.y > buttonViewRect.y && cursorPos.y < buttonViewRect.w) {
            if (!focused) {
                changeState = true;
                focused = true;
            }
        } else {
            if (focused) {
                changeState = true;
                focused = false;
            }
        }

        if (changeState) {
            if (focused) {
                nineSlicedSpriteComponent.sprite = new NineSlicedSprite(Resources.getSpritesheet("ui/button_focused.json"));
            } else {
                nineSlicedSpriteComponent.sprite = new NineSlicedSprite(Resources.getSpritesheet("ui/button_default.json"));
            }
        }
    }

    @Override
    public void render() {

    }
}
