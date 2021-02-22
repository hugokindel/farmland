package com.ustudents.farmland.scene;

import com.ustudents.engine.audio.Sound;
import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.cli.option.annotation.Command;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.ecs.Entity;
import com.ustudents.engine.ecs.component.audio.SoundComponent;
import com.ustudents.engine.ecs.component.core.TransformComponent;
import com.ustudents.engine.ecs.component.graphics.*;
import com.ustudents.engine.ecs.component.gui.wip.CanvasComponent;
import com.ustudents.engine.graphic.*;
import com.ustudents.engine.gui.GuiBuilder;
import com.ustudents.engine.scene.Scene;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.component.GridComponent;
import org.joml.Vector2f;
import org.joml.Vector2i;

public class MainMenu extends Scene {
    @Override
    public void initialize() {
        initializeGameplay();
        initializeMusic();
        initializeGui();
    }

    public void initializeGameplay() {
        getWorldCamera().enableInput(false);

        NineSlicedSprite gridBackground = new NineSlicedSprite(Resources.loadSpritesheet("ui/map_background.json"));
        Texture cellBackground = Resources.loadTexture("map/grass.png");
        AnimatedSprite selectionCursor = new AnimatedSprite(Resources.loadSpritesheet("ui/map_cell_cursor.json"));
        Spritesheet territoryTexture = Resources.loadSpritesheet("ui/map_territory_indicator.json");

        if (!hasEntityWithName("grid")) {
            Entity grid = createEntityWithName("grid");
            grid.keepOnLoad(true);
            grid.addComponent(new TransformComponent());
            grid.addComponent(new WorldRendererComponent());
            GridComponent gridComponent = grid.addComponent(new GridComponent(new Vector2i(32, 32), new Vector2i(24, 24), gridBackground, cellBackground, selectionCursor, territoryTexture));
            gridComponent.setSelectionCursorEnabled(false);
        }
    }

    public void initializeGui() {
        GuiBuilder guiBuilder = new GuiBuilder();

        Texture titleTexture = Resources.loadTexture("ui/farmland_title.png");

        GuiBuilder.ImageData imageData = new GuiBuilder.ImageData(titleTexture);
        imageData.id = "titleImage";
        imageData.origin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Center);
        imageData.anchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Center);
        imageData.scale = new Vector2f(1.5f, 1.5f);
        imageData.position.y = 50;
        guiBuilder.addImage(imageData);

        String[] buttonNames = {"Solo", "Multijoueur", "Paramètres", "Crédits", "Quitter"};
        String[] buttonIds = {"singleplayerButton", "multiplayerButton", "settingsButton", "creditsButton", "quitButton"};

        for (int i = 0; i < buttonNames.length; i++) {
            int j = i;
            GuiBuilder.ButtonData buttonData = new GuiBuilder.ButtonData(buttonNames[i], (dataType, data) -> {
                switch (buttonIds[j]) {
                    case "singleplayerButton":
                        getSceneManager().changeScene(new SingleplayerMenu());
                        break;
                    case "multiplayerButton":
                        changeScene(new MultiplayerMenu());
                        break;
                    case "settingsButton":
                        changeScene(new SettingsMenu());
                        break;
                    case "creditsButton":
                        changeScene(new CreditsMenu());
                        break;
                    case "quitButton":
                        quit();
                        break;
                }
            });
            buttonData.origin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Center);
            buttonData.anchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Center);
            buttonData.position = new Vector2f(0, 250 + i * 75);
            buttonData.id = buttonIds[i];
            guiBuilder.addButton(buttonData);
        }

        Font debugFont = Resources.loadFont("ui/debug.ttf", 16);

        GuiBuilder.TextData textData = new GuiBuilder.TextData("Version: " + Farmland.class.getAnnotation(Command.class).version());
        textData.font = debugFont;
        textData.origin = new Origin(Origin.Vertical.Bottom, Origin.Horizontal.Left);
        textData.anchor = new Anchor(Anchor.Vertical.Bottom, Anchor.Horizontal.Left);
        textData.position = new Vector2f(10, -10);
        textData.scale = new Vector2f(0.75f, 0.75f);
        guiBuilder.addText(textData);
    }

    public void initializeMusic() {
        if (!hasEntityWithName("backgroundMusic")) {
            Sound musicSound = Resources.loadSound("music/main_menu_background.ogg");
            Entity music = createEntityWithName("backgroundMusic");
            music.keepOnLoad(true);
            music.addComponent(new SoundComponent(musicSound, true, true));
        }
    }
}
