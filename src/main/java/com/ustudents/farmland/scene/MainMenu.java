package com.ustudents.farmland.scene;

import com.ustudents.engine.Game;
import com.ustudents.engine.audio.Sound;
import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.Window;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.core.event.EventListener;
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

import java.math.BigDecimal;
import java.math.RoundingMode;

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
                Entity grass = registry.createEntity();
                grass.setParent(grassContainer);

                grass.addComponent(TransformComponent.class, new Vector2f(-360 + x * 24, -240 + y * 24), new Vector2f(1, 1));

                int textureRegionX = 24 * random.generateInRange(1, 5);
                int textureRegionY = 24 * random.generateInRange(1, 5);
                grass.addComponent(TextureComponent.class, grassTexture,
                        new Vector4f(textureRegionX, textureRegionY, 24, 24));

                grass.addComponent(RenderableComponent.class);
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
        title.addComponent(TransformComponent.class, new Vector2f(windowSize.x / 2.0f, 20), new Vector2f(1.5f, 1.5f));
        title.addComponent(TextureComponent.class, titleTexture);
        Farmland.get().getWindow().sizeChanged.add(new EventListener() {
            @Override
            public void onReceived(Class<?> dataType, Object data) {
                Window.SizeChangedEventData event = (Window.SizeChangedEventData)data;
                title.getComponent(TransformComponent.class).position = new Vector2f(event.newSize.x / 2.0f, 20);
            }
        });
        title.getComponent(TextureComponent.class).origin = new Vector2f(256.0f, 0.0f);
        title.addComponent(RenderableComponent.class);
        title.addComponent(UiComponent.class);

        String[] buttons = new String[] {"Jouer", "Multijoueur", "Paramètres", "Crédits", "Quitter"};
        String[] buttonsName = new String[] {"singleplayer", "multiplayer", "settings", "credits", "quit"};

        for (int i = 0; i < buttons.length; i++) {
            Entity button = registry.createEntity();
            button.setName(buttonsName[i] + "Button");
            button.setParent(uiContainer);
            button.addComponent(TransformComponent.class, new Vector2f(windowSize.x / 2.0f, 300 + 80 * i), new Vector2f(3.1f, 3.1f));
            button.addComponent(ButtonComponent.class, buttons[i]);
            int finalI = i;
            button.getComponent(ButtonComponent.class).addListener((dataType, data) -> {
                Out.println("Button called " + buttonsName[finalI] + "Button" + " pressed");
                if (buttonsName[finalI].equals("quit")) {
                    Game.get().close();
                }
            });
            Farmland.get().getWindow().sizeChanged.add(new EventListener() {
                @Override
                public void onReceived(Class<?> dataType, Object data) {
                    Window.SizeChangedEventData event = (Window.SizeChangedEventData)data;
                    button.getComponent(TransformComponent.class).position = new Vector2f(event.newSize.x / 2.0f, 300 + 80 * finalI);
                }
            });
            button.addComponent(RenderableComponent.class, 100);
            button.addComponent(UiComponent.class);
        }

        Entity stats = registry.createEntity();
        stats.setName("statsLabel");
        stats.setParent(uiContainer);
        stats.addComponent(TransformComponent.class, new Vector2f(10, 10), new Vector2f(1, 1));
        stats.addComponent(LabelComponent.class, "", fontSmaller);
        stats.addComponent(RenderableComponent.class);
        stats.addComponent(UiComponent.class);

        Entity version = registry.createEntity();
        version.setName("versionLabel");
        version.setParent(uiContainer);
        version.addComponent(TransformComponent.class, new Vector2f(10, windowSize.y - font.getTextHeight("Version: 0.0.1")), new Vector2f(1, 1));
        version.addComponent(LabelComponent.class, "Version: 0.0.1", fontSmaller);
        version.addComponent(RenderableComponent.class);
        version.addComponent(UiComponent.class);
        Farmland.get().getWindow().sizeChanged.add(new EventListener() {
            @Override
            public void onReceived(Class<?> dataType, Object data) {
                Window.SizeChangedEventData event = (Window.SizeChangedEventData)data;
                version.getComponent(TransformComponent.class).position = new Vector2f(10, event.newSize.y - font.getTextHeight("Version: 0.0.1"));
            }
        });
    }

    public void initializeMusic() {
        Entity music = registry.createEntity();
        music.setName("music");
        music.addComponent(SoundComponent.class, musicSound, true);
    }

    @Override
    public void update(float dt) {
        int fps = Game.get().getTimer().getFPS();
        double ms = BigDecimal.valueOf(Game.get().getTimer().getFrameDuration())
                .setScale(3, RoundingMode.HALF_UP).doubleValue();
        int numEntities = registry.getTotalNumberOfEntities();

        registry.getEntityByName("statsLabel").getComponent(LabelComponent.class).text = "FPS: " + fps + "\nFramerate: " + ms + "\nNumber of entities: " + numEntities;
    }

    @Override
    public void render() {
        /*spritebatch.begin(camera);
        nine(new Vector2f(10, 10), new Vector2f(font2.getTextWidth("Solo"), font2.getTextHeight("Solo") / 2), "Solo", font2);
        spritebatch.end();*/
    }

    public void nine(Vector2f position, Vector2f size, String text, Font font) {
        Vector2f neededSize = nineSlicedSprite.getCompleteNeededSize(size);
        spritebatch.drawNineSlicedSprite(nineSlicedSprite, position, size, 0, Color.WHITE, 0, new Vector2f(1, 1), new Vector2f(neededSize.x / 2, neededSize.y / 2));
        spritebatch.drawText(text, font, position, 0, Color.WHITE, 0, new Vector2f(1, 1), new Vector2f(font.getTextWidth(text) / 2, font.getTextHeight(text) / 2));
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
