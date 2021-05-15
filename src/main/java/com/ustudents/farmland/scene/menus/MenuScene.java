package com.ustudents.farmland.scene.menus;

import com.ustudents.engine.audio.Sound;
import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.cli.option.annotation.Command;
import com.ustudents.engine.core.event.EventListener;
import com.ustudents.engine.ecs.Entity;
import com.ustudents.engine.ecs.component.audio.SoundComponent;
import com.ustudents.engine.ecs.component.core.TransformComponent;
import com.ustudents.engine.ecs.component.graphic.WorldRendererComponent;
import com.ustudents.engine.graphic.*;
import com.ustudents.engine.gui.GuiBuilder;
import com.ustudents.engine.scene.Scene;
import com.ustudents.engine.scene.SceneManager;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.component.GridComponent;
import org.joml.Vector2f;
import org.joml.Vector2i;

public abstract class MenuScene extends Scene {
    private String[] buttonNames;
    private String[] buttonIds;
    private EventListener[] eventListeners;
    boolean gameTitle;
    boolean canGoToMainMenu;
    boolean canQuit;
    boolean canGoBack;
    protected Vector2i goBackButtonSpacing = new Vector2i(0, 0);
    protected Anchor goBackButtonAnchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Center);
    protected Origin goBackButtonOrigin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Center);
    protected boolean hasButtonNearBack = false;
    protected Vector2i buttonNearBackSpacing = new Vector2i(0, 0);
    protected Anchor buttonNearBackAnchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Center);
    protected Origin buttonNearBackOrigin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Center);

    public MenuScene() {
        this.buttonNames = new String[0];
        this.buttonIds = new String[0];
        this.eventListeners = new EventListener[0];
    }

    public void initializeMenu(String[] buttonNames, String[] buttonIds, EventListener[] eventListeners, boolean gameTitle, boolean canGoToMainMenu, boolean canQuit, boolean canGoBack) {
        this.buttonNames = buttonNames;
        this.buttonIds = buttonIds;
        this.eventListeners = eventListeners;
        this.gameTitle = gameTitle;
        this.canGoToMainMenu = canGoToMainMenu;
        this.canQuit = canQuit;
        this.canGoBack = canGoBack;
    }

    @Override
    public void initialize() {
        initializeGameplay();
        initializeMusic();
        initializeGui();
    }

    public void initializeGameplay() {
        getWorldCamera().enableInput(false);

        NineSlicedSprite gridBackground = new NineSlicedSprite(Resources.loadSpritesheet("ui/map_background.json"));
        Texture cellBackground = Resources.loadTexture("terrain/grass.png");
        AnimatedSprite selectionCursor = new AnimatedSprite(Resources.loadSpritesheet("ui/map_cell_cursor.json"));
        Spritesheet territoryTexture = Resources.loadSpritesheet("ui/map_territory_indicator_white.json");

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

        if (gameTitle){

            Texture titleTexture = Resources.loadTexture("ui/farmland_title.png");
            GuiBuilder.ImageData imageData = new GuiBuilder.ImageData(titleTexture);
            imageData.id = "titleImage";
            imageData.origin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Center);
            imageData.anchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Center);
            imageData.scale = new Vector2f(1.5f, 1.5f);
            imageData.position.y = 50;
            guiBuilder.addImage(imageData);
        }

        /*if (gameResult){
            String resultPath = (isWin)?"ui/victory.png":"ui/defeat.png";
            Texture resultTexture = Resources.loadTexture(resultPath);
            GuiBuilder.ImageData imageData = new GuiBuilder.ImageData(resultTexture);
            imageData.id = "resultImage";
            imageData.origin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Center);
            imageData.anchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Center);
            imageData.scale = new Vector2f(7f, 7f);
            imageData.position.y = 7;
            guiBuilder.addImage(imageData);
        }*/

        int i;
        for (i = 0; i < buttonNames.length; i++) {
            GuiBuilder.ButtonData buttonData = new GuiBuilder.ButtonData(buttonNames[i], eventListeners[i]);

            if (hasButtonNearBack && i == buttonNames.length - 1) {
                buttonData.position = new Vector2f(buttonNearBackSpacing.x, 250 + i * 75 + buttonNearBackSpacing.y);
                buttonData.origin = buttonNearBackOrigin;
                buttonData.anchor = buttonNearBackAnchor;
            } else {
                buttonData.position = new Vector2f(0, 250 + i * 75);
                buttonData.origin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Center);
                buttonData.anchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Center);
            }

            buttonData.id = buttonIds[i];
            guiBuilder.addButton(buttonData);
        }

        if (canGoBack) {
            if (hasButtonNearBack) {
                i--;
            }

            try {
                GuiBuilder.ButtonData buttonData = new GuiBuilder.ButtonData(Resources.getLocalizedText("return"), (dataType, data) -> {
                    try {
                        SceneManager.get().goBack();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                buttonData.origin = goBackButtonOrigin;
                buttonData.anchor = goBackButtonAnchor;
                buttonData.position = new Vector2f(goBackButtonSpacing.x, 250 + i * 75 + goBackButtonSpacing.y);
                buttonData.id = "backButton";
                guiBuilder.addButton(buttonData);
                i++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (canGoToMainMenu) {
            GuiBuilder.ButtonData buttonData = new GuiBuilder.ButtonData(Resources.getLocalizedText("mainMenu"), (dataType, data) -> changeScene(new MainMenu(), false));
            buttonData.origin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Center);
            buttonData.anchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Center);
            buttonData.position = new Vector2f(0, 250 + i * 75);
            buttonData.id = "mainMenuButton";
            guiBuilder.addButton(buttonData);
            i++;
        }

        if (canQuit) {
            GuiBuilder.ButtonData buttonData = new GuiBuilder.ButtonData(Resources.getLocalizedText("quit"), (dataType, data) -> quit());
            buttonData.origin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Center);
            buttonData.anchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Center);
            buttonData.position = new Vector2f(0, 250 + i * 75);
            buttonData.id = "quitButton";
            guiBuilder.addButton(buttonData);
        }

        Font debugFont = Resources.loadFont("ui/debug.ttf", 16);

        GuiBuilder.TextData textData = new GuiBuilder.TextData(Resources.getLocalizedText("version", Farmland.class.getAnnotation(Command.class).version()));
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
