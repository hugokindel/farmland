package com.ustudents.engine.ecs.component.gui;

import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.event.EventDispatcher;
import com.ustudents.engine.core.event.EventListener;
import com.ustudents.engine.ecs.component.core.BehaviourComponent;
import com.ustudents.engine.ecs.component.core.TransformComponent;
import com.ustudents.engine.ecs.component.graphic.*;
import com.ustudents.engine.graphic.*;
import com.ustudents.engine.graphic.imgui.annotation.Viewable;
import com.ustudents.engine.input.Input;
import com.ustudents.engine.input.MouseButton;
import org.joml.Vector2f;
import org.joml.Vector4f;

// TODO: Optimize & refactor
@Viewable
@SuppressWarnings("unchecked")
public class ButtonComponent extends BehaviourComponent implements RenderableComponent {
    @Viewable
    public TextComponent label;

    @Viewable
    public NineSlicedSpriteComponent sprite;

    @Viewable
    public Boolean bypassDisableInput = false;

    private boolean focused;

    private boolean down;

    private boolean changeState;

    private Vector2f textOrigin;

    private Vector2f size;

    private EventDispatcher event;

    private Vector2f textSize;

    public static boolean disableInput;

    public ButtonComponent(String label) {
        this(label, null);
    }

    public ButtonComponent(String label, EventListener listener) {
        this(
                new TextComponent(
                        label,
                        Resources.loadFont("ui/default.ttf", 16)
                )
        );

        addListener(listener);
    }

    public ButtonComponent(TextComponent label) {
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

    public ButtonComponent(TextComponent label, NineSlicedSpriteComponent sprite) {
        this(label, sprite, null);
    }

    public ButtonComponent(TextComponent label, NineSlicedSpriteComponent sprite, EventListener listener) {
        event = new EventDispatcher();

        if (listener != null) {
            event.add(listener);
        }

        this.focused = false;
        this.changeState = false;
        this.label = label;
        this.sprite = sprite;
    }

    @Override
    public void initialize() {
        TransformComponent transformComponent = getEntity().getComponent(TransformComponent.class);

        Vector2f textViewRect = label.font.getScaledTextSize(label.text, transformComponent.scale);
        label.setEntity(getEntity());
        textSize = new Vector2f(
                label.font.getTextWidth(label.text),
                label.font.getTextHeight(label.text)
        );

        Vector2f buttonSize = sprite.parts.getSizeForContent(textSize, new Vector2f(1, 1));

        sprite.origin = new Vector2f(0, 0);

        textOrigin = new Vector2f(
                sprite.origin.x - 5,
                sprite.origin.y - 5
        );

        size = new Vector2f(
                10 * transformComponent.scale.x + textViewRect.x,
                10 * transformComponent.scale.y + textViewRect.y
        );
    }

    @Override
    public void update(float dt) {
        TransformComponent comp = getEntity().getComponent(TransformComponent.class);
        Camera camera = getWorldCamera();
        Vector2f cursorPos = Input.getMousePos();
        Vector2f buttonPos = camera.worldCoordToScreenCoord(getEntity().getComponent(TransformComponent.class).position);
        Vector2f realButtonSize = new Vector2f(size.x, size.y);
        Vector2f realButtonPos = new Vector2f(comp.position.x, comp.position.y);
        Vector4f buttonViewRect = new Vector4f(
                realButtonPos.x,
                realButtonPos.y,
                realButtonPos.x + realButtonSize.x,
                realButtonPos.y + realButtonSize.y
        );

        if (!bypassDisableInput && disableInput && !changeState) {
            focused = false;
            down = false;
            changeState = true;
        }

        if (cursorPos.x > buttonViewRect.x && cursorPos.x < buttonViewRect.z && cursorPos.y > buttonViewRect.y && cursorPos.y < buttonViewRect.w) {
            if (!focused) {
                if (bypassDisableInput || !disableInput) {
                    changeState = true;
                    focused = true;
                }
            }
        } else {
            if (down) {
                if (bypassDisableInput || !disableInput) {
                    changeState = true;
                    down = false;
                    focused = false;
                }
            } else if (focused) {
                if (bypassDisableInput || !disableInput) {
                    changeState = true;
                    focused = false;
                }
            }
        }

        if (focused) {
            if (!down && Input.isActionSuccessful("putItem")){
                if (bypassDisableInput || !disableInput) {
                    down = true;
                    changeState = true;
                }
            } else if (down && !Input.isActionSuccessful("putItem")){
                if (bypassDisableInput || !disableInput) {
                    down = false;
                    event.dispatch();
                    changeState = true;
                }
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
            changeState = false;
        }
    }

    public void addListener(EventListener listener) {
        if (listener != null) {
            event.add(listener);
        }
    }

    @Override
    public void render(Spritebatch spritebatch, RendererComponent rendererComponent, TransformComponent transformComponent) {
        Spritebatch.NineSlicedSpriteData spriteData = new Spritebatch.NineSlicedSpriteData(sprite.parts,
                transformComponent.position, new Vector2f(label.getSize().x / transformComponent.scale.x, label.getSize().y / transformComponent.scale.y));
        spriteData.zIndex = rendererComponent.zIndex;
        spriteData.tint = Color.WHITE;
        spriteData.rotation = transformComponent.rotation;
        spriteData.scale = transformComponent.scale;
        spriteData.origin = new Vector2f(0, 0);

        spritebatch.drawNineSlicedSprite(spriteData);

        Spritebatch.TextData textData = new Spritebatch.TextData(label.text, label.font, transformComponent.position);
        textData.zIndex = rendererComponent.zIndex;
        textData.color = label.color;
        textData.rotation = transformComponent.rotation;
        textData.scale = transformComponent.scale;
        textData.origin = new Vector2f(getTextOrigin().x, getTextOrigin().y);

        spritebatch.drawText(textData);
    }

    public Vector2f getTextOrigin() {
        return textOrigin;
    }

    public Vector2f getOldSize() {
        return size;
    }

    public Vector2f getSize() {
        TransformComponent transformComponent = getEntity().getComponent(TransformComponent.class);
        Vector2f textSize = label.getSize();
        return new Vector2f(
                sprite.parts.topLeft.getRegion().z + sprite.parts.topRight.getRegion().z,
                sprite.parts.topLeft.getRegion().w + sprite.parts.topRight.getRegion().w
        ).mul(transformComponent.scale).add(textSize);
    }
}
