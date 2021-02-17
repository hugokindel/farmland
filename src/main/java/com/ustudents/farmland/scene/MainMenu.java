package com.ustudents.farmland.scene;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.Resources;
import com.ustudents.engine.ecs.Entity;
import com.ustudents.engine.ecs.component.*;
import com.ustudents.engine.graphic.*;
import com.ustudents.engine.scene.Scene;
import com.ustudents.engine.graphic.imgui.ImGuiUtils;
import com.ustudents.engine.utility.SeedRandom;
import com.ustudents.farmland.Farmland;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;

public class MainMenu extends Scene {
    Texture grassTexture;
    Texture titleTexture;
    Font font;

    @Override
    public void initialize() {
        camera.enableInput(false);

        SeedRandom random = new SeedRandom();
        Vector2i windowSize = Farmland.get().getWindow().getSize();

        grassTexture = Resources.loadTexture("examples/grass.png");
        titleTexture = Resources.loadTexture("title.png");
        font = Resources.loadFont("default.ttf", 36);

        Entity grassContainer = registry.createEntity();
        grassContainer.setName("grassContainer");

        for (int x = 0; x < 20; x++) {
            for (int y = 0; y < 20; y++) {
                Entity grass = registry.createEntity();
                grass.setParent(grassContainer);

                grass.addComponent(TransformComponent.class, new Vector2f(-240 + x * 24, -240 + y * 24), new Vector2f(1, 1));

                int textureRegionX = 24 * random.generateInRange(1, 5);
                int textureRegionY = 24 * random.generateInRange(1, 5);
                grass.addComponent(SpriteComponent.class, grassTexture,
                        new Vector4f(textureRegionX, textureRegionY, 24, 24));

                grass.addComponent(RenderableComponent.class);
            }
        }

        Entity uiContainer = registry.createEntity();
        uiContainer.setName("uiContainer");

        Entity title = registry.createEntity();
        title.setName("titleLabel");
        title.setParent(uiContainer);

        title.addComponent(TransformComponent.class, new Vector2f(windowSize.x / 2.0f, 20), new Vector2f(1.5f, 1.5f));
        title.addComponent(SpriteComponent.class, titleTexture);
        title.getComponent(SpriteComponent.class).origin = new Vector2f(256.0f, 0.0f);
        title.addComponent(RenderableComponent.class);
        title.addComponent(UiComponent.class);

        String[] buttons = new String[] {"Jouer", "Multijoueur", "Paramètres", "Crédits", "Quitter"};
        String[] buttonsName = new String[] {"singleplayer", "multiplayer", "settings", "credits", "quit"};

        for (int i = 0; i < buttons.length; i++) {
            Entity button = registry.createEntity();
            button.setName(buttonsName[i]);
            button.setParent(uiContainer);

            button.addComponent(TransformComponent.class, new Vector2f(windowSize.x / 2.0f - font.getTextWidth(buttons[i]) / 2, 300 + 50 * i), new Vector2f(1.5f, 1.5f));
            button.addComponent(TextComponent.class, buttons[i], font);
            button.addComponent(RenderableComponent.class);
            button.addComponent(UiComponent.class);
        }
    }

    @Override
    public void update(double dt) {

    }

    @Override
    public void render() {
        /*spritebatch.begin(getCamera());
        spritebatch.drawRectangle(new Vector2f(0, 0), new Vector2f(10, 10));
        spritebatch.end();

        uiSpritebatch.begin(getUiCamera());
        Vector2i size = Farmland.get().getWindow().getSize();
        uiSpritebatch.drawRectangle(new Vector2f(0, 0), new Vector2f(120, 120), 0, Color.RED, 45, 10, new Vector2f(2, 2), new Vector2f(0, 0));
        uiSpritebatch.drawRectangle(new Vector2f(size.x, 0), new Vector2f(120, 120), 0, Color.RED, 45, 10, new Vector2f(22, 2), new Vector2f(120, 0));
        uiSpritebatch.drawRectangle(new Vector2f(size.x, size.y), new Vector2f(120, 120), 0, Color.RED, 45, 10, new Vector2f(2, 2), new Vector2f(120, 120));
        uiSpritebatch.drawRectangle(new Vector2f(0, size.y), new Vector2f(120, 120), 0, Color.RED, 45, 10, new Vector2f(2, 2), new Vector2f(0, 120));
        uiSpritebatch.drawRectangle(new Vector2f((float)size.x / 2, (float)size.y / 2), new Vector2f(120, 120), 0, Color.RED, 45, 10, new Vector2f(2, 2), new Vector2f(60, 60));
        /*uiSpritebatch.draw(texutre, new Vector2f(0, 0), new Vector4f(0, 0, 120, 120), 0, Color.WHITE, 45, new Vector2f(2, 2), new Vector2f(0, 0));
        uiSpritebatch.draw(texutre, new Vector2f(size.x, 0), new Vector4f(0, 0, 120, 120), 0, Color.WHITE, 45, new Vector2f(2, 2), new Vector2f(120, 0));
        uiSpritebatch.draw(texutre, new Vector2f(size.x, size.y), new Vector4f(0, 0, 120, 120), 0, Color.WHITE, 45, new Vector2f(2, 2), new Vector2f(120, 120));
        uiSpritebatch.draw(texutre, new Vector2f(0, size.y), new Vector4f(0, 0, 120, 120), 0, Color.WHITE, 45, new Vector2f(2, 2), new Vector2f(0, 120));
        uiSpritebatch.draw(texutre, new Vector2f((float)size.x / 2, (float)size.y / 2), new Vector4f(0, 0, 120, 120), 0, Color.WHITE, 45, new Vector2f(2, 2), new Vector2f(60, 60));
        uiSpritebatch.end();*/
    }

    @Override
    public void renderImGui() {
        ImGuiUtils.setNextWindowWithSizeCentered(300, 300, ImGuiCond.Appearing);
        ImGui.begin("Main Menu");

        if (ImGui.button("Single Player Menu")) {
            Game.get().getSceneManager().changeScene(SinglePlayerMenu.class);
        }
        if (ImGui.button("Multi Player Menu")) {
            Game.get().getSceneManager().changeScene(MultiPlayerMenu.class);
        }
        if (ImGui.button("Option Menu")) {
            Game.get().getSceneManager().changeScene(OptionMenu.class);
        }
        if (ImGui.button("Credit Menu")) {
            Game.get().getSceneManager().changeScene(CreditMenu.class);
        }
        if (ImGui.button("Example Scene")) {
            Game.get().getSceneManager().changeScene(ExampleScene.class);
        }
        if (ImGui.button("Quit")) {
            Game.get().close();
        }

        ImGui.end();
    }

    @Override
    public void destroy() {

    }
}
