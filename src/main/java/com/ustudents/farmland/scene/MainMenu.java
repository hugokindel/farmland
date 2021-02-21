package com.ustudents.farmland.scene;

import com.ustudents.engine.audio.Sound;
import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.Window;
import com.ustudents.engine.ecs.Entity;
import com.ustudents.engine.ecs.component.audio.SoundComponent;
import com.ustudents.engine.ecs.component.core.TransformComponent;
import com.ustudents.engine.ecs.component.graphics.*;
import com.ustudents.engine.ecs.component.gui.ButtonComponent;
import com.ustudents.engine.ecs.component.gui.wip.CanvasComponent;
import com.ustudents.engine.graphic.*;
import com.ustudents.engine.scene.Scene;
import com.ustudents.engine.utility.SeedRandom;
import com.ustudents.farmland.Farmland;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;

public class MainMenu extends Scene {
    Font font;
    Texture grassTexture;
    Texture titleTexture;
    Sound musicSound;

    @Override
    public void initialize() {
        camera.enableInput(false);

        font = Resources.loadFont("ui/debug.ttf", 32);
        grassTexture = Resources.loadTexture("map/grass.png");
        titleTexture = Resources.loadTexture("ui/farmland_title.png");
        musicSound = Resources.loadSound("music/main_menu_background.ogg");

        initializeBackground();
        initializeUi();
        initializeMusic();
    }

    public void initializeBackground() {
        SeedRandom random = new SeedRandom();

        Entity grassContainer = registry.createEntity();
        grassContainer.setName("grassContainer");

        for (int x = 0; x < 30; x++) {
            for (int y = 0; y < 15; y++) {
                int textureRegionX = 24 * random.generateInRange(1, 5);
                int textureRegionY = 24 * random.generateInRange(1, 5);

                Entity grass = registry.createEntity();
                grass.setParent(grassContainer);
                grass.addComponent(new TransformComponent(new Vector2f(-360 + x * 24, -240 + y * 24)));
                TextureComponent textureComponent = grass.addComponent(new TextureComponent(grassTexture));
                textureComponent.setRegion(new Vector4f(textureRegionX, textureRegionY, 24, 24));
                grass.addComponent(new WorldRendererComponent());
            }
        }
    }

    public void initializeUi() {
        Vector2i windowSize = Farmland.get().getWindow().getSize();

        Entity uiContainer = registry.createEntity();
        uiContainer.setName("uiContainer");

        Entity title = registry.createEntity();
        title.setName("titleLabel");
        title.setParent(uiContainer);
        title.addComponent(
                new TransformComponent(new Vector2f(windowSize.x / 2, 20), new Vector2f(1.5f, 1.5f)));
        title.addComponent(new TextureComponent(titleTexture));
        Farmland.get().getWindow().sizeChanged.add((dataType, data) -> {
            Window.SizeChangedEventData event = (Window.SizeChangedEventData)data;
            title.getComponent(TransformComponent.class).setPosition(new Vector2f(event.newSize.x / 2.0f, 20));
        });
        title.getComponent(TextureComponent.class).setOrigin(new Vector2f(titleTexture.getWidth() / 2, 0.0f));
        title.addComponent(new UiRendererComponent());

        String[] buttons = new String[] {"Jouer", "Multijoueur", "Paramètres", "Crédits", "Quitter"};
        String[] buttonsName = new String[] {"singleplayer", "multiplayer", "settings", "credits", "quit"};

        for (int i = 0; i < buttons.length; i++) {
            int finalI = i;
            Entity button = registry.createEntity();
            button.setName(buttonsName[i] + "Button");
            button.setParent(uiContainer);
            button.addComponent(new TransformComponent(
                    new Vector2f(windowSize.x / 2.0f, 300 + 80 * i), new Vector2f(3.1f, 3.1f)));
            button.addComponent(new ButtonComponent(buttons[i], (dataType, data) -> {
                switch (buttonsName[finalI]) {
                    case "singleplayer":
                        changeScene(SinglePlayerMenu.class);
                        break;
                    case "multiplayer":
                        changeScene(MultiplayerMenu.class);
                        break;
                    case "settings":
                        changeScene(SettingsMenu.class);
                        break;
                    case "credits":
                        changeScene(CreditsMenu.class);
                        break;
                    case "quit":
                        quit();
                        break;
                }
            }));
            Farmland.get().getWindow().sizeChanged.add((dataType, data) -> {
                Window.SizeChangedEventData event = (Window.SizeChangedEventData)data;
                button.getComponent(TransformComponent.class).setPosition(new Vector2f(event.newSize.x / 2.0f, 300 + 80 * finalI));
            });
            button.addComponent(new UiRendererComponent());
        }

        Entity version = registry.createEntity();
        version.setName("versionLabel");
        version.setParent(uiContainer);
        version.addComponent(new TransformComponent(
                new Vector2f(10, windowSize.y - font.getTextHeight("Version: 0.0.1") - 8), new Vector2f(1, 1)));
        version.addComponent(new TextComponent("Version: 0.0.1", font));
        version.addComponent(new UiRendererComponent());
        Farmland.get().getWindow().sizeChanged.add((dataType, data) -> {
            Window.SizeChangedEventData event = (Window.SizeChangedEventData)data;
            version.getComponent(TransformComponent.class).setPosition(new Vector2f(10, event.newSize.y - font.getTextHeight("Version: 0.0.1") - 8));
        });
    }

    public void initializeUi2() {
        Entity canvas = registry.createEntity();
        canvas.addComponent(new TransformComponent());
        canvas.addComponent(new UiRendererComponent());
        canvas.addComponent(new CanvasComponent());

        Entity title = registry.createEntity();
        title.setParent(canvas);
        title.addComponent(new TransformComponent());
    }

    public void initializeMusic() {
        Entity music = registry.createEntity();
        music.setName("music");
        music.addComponent(new SoundComponent(musicSound, true, true));
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void render() {

    }

    @Override
    public void destroy() {

    }
}
