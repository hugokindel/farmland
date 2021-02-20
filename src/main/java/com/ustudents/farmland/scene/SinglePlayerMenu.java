package com.ustudents.farmland.scene;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.Window;
import com.ustudents.engine.ecs.Entity;
import com.ustudents.engine.ecs.component.core.TransformComponent;
import com.ustudents.engine.ecs.component.graphics.*;
import com.ustudents.engine.ecs.component.gui.ButtonComponent;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.graphic.Font;
import com.ustudents.engine.graphic.NineSlicedSprite;
import com.ustudents.engine.input.Input;
import com.ustudents.engine.input.Key;
import com.ustudents.engine.scene.Scene;
import com.ustudents.engine.graphic.imgui.ImGuiUtils;
import com.ustudents.engine.utility.SeedRandom;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.map.CellComponent;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;

public class SinglePlayerMenu extends Scene {
    @Override
    public void initialize() {
        camera.setMinimalX(-360);
        camera.setMinimalY(-240);
        camera.setMaximalX(-360 + 32 * 24);
        camera.setMaximalY(-240 + 32 * 24);

        Farmland.setKindOfGame("SinglePlayer");

        Vector2i size = Window.get().getSize();

        SeedRandom random = new SeedRandom();
        Entity mapContainer = registry.createEntity();
        mapContainer.setName("map");

        for (int x = 0; x < 32; x++) {
            for (int y = 0; y < 32; y++) {
                int textureRegionX = 24 * random.generateInRange(1, 5);
                int textureRegionY = 24 * random.generateInRange(1, 5);

                {
                    Entity grass = registry.createEntity();
                    grass.setParent(mapContainer);
                    grass.addComponent(new TransformComponent(new Vector2f(-360 + x * 24, -240 + y * 24)));
                    TextureComponent textureComponent = grass.addComponent(new TextureComponent(Resources.loadTexture("examples/grass.png")));
                    textureComponent.setRegion(new Vector4f(textureRegionX, textureRegionY, 24, 24));
                    grass.addComponent(new WorldRendererComponent(1));
                    grass.addComponent(new CellComponent());
                }
            }
        }

        {
            Entity mapBackground = registry.createEntity();
            mapBackground.setParent(mapContainer);
            mapBackground.addComponent(new TransformComponent(new Vector2f(-365, -245)));
            NineSlicedSpriteComponent textureComponent = mapBackground.addComponent(new NineSlicedSpriteComponent(new NineSlicedSprite(Resources.loadSpritesheet("ui/map_background.json")), new Vector2f(32 * 24, 32 * 24)));
            mapBackground.addComponent(new WorldRendererComponent(0));

            Entity cursorSelector = registry.createEntity();
            cursorSelector.setName("cellCursor");
            cursorSelector.addComponent(new TransformComponent(new Vector2f(0, 0)));
            AnimatedSpriteComponent textureComponent2 = cursorSelector.addComponent(new AnimatedSpriteComponent(Resources.loadSpritesheet("ui/cell_cursor.json"), "default"));
            cursorSelector.addComponent(new WorldRendererComponent(2));
        }

        // ----

        {
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
            canvasTitle.setParent(mapContainer);
            canvasTitle.addComponent(new TransformComponent(
                    new Vector2f((float) size.x / 2 - (textSize.x), -12), new Vector2f(3f, 3f)));
            NineSlicedSpriteComponent sprite = canvasTitle.addComponent(new NineSlicedSpriteComponent(new NineSlicedSprite(Resources.loadSpritesheet("ui/canvas_default.json")), new Vector2f(textSize.x, textSize.y)));
            sprite.setOrigin(new Vector2f(new Vector2f(textSize.x / 4, 0)));
            canvasTitle.addComponent(new UiRendererComponent(0));

            Entity player = registry.createEntity();
            player.addComponent(new TransformComponent(
                    new Vector2f((float) size.x / 2 - (textSize.x), 10), new Vector2f(3.1f, 3.1f)));
            TextComponent textComponent = player.addComponent(new TextComponent(text, Resources.loadFont("ui/default.ttf", 16)));
            textComponent.setColor(Color.BLACK);
            Farmland.get().getWindow().sizeChanged.add((dataType, data) -> {
                Window.SizeChangedEventData event = (Window.SizeChangedEventData) data;
                player.getComponent(TransformComponent.class).setPosition(new Vector2f((float) event.newSize.x / 2, 10));
            });
            player.addComponent(new UiRendererComponent(1));
        }


    }

    @Override
    public void update(float dt) {
        if (Input.isKeyDown(Key.W) || Input.isKeyDown(Key.Up)) {
            getCamera().moveTop(400 * dt);
        }
        if (Input.isKeyDown(Key.S) || Input.isKeyDown(Key.Down)) {
            getCamera().moveBottom(400 * dt);
        }
        if (Input.isKeyDown(Key.A) || Input.isKeyDown(Key.Left)) {
            getCamera().moveLeft(400 * dt);
        }
        if (Input.isKeyDown(Key.D) || Input.isKeyDown(Key.Right)) {
            getCamera().moveRight(400 * dt);
        }
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
