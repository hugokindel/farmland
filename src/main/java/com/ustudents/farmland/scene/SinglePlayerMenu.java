package com.ustudents.farmland.scene;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.Window;
import com.ustudents.engine.ecs.Entity;
import com.ustudents.engine.ecs.component.core.TransformComponent;
import com.ustudents.engine.ecs.component.graphics.TextComponent;
import com.ustudents.engine.ecs.component.graphics.TextureComponent;
import com.ustudents.engine.ecs.component.graphics.UiRendererComponent;
import com.ustudents.engine.ecs.component.graphics.WorldRendererComponent;
import com.ustudents.engine.ecs.component.gui.ButtonComponent;
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

public class SingleplayerMenu extends Scene {
    @Override
    public void initialize() {
        Farmland.setKindOfGame("SinglePlayer");

        Vector2i size = Window.get().getSize();

        SeedRandom random = new SeedRandom();
        Entity mapContainer = registry.createEntity();
        mapContainer.setName("map");

        for (int x = 0; x < 32; x++) {
            for (int y = 0; y < 32; y++) {
                int textureRegionX = 24 * random.generateInRange(1, 5);
                int textureRegionY = 24 * random.generateInRange(1, 5);

                Entity grass = registry.createEntity();
                grass.setParent(mapContainer);
                grass.addComponent(new TransformComponent(new Vector2f(-360 + x * 24, -240 + y * 24)));
                TextureComponent textureComponent = grass.addComponent(new TextureComponent(Resources.loadTexture("examples/grass.png")));
                textureComponent.setRegion(new Vector4f(textureRegionX, textureRegionY, 24, 24));
                grass.addComponent(new WorldRendererComponent());
                grass.addComponent(new CellComponent());
            }
        }

        Entity button = registry.createEntity();
        button.addComponent(new TransformComponent(
                new Vector2f(size.x - 120, size.y - 35), new Vector2f(3.1f, 3.1f)));
        button.addComponent(new ButtonComponent("Finir le tour"));
        Farmland.get().getWindow().sizeChanged.add((dataType, data) -> {
            Window.SizeChangedEventData event = (Window.SizeChangedEventData)data;
            button.getComponent(TransformComponent.class).setPosition(new Vector2f(event.newSize.x - 160, event.newSize.y - 140));
        });
        button.addComponent(new UiRendererComponent());

        Entity player = registry.createEntity();
        player.addComponent(new TransformComponent(
                new Vector2f((float)size.x / 2 - (Resources.loadFont("ui/default.ttf", 16).getTextWidth("Tour 5 de Léo") * 3 / 2), 10), new Vector2f(3.1f, 3.1f)));
        player.addComponent(new TextComponent("Tour 5 de Léo", Resources.loadFont("ui/default.ttf", 16)));
        Farmland.get().getWindow().sizeChanged.add((dataType, data) -> {
            Window.SizeChangedEventData event = (Window.SizeChangedEventData)data;
            player.getComponent(TransformComponent.class).setPosition(new Vector2f((float)event.newSize.x / 2, 10));
        });
        player.addComponent(new UiRendererComponent());
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
