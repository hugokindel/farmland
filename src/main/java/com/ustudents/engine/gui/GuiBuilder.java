package com.ustudents.engine.gui;

import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.Window;
import com.ustudents.engine.core.event.EventListener;
import com.ustudents.engine.ecs.Entity;
import com.ustudents.engine.ecs.Registry;
import com.ustudents.engine.ecs.component.core.TransformComponent;
import com.ustudents.engine.ecs.component.graphics.UiRendererComponent;
import com.ustudents.engine.ecs.component.gui.ButtonComponent;
import com.ustudents.engine.graphic.Anchor;
import com.ustudents.engine.graphic.Font;
import com.ustudents.engine.graphic.Origin;
import com.ustudents.engine.scene.Scene;
import com.ustudents.engine.scene.SceneManager;
import com.ustudents.farmland.Farmland;
import org.joml.Vector2f;
import org.joml.Vector2i;

public class GuiBuilder {
    public class TextData {
        String text;

        Font font;
    }

    public static class ButtonData {
        public String text;

        public String id;

        public Font font;

        public Vector2f position;

        public Origin origin;

        public Anchor anchor;

        public EventListener listener;

        public ButtonData(String text, EventListener listener) {
            this.text = text;
            this.listener = listener;
            this.font = Resources.loadFont("ui/default.ttf", 16);
            this.origin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Left);
            this.anchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Left);
        }
    }

    Scene scene = SceneManager.getScene();

    Registry registry = SceneManager.getScene().getRegistry();

    Entity canvas;

    Font font = Resources.loadFont("ui/default.ttf", 16);

    Vector2f globalScale;

    public GuiBuilder() {
        canvas = registry.createEntityWithName("canvas");
        globalScale = new Vector2f(3.0f, 3.0f);
    }

    public void addButton(ButtonData data) {
        Entity button = canvas.createChildWithName(data.id);

        TransformComponent transformComponent = new TransformComponent();

        transformComponent.position = data.position;
        transformComponent.scale = globalScale;

        switch (data.origin.horizontal) {
            case Custom:
                transformComponent.position.x += data.origin.customHorizontal;
                break;
            case Left:
                transformComponent.position.x += (5 + data.font.getTextWidth(data.text) / 2) * globalScale.x;
                break;
            case Center:
                break;
            case Right:
                transformComponent.position.x -= (5 + data.font.getTextWidth(data.text) / 2) * globalScale.x;
                break;
        }

        switch (data.origin.vertical) {
            case Custom:
                transformComponent.position.x += data.origin.customVertical;
                break;
            case Top:
                transformComponent.position.y += (5 + data.font.getTextHeight(data.text) / 2) * globalScale.x;
                break;
            case Middle:
                break;
            case Bottom:
                transformComponent.position.y -= (5 + data.font.getTextHeight(data.text) / 2) * globalScale.x;
                break;
        }

        Vector2i windowSize = Window.get().getSize();

        switch (data.anchor.horizontal) {
            case Left:
                break;
            case Center:
                transformComponent.position.x += (float)windowSize.x / 2;
                break;
            case Right:
                transformComponent.position.x += (float)windowSize.x;
                break;
        }

        switch (data.anchor.vertical) {

            case Top:
                break;
            case Middle:
                transformComponent.position.y += (float)windowSize.y / 2;
                break;
            case Bottom:
                transformComponent.position.y += (float)windowSize.y;
                break;
        }

        button.addComponent(transformComponent);
        button.addComponent(new UiRendererComponent());
        button.addComponent(new ButtonComponent(data.text, data.listener));

        /*button.addComponent(new TransformComponent(
                new Vector2f(size.x - 120, size.y - 35), new Vector2f(3.1f, 3.1f)));
        button.addComponent(new ButtonComponent("Finir le tour"));
        Farmland.get().getWindow().sizeChanged.add((dataType, data) -> {
            Window.SizeChangedEventData event = (Window.SizeChangedEventData) data;
            button.getComponent(TransformComponent.class).setPosition(new Vector2f(event.newSize.x - 160, event.newSize.y - 140));
        });
        button.addComponent(new UiRendererComponent());*/
    }

    public void end() {

    }
}
