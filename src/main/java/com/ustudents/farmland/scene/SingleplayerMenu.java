package com.ustudents.farmland.scene;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.Window;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.ecs.Entity;
import com.ustudents.engine.ecs.component.core.TransformComponent;
import com.ustudents.engine.ecs.component.graphics.*;
import com.ustudents.engine.graphic.*;
import com.ustudents.engine.gui.GuiBuilder;
import com.ustudents.engine.scene.Scene;
import com.ustudents.engine.graphic.imgui.ImGuiUtils;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.component.GridComponent;
import com.ustudents.farmland.component.PlayerMovementComponent;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import org.joml.Vector2f;
import org.joml.Vector2i;

public class SingleplayerMenu extends Scene {
    @Override
    public void initialize() {
        Farmland.setKindOfGame("SinglePlayer");

        Vector2i size = Window.get().getSize();

        initializeGameplay();
        initializeGui();

        // ----

        /*{
            Font font = Resources.loadFont("ui/default.ttf", 16);

            Entity button = registry.createEntity();
            button.addComponent(new TransformComponent(
                    new Vector2f(size.x - 120, size.y - 35), new Vector2f(3.1f, 3.1f)));
            button.addComponent(new ButtonComponent("Finir le tour"));
            Farmland.get().getWindow().sizeChanged.add((dataType, data) -> {
                Window.SizeChangedEventData event = (Window.SizeChangedEventData) data;
                button.getComponent(TransformComponent.class).setPosition(new Vector2f(event.newSize.x - 160, event.newSize.y - 140));
            });
            button.addComponent(new UiRendererComponent());

            String text = "Tour 1 de Joueur 1";
            Vector2f textSize = new Vector2f(font.getTextWidth(text) * 3 / 2, font.getTextHeight(text) * 3 / 2);

            Entity canvasTitle = registry.createEntity();
            canvasTitle.addComponent(new TransformComponent(
                    new Vector2f((float) size.x / 2 - (textSize.x), -12), new Vector2f(3f, 3f)));
            NineSlicedSpriteComponent sprite = canvasTitle.addComponent(new NineSlicedSpriteComponent(new NineSlicedSprite(Resources.loadSpritesheet("ui/canvas_default.json")), new Vector2f(textSize.x, textSize.y)));
            sprite.setOrigin(new Vector2f(new Vector2f(textSize.x / 4, 0)));
            canvasTitle.addComponent(new UiRendererComponent(0));

            Entity playerText = registry.createEntity();
            playerText.addComponent(new TransformComponent(
                    new Vector2f((float) size.x / 2 - (textSize.x), 10), new Vector2f(3.1f, 3.1f)));
            TextComponent textComponent = playerText.addComponent(new TextComponent(text, Resources.loadFont("ui/default.ttf", 16)));
            textComponent.setColor(Color.BLACK);
            Farmland.get().getWindow().sizeChanged.add((dataType, data) -> {
                Window.SizeChangedEventData event = (Window.SizeChangedEventData) data;
                playerText.getComponent(TransformComponent.class).setPosition(new Vector2f((float) event.newSize.x / 2, 10));
            });
            playerText.addComponent(new UiRendererComponent(1));
        }*/


    }

    public void initializeGameplay() {
        NineSlicedSprite gridBackground = new NineSlicedSprite(Resources.loadSpritesheet("ui/map_background.json"));
        Texture cellBackground = Resources.loadTexture("map/grass.png");
        AnimatedSprite selectionCursor = new AnimatedSprite(Resources.loadSpritesheet("ui/map_cell_cursor.json"));
        Spritesheet territoryTexture = Resources.loadSpritesheet("ui/map_territory_indicator.json");

        Entity grid = registry.createEntityWithName("grid");
        grid.addComponent(new TransformComponent());
        grid.addComponent(new WorldRendererComponent());
        grid.addComponent(new GridComponent(new Vector2i(32, 32), new Vector2i(24, 24), gridBackground, cellBackground, selectionCursor, territoryTexture));

        Entity player = registry.createEntityWithName("player");
        player.addComponent(new PlayerMovementComponent(500.0f));
    }

    public void initializeGui() {
        GuiBuilder guiBuilder = new GuiBuilder();

        GuiBuilder.ButtonData buttonData = new GuiBuilder.ButtonData("Finir le tour", (dataType, data) -> {
            Out.println("Fin du tour");
        });
        buttonData.origin = new Origin(Origin.Vertical.Bottom, Origin.Horizontal.Right);
        buttonData.anchor = new Anchor(Anchor.Vertical.Bottom, Anchor.Horizontal.Right);
        buttonData.position = new Vector2f(-10, -10);
        guiBuilder.addButton(buttonData);

        GuiBuilder.WindowData windowData = new GuiBuilder.WindowData();
        windowData.origin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Center);
        windowData.anchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Center);
        windowData.position.y += 5;
        guiBuilder.beginWindow(windowData);

        // Happens within the window.
        {
            GuiBuilder.TextData textData = new GuiBuilder.TextData("Tour 1 de Joueur 1");
            textData.origin = new Origin(Origin.Vertical.Middle, Origin.Horizontal.Center);
            textData.anchor = new Anchor(Anchor.Vertical.Middle, Anchor.Horizontal.Center);
            textData.color = Color.BLACK;
            guiBuilder.addText(textData);
        }

        guiBuilder.endWindow();
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void render() {

    }

    @Override
    public void renderImGui() {
        ImGuiUtils.setNextWindowWithSizeCentered(300, 300, ImGuiCond.Appearing);
        ImGui.begin("Singleplayer Menu");

        if (ImGui.button("Main Menu")) {
            Game.get().getSceneManager().changeScene(MainMenu.class);
        }

        if (ImGui.button("Define User Menu")) {
            Game.get().getSceneManager().changeScene(DefineUser.class);
        }

        ImGui.end();
    }

    @Override
    public void destroy() {

    }
}
