package com.ustudents.farmland.scene;

import com.ustudents.engine.audio.Sound;
import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.Window;
import com.ustudents.engine.ecs.Entity;
import com.ustudents.engine.ecs.component.*;
import com.ustudents.engine.ecs.system.UiRenderSystem;
import com.ustudents.engine.graphic.*;
import com.ustudents.engine.scene.Scene;
import com.ustudents.engine.utility.SeedRandom;
import com.ustudents.farmland.Farmland;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;

public class MainMenu extends Scene {
    Texture grassTexture;
    Texture titleTexture;
    Font font;
    Font fontSmaller;
    Font font2;
    Sound musicSound;
    Spritesheet spritesheet;
    NineSlicedSprite nineSlicedSprite;

    @Override
    public void initialize() {
        camera.enableInput(false);

        grassTexture = Resources.loadTexture("examples/grass.png");
        titleTexture = Resources.loadTexture("title.png");
        font = Resources.loadFont("default.ttf", 36);
        font2 = Resources.loadFont("kobold.ttf", 16);
        fontSmaller = Resources.loadFont("default.ttf", 24);
        musicSound = Resources.loadSound("backgroundMenu1.ogg");
        spritesheet = Resources.loadSpritesheet("ui/button_default.json");
        nineSlicedSprite = new NineSlicedSprite(spritesheet);

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
                new TransformComponent(new Vector2f(windowSize.x / 2.0f, 20), new Vector2f(1.5f, 1.5f)));
        title.addComponent(new TextureComponent(titleTexture));
        Farmland.get().getWindow().sizeChanged.add((dataType, data) -> {
            Window.SizeChangedEventData event = (Window.SizeChangedEventData)data;
            title.getComponent(TransformComponent.class).setPosition(new Vector2f(event.newSize.x / 2.0f, 20));
        });
        title.getComponent(TextureComponent.class).setOrigin(new Vector2f(256.0f, 0.0f));
        title.addComponent(new UiRendererComponent());

        String[] buttons = new String[] {"Jouer", "Multijoueur", "Paramètres", "Crédits", "Quitter"};
        String[] buttonsName = new String[] {"singleplayer", "multiplayer", "settings", "credits", "quit"};

        for (int i = 0; i < buttons.length; i++) {
            Entity button = registry.createEntity();
            button.setName(buttonsName[i] + "Button");
            button.setParent(uiContainer);
            button.addComponent(new TransformComponent(
                    new Vector2f(windowSize.x / 2.0f, 300 + 80 * i), new Vector2f(3.1f, 3.1f)));
            button.addComponent(new ButtonComponent(buttons[i]));
            int finalI = i;
            button.getComponent(ButtonComponent.class).addListener((dataType, data) -> {
                if (buttonsName[finalI].equals("singleplayer")) {
                    changeScene(SingleplayerMenu.class);
                } else if (buttonsName[finalI].equals("multiplayer")) {
                    changeScene(MultiplayerMenu.class);
                } else if (buttonsName[finalI].equals("settings")) {
                    changeScene(SettingsMenu.class);
                } else if (buttonsName[finalI].equals("credits")) {
                    changeScene(CreditsMenu.class);
                } else if (buttonsName[finalI].equals("quit")) {
                    quit();
                }
            });
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
                new Vector2f(10, windowSize.y - font.getTextHeight("Version: 0.0.1")), new Vector2f(1, 1)));
        version.addComponent(new LabelComponent("Version: 0.0.1", fontSmaller));
        version.addComponent(new UiRendererComponent());
        Farmland.get().getWindow().sizeChanged.add((dataType, data) -> {
            Window.SizeChangedEventData event = (Window.SizeChangedEventData)data;
            version.getComponent(TransformComponent.class).setPosition(new Vector2f(10, event.newSize.y - font.getTextHeight("Version: 0.0.1")));
        });
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
