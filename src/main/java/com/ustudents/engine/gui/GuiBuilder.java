package com.ustudents.engine.gui;

import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.window.Window;
import com.ustudents.engine.core.event.EventListener;
import com.ustudents.engine.ecs.component.graphic.RectangleComponent;
import com.ustudents.engine.ecs.component.graphic.SpriteComponent;
import com.ustudents.engine.ecs.Entity;
import com.ustudents.engine.ecs.Registry;
import com.ustudents.engine.ecs.component.core.TransformComponent;
import com.ustudents.engine.ecs.component.graphic.NineSlicedSpriteComponent;
import com.ustudents.engine.ecs.component.graphic.UiRendererComponent;
import com.ustudents.engine.ecs.component.gui.ButtonComponent;
import com.ustudents.engine.ecs.component.gui.TextComponent;
import com.ustudents.engine.graphic.*;
import com.ustudents.engine.scene.Scene;
import com.ustudents.engine.scene.SceneManager;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;

@SuppressWarnings("unchecked")
public class GuiBuilder {
    public static class TextData {
        public String text;

        public String id;

        public Font font;

        public Vector2f position;

        public Vector2f scale;

        public Origin origin;

        public Anchor anchor;

        public Color color;

        public int zIndex;

        public boolean applyGlobalScaling;

        public TextData(String text) {
            this.text = text;
            this.id = "button";
            this.position = new Vector2f();
            this.scale = new Vector2f(1.0f, 1.0f);
            this.font = Resources.loadFont("ui/default.ttf", 16);
            this.origin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Left);
            this.anchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Left);
            this.color = Color.WHITE;
            this.zIndex = 0;
            this.applyGlobalScaling = true;
        }

        public static TextData copy(TextData data) {
            TextData textData = new TextData(data.text);
            textData.id = data.id;
            textData.position = data.position;
            textData.scale = data.scale;
            textData.font = data.font;
            textData.origin = data.origin;
            textData.anchor = data.anchor;
            textData.color = data.color;
            textData.applyGlobalScaling = data.applyGlobalScaling;
            return textData;
        }
    }

    public static class ButtonData {
        public String text;

        public String id;
        
        public String parentId;

        public Font font;

        public Vector2f position;

        public Vector2f scale;

        public Origin origin;

        public Anchor anchor;

        public EventListener listener;

        public boolean applyGlobalScaling;

        public int zIndex;

        public ButtonData(String text, EventListener listener) {
            this.text = text;
            this.id = "button";
            this.position = new Vector2f();
            this.listener = listener;
            this.scale = new Vector2f(1.0f, 1.0f);
            this.font = Resources.loadFont("ui/default.ttf", 16);
            this.origin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Left);
            this.anchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Left);
            this.applyGlobalScaling = true;
            this.zIndex = 0;
        }
    }

    public static class ImageData {
        public Texture texture;

        public String id;

        public Vector2f position;
        
        public String parentId;

        public Vector2f scale;

        public Vector4f region;

        public Origin origin;

        public Anchor anchor;

        public boolean applyGlobalScaling;

        public int zIndex;

        public Color tint;

        public ImageData(Texture texture) {
            this.texture = texture;
            this.id = "image";
            this.scale = new Vector2f(1.0f, 1.0f);
            this.applyGlobalScaling = false;
            this.position = new Vector2f();
            this.origin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Left);
            this.anchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Left);
            this.region = new Vector4f(0, 0, texture.getWidth(), texture.getHeight());
            this.zIndex = 0;
            this.tint = Color.WHITE;
        }
    }

    public static class RectangleData {
        public String id;

        public Vector2f position;

        public Vector2f scale;

        public Origin origin;

        public Anchor anchor;

        public boolean applyGlobalScaling;

        public Vector2f size;

        public Color color;

        public Integer thickness;

        public Boolean filled;

        public int zIndex;

        public RectangleData(Vector2f size) {
            this.id = "rectangle";
            this.scale = new Vector2f(1.0f, 1.0f);
            this.applyGlobalScaling = false;
            this.position = new Vector2f();
            this.origin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Left);
            this.anchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Left);
            this.size = size;
            this.color = Color.WHITE;
            this.thickness = 1;
            this.filled = true;
            this.zIndex = 0;
        }
    }

    public static class WindowData {
        public String id;

        public Vector2f position;

        public Vector2f scale;

        public Origin origin;

        public Anchor anchor;

        public WindowData() {
            this.id = "window";
            this.position = new Vector2f();
            this.scale = new Vector2f(1.0f, 1.0f);
            this.origin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Left);
            this.anchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Left);
        }

        public static WindowData copy(WindowData data) {
            WindowData windowData = new WindowData();
            windowData.id = data.id;
            windowData.position = data.position;
            windowData.scale = data.scale;
            windowData.origin = data.origin;
            windowData.anchor = data.anchor;
            return windowData;
        }
    }

    private static class WindowContainer {
        Entity entity;
        WindowData data;
        Entity content;
        TextData contentData;

        public static WindowContainer copy(WindowContainer data, TextData contentData) {
            WindowContainer windowContainer = new WindowContainer();
            windowContainer.entity = data.entity;
            windowContainer.data = WindowData.copy(data.data);
            windowContainer.contentData = contentData;
            windowContainer.content = data.content;
            return windowContainer;
        }
    }

    Scene scene = SceneManager.getScene();

    Registry registry = SceneManager.getScene().getRegistry();

    Entity canvas;

    Font font = Resources.loadFont("ui/default.ttf", 16);

    Vector2f globalScale;

    WindowContainer currentWindow;

    public GuiBuilder() {
        canvas = registry.addEntityWithName("canvas");
        globalScale = new Vector2f(3.0f, 3.0f);
    }

    public void addText(TextData data) {
        if (currentWindow == null) {
            Entity text = canvas.createChildWithName(data.id);
            TransformComponent transformComponent = createScaledComponent(data.scale, data.applyGlobalScaling);
            textPosition(data, transformComponent);
            text.addComponent(transformComponent);
            text.addComponent(new UiRendererComponent(data.zIndex));
            TextComponent textComponent = text.addComponent(new TextComponent(data.text, data.font));
            textComponent.color = data.color;
            Window.get().getSizeChanged().add((dataType, windowData) -> {
                TextData textData = TextData.copy(data);
                textData.text = textComponent.text;
                textPosition(textData, transformComponent);
            });
            textComponent.textChanged.add((dataType, unused) -> {
                TextData textData = TextData.copy(data);
                textData.text = textComponent.text;
                textPosition(textData, transformComponent);
            });
        } else {
            Entity text = currentWindow.entity.createChildWithName(data.id);
            TransformComponent transformComponent = createScaledComponent(data.scale, data.applyGlobalScaling);
            textPosition(data, transformComponent);
            text.addComponent(transformComponent);
            text.addComponent(new UiRendererComponent(data.zIndex));
            TextComponent textComponent = text.addComponent(new TextComponent(data.text, data.font));
            textComponent.color = data.color;
            currentWindow.content = text;
            currentWindow.contentData = data;
        }
    }

    public void addButton(ButtonData data) {
        Entity button;
        if (data.parentId == null || data.parentId.isEmpty()) {
            button = canvas.createChildWithName(data.id);
        } else {
            button = registry.getEntityByName(data.parentId).createChildWithName(data.id);
        }
        TransformComponent transformComponent = createScaledComponent(data.scale, data.applyGlobalScaling);
        buttonPosition(data, transformComponent);
        Window.get().getSizeChanged().add((dataType, windowData) -> buttonPosition(data, transformComponent));
        button.addComponent(transformComponent);
        button.addComponent(new UiRendererComponent(data.zIndex));
        button.addComponent(new ButtonComponent(data.text, data.listener));
    }

    public void addImage(ImageData data) {
        Entity image;
        if (data.parentId == null || data.parentId.isEmpty()) {
            image = canvas.createChildWithName(data.id);
        } else {
            image = registry.getEntityByName(data.parentId).createChildWithName(data.id);
        }
        TransformComponent transformComponent = createScaledComponent(data.scale, data.applyGlobalScaling);
        imagePosition(data, transformComponent);
        Window.get().getSizeChanged().add((dataType, windowData) -> imagePosition(data, transformComponent));
        image.addComponent(transformComponent);
        image.addComponent(new UiRendererComponent(data.zIndex));
        image.addComponent(new SpriteComponent(new Sprite(data.texture, data.region), data.tint));
    }

    public void addRectangle(RectangleData data) {
        Entity image = canvas.createChildWithName(data.id);
        TransformComponent transformComponent = createScaledComponent(data.scale, data.applyGlobalScaling);
        rectanglePosition(data, transformComponent);
        Window.get().getSizeChanged().add((dataType, windowData) -> rectanglePosition(data, transformComponent));
        image.addComponent(transformComponent);
        image.addComponent(new UiRendererComponent(data.zIndex));
        image.addComponent(new RectangleComponent(data.size));
        image.getComponent(RectangleComponent.class).setColor(data.color);
        image.getComponent(RectangleComponent.class).setFilled(data.filled);
        image.getComponent(RectangleComponent.class).setThickness(data.thickness);
    }

    public void beginWindow(WindowData data) {
        Entity window = canvas.createChildWithName(data.id);
        currentWindow = new WindowContainer();
        currentWindow.entity = window;
        currentWindow.data = data;
    }

    public void endWindow() {
        NineSlicedSprite nineSlicedSprite = new NineSlicedSprite(Resources.loadSpritesheet("ui/window_default.json"));

        TransformComponent transformComponent = createScaledComponent(currentWindow.data.scale, true);
        WindowContainer copy = WindowContainer.copy(currentWindow, currentWindow.contentData);
        windowPosition(copy.content, copy.data, transformComponent);
        TextComponent textComponent = copy.content.getComponent(TextComponent.class);

        Entity windowEntity = currentWindow.entity;
        Window.get().getSizeChanged().add((dataType, windowData) -> {
            if (copy.content.getComponentSafe(TextComponent.class) != null) {
                TextData textData = TextData.copy(copy.contentData);
                textData.text = copy.content.getComponent(TextComponent.class).text;
                copy.contentData = textData;
                if (windowEntity.getComponentSafe(NineSlicedSpriteComponent.class) != null) {
                    windowEntity.getComponentSafe(NineSlicedSpriteComponent.class).setSize(copy.content.getComponent(TextComponent.class).getSize().div(transformComponent.scale));
                }
                windowPosition(copy.content, copy.data, transformComponent);
            }
        });
        copy.content.getComponent(TextComponent.class).textChanged.add((dataType, datas) -> {
            if (copy.content.getComponentSafe(TextComponent.class) != null) {
                TextData textData = TextData.copy(copy.contentData);
                textData.text = copy.content.getComponent(TextComponent.class).text;
                copy.contentData = textData;
                windowEntity.getComponent(NineSlicedSpriteComponent.class).setSize(copy.content.getComponent(TextComponent.class).getSize().div(transformComponent.scale));
                windowPosition(copy.content, copy.data, transformComponent);
            }
        });
        currentWindow.entity.addComponent(transformComponent);
        currentWindow.entity.addComponent(new UiRendererComponent());
        currentWindow.entity.addComponent(new NineSlicedSpriteComponent(nineSlicedSprite, currentWindow.content.getComponent(TextComponent.class).getSize().div(transformComponent.scale)));

        TransformComponent contentTransform = currentWindow.content.getComponent(TransformComponent.class);
        currentWindow.content.getComponent(UiRendererComponent.class).zIndex++;
        contentTransform.position = new Vector2f(transformComponent.position.x + 5 * transformComponent.scale.x, transformComponent.position.y + 5 * transformComponent.scale.y);
        Window.get().getSizeChanged().add((dataType, windowData) -> contentTransform.position = new Vector2f(transformComponent.position.x + 5 * transformComponent.scale.x, transformComponent.position.y + 5 * transformComponent.scale.y));
        textComponent.textChanged.add((dataType, unused) -> contentTransform.position = new Vector2f(transformComponent.position.x + 5 * transformComponent.scale.x, transformComponent.position.y + 5 * transformComponent.scale.y));

        currentWindow = null;
    }

    private void textPosition(TextData data, TransformComponent transformComponent) {
        transformComponent.position = new Vector2f(data.position.x, data.position.y);

        switch (data.origin.horizontal) {
            case Custom:
                transformComponent.position.x += data.origin.customHorizontal;
                break;
            case Left:
                break;
            case Center:
                transformComponent.position.x -= data.font.getScaledTextWidth(data.text, transformComponent.scale.x) / 2 * transformComponent.scale.x;
                break;
            case Right:
                transformComponent.position.x -= data.font.getScaledTextWidth(data.text, transformComponent.scale.x) * transformComponent.scale.x;
                break;
        }

        switch (data.origin.vertical) {
            case Custom:
                transformComponent.position.x += data.origin.customVertical;
                break;
            case Top:
                break;
            case Middle:
                transformComponent.position.y -= data.font.getScaledTextHeight(data.text, transformComponent.scale.y) / 2 * transformComponent.scale.y;
                break;
            case Bottom:
                transformComponent.position.y -= data.font.getScaledTextHeight(data.text, transformComponent.scale.y) * transformComponent.scale.x;
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
    }

    private void buttonPosition(ButtonData data, TransformComponent transformComponent) {
        transformComponent.position = new Vector2f(data.position.x, data.position.y);

        switch (data.origin.horizontal) {
            case Custom:
                transformComponent.position.x += data.origin.customHorizontal;
                break;
            case Left:
                break;
            case Center:
                transformComponent.position.x -= (5 + (data.font.getScaledTextWidth(data.text, transformComponent.scale.x) / 2)) * transformComponent.scale.x;
                break;
            case Right:
                transformComponent.position.x -= (10 + (data.font.getScaledTextWidth(data.text, transformComponent.scale.x))) * transformComponent.scale.x;
                break;
        }

        switch (data.origin.vertical) {
            case Custom:
                transformComponent.position.x += data.origin.customVertical;
                break;
            case Top:
                break;
            case Middle:
                transformComponent.position.y -= (5 + (data.font.getScaledTextHeight(data.text, transformComponent.scale.y) / 2)) * transformComponent.scale.y;
                break;
            case Bottom:
                transformComponent.position.y -= (10 + (data.font.getScaledTextHeight(data.text, transformComponent.scale.y))) * transformComponent.scale.x;
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
    }

    private void imagePosition(ImageData data, TransformComponent transformComponent) {
        transformComponent.position = new Vector2f(data.position.x, data.position.y);

        switch (data.origin.horizontal) {
            case Custom:
                transformComponent.position.x += data.origin.customHorizontal;
                break;
            case Left:
                break;
            case Center:
                transformComponent.position.x -= data.texture.getWidth() * transformComponent.scale.x / 2;
                break;
            case Right:
                transformComponent.position.x -= data.texture.getWidth() * transformComponent.scale.x;
                break;
        }

        switch (data.origin.vertical) {
            case Custom:
                transformComponent.position.x += data.origin.customVertical;
                break;
            case Top:
                break;
            case Middle:
                transformComponent.position.x -= data.texture.getHeight() * transformComponent.scale.y / 2;
                break;
            case Bottom:
                transformComponent.position.x -= data.texture.getHeight() * transformComponent.scale.y;
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
    }

    private void rectanglePosition(RectangleData data, TransformComponent transformComponent) {
        transformComponent.position = new Vector2f(data.position.x, data.position.y);

        switch (data.origin.horizontal) {
            case Custom:
                transformComponent.position.x += data.origin.customHorizontal;
                break;
            case Left:
                break;
            case Center:
                transformComponent.position.x -= data.size.x * transformComponent.scale.x / 2;
                break;
            case Right:
                transformComponent.position.x -= data.size.x * transformComponent.scale.x;
                break;
        }

        switch (data.origin.vertical) {
            case Custom:
                transformComponent.position.x += data.origin.customVertical;
                break;
            case Top:
                break;
            case Middle:
                transformComponent.position.x -= data.size.y * transformComponent.scale.y / 2;
                break;
            case Bottom:
                transformComponent.position.x -= data.size.y * transformComponent.scale.y;
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
    }

    private void windowPosition(Entity content, WindowData data, TransformComponent transformComponent) {
        if (content != null && content.getComponentSafe(TextComponent.class) != null) {
            transformComponent.position = new Vector2f(data.position.x, data.position.y);

            TextComponent textComponent = content.getComponent(TextComponent.class);

            switch (data.origin.horizontal) {
                case Custom:
                    transformComponent.position.x += data.origin.customHorizontal;
                    break;
                case Left:
                    break;
                case Center:
                    transformComponent.position.x -= (5 + (textComponent.font.getScaledTextWidth(textComponent.text, transformComponent.scale.x) / 2)) * transformComponent.scale.x;
                    break;
                case Right:
                    transformComponent.position.x -= (10 + (textComponent.font.getScaledTextWidth(textComponent.text, transformComponent.scale.x))) * transformComponent.scale.x;
                    break;
            }

            switch (data.origin.vertical) {
                case Custom:
                    transformComponent.position.x += data.origin.customVertical;
                    break;
                case Top:
                    break;
                case Middle:
                    transformComponent.position.y -= (5 + (textComponent.font.getScaledTextHeight(textComponent.text, transformComponent.scale.y) / 2)) * transformComponent.scale.y;
                    break;
                case Bottom:
                    transformComponent.position.y -= (10 + (textComponent.font.getScaledTextHeight(textComponent.text, transformComponent.scale.y))) * transformComponent.scale.x;
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
        }
    }

    private TransformComponent createScaledComponent(Vector2f scale, boolean applyGlobalScaling) {
        TransformComponent transformComponent = new TransformComponent();

        if (applyGlobalScaling) {
            transformComponent.scale = new Vector2f(globalScale.x, globalScale.y);
        }

        transformComponent.scale.mul(scale);

        return transformComponent;
    }
}
