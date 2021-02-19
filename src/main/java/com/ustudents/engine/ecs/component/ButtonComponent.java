package com.ustudents.engine.ecs.component;

import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.event.EventDispatcher;
import com.ustudents.engine.core.event.EventListener;
import com.ustudents.engine.graphic.Camera;
import com.ustudents.engine.graphic.NineSlicedSprite;
import com.ustudents.engine.graphic.imgui.annotation.Editable;
import com.ustudents.engine.input.Input;
import com.ustudents.engine.input.MouseButton;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class ButtonComponent extends BehaviourComponent {
    @Editable
    public LabelComponent label;

    @Editable
    public NineSlicedSpriteComponent sprite;

    private boolean focused;

    private boolean down;

    private boolean changeState;

    private Vector2f textOrigin;

    private Vector2f size;

    private EventDispatcher event;

    public ButtonComponent(String label) {
        this(label, null);
    }

    public ButtonComponent(String label, EventListener listener) {
        this(
                new LabelComponent(
                        label,
                        Resources.loadFont("ui/default.ttf", 16)
                )
        );

        addListener(listener);
    }

    public ButtonComponent(LabelComponent label) {
        this(
                label,
                new NineSlicedSpriteComponent(
                        new NineSlicedSprite(
                                Resources.loadSpritesheet("ui/button_default.json")),
                        new Vector2f(label.font.getTextWidth(label.text),
                                label.font.getTextHeight(label.text)
                        )
                )
        );
    }

    public ButtonComponent(LabelComponent label, NineSlicedSpriteComponent sprite) {
        this(label, sprite, null);
    }

    public ButtonComponent(LabelComponent label, NineSlicedSpriteComponent sprite, EventListener listener) {
        event = new EventDispatcher();

        if (listener != null) {
            event.add(listener);
        }

        Resources.loadSpritesheet("ui/button_default.json");
        Resources.loadSpritesheet("ui/button_focused.json");
        Resources.loadSpritesheet("ui/button_down.json");

        this.focused = false;
        this.changeState = false;
        this.label = label;
        this.sprite = sprite;

        Vector2f textSize = new Vector2f(
                label.font.getTextWidth(label.text),
                label.font.getTextHeight(label.text)
        );

        Vector2f buttonSize = sprite.parts.getCompleteNeededSize(textSize);

        sprite.origin = new Vector2f(buttonSize.x / 2, buttonSize.y / 2);

        textOrigin = new Vector2f(
                label.getTextSize().x / 2,
                label.getTextSize().y / 2
        );

        size = new Vector2f(
                sprite.parts.topLeft.getRegion().z + label.getTextSize().x + sprite.parts.topRight.getRegion().z,
                sprite.parts.topLeft.getRegion().w + label.getTextSize().y + sprite.parts.bottomLeft.getRegion().w
        );
    }

    @Override
    public void update(float dt) {
        TransformComponent comp = entity.getComponent(TransformComponent.class);
        Camera camera = getRelatedCamera();
        Vector2f cursorPos = Input.getMousePos();
        Vector2f buttonPos = camera.worldCoordToScreenCoord(entity.getComponent(TransformComponent.class).position);
        Vector2f realButtonSize = new Vector2f(size.x * comp.scale.x, size.y * comp.scale.y);
        Vector2f realButtonPos = new Vector2f(buttonPos.x, buttonPos.y);
        Vector4f buttonViewRect = new Vector4f(
                realButtonPos.x - (realButtonSize.x / 2),
                realButtonPos.y - (realButtonSize.y / 2),
                (realButtonPos.x - (realButtonSize.x / 2)) + realButtonSize.x,
                (realButtonPos.y - (realButtonSize.y / 2)) + realButtonSize.y
        );

        if (cursorPos.x > buttonViewRect.x && cursorPos.x < buttonViewRect.z && cursorPos.y > buttonViewRect.y && cursorPos.y < buttonViewRect.w) {
            if (!focused) {
                changeState = true;
                focused = true;
            }
        } else {
            if (down) {
                changeState = true;
                down = false;
                focused = false;
            } else if (focused) {
                changeState = true;
                focused = false;
            }
        }

        if (focused) {
            if (!down && Input.isMouseDown(MouseButton.Left)) {
                event.dispatch();
                down = true;
                changeState = true;
            } else if (down && !Input.isMouseDown(MouseButton.Left)) {
                down = false;
                changeState = true;
            }
        }

        if (changeState) {
            if (down) {
                sprite.parts = new NineSlicedSprite(Resources.getSpritesheet("ui/button_down.json"));
            } else if (focused) {
                sprite.parts = new NineSlicedSprite(Resources.getSpritesheet("ui/button_focused.json"));
            }  else {
                sprite.parts = new NineSlicedSprite(Resources.getSpritesheet("ui/button_default.json"));
            }
        }
    }

    public void addListener(EventListener listener) {
        if (listener != null) {
            event.add(listener);
        }
    }

    @Override
    public void render() {

    }

    public Vector2f getTextOrigin() {
        return textOrigin;
    }

    public Vector2f getSize() {
        return size;
    }
}
