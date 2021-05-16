package com.ustudents.farmland.scene;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.core.event.Event;
import com.ustudents.engine.core.event.EventDispatcher;
import com.ustudents.engine.core.event.EventListener;
import com.ustudents.engine.core.window.Window;
import com.ustudents.engine.graphic.imgui.ImGuiUtils;
import com.ustudents.engine.tools.console.ConsoleCommands;
import com.ustudents.engine.input.Input;
import com.ustudents.engine.input.Key;
import com.ustudents.engine.input.MouseButton;
import com.ustudents.engine.ecs.component.graphic.RectangleComponent;
import com.ustudents.engine.network.NetMode;
import com.ustudents.engine.ecs.component.gui.ButtonComponent;
import com.ustudents.engine.ecs.component.graphic.SpriteComponent;
import com.ustudents.engine.ecs.Entity;
import com.ustudents.engine.ecs.component.core.TransformComponent;
import com.ustudents.engine.ecs.component.graphic.WorldRendererComponent;
import com.ustudents.engine.ecs.component.gui.TextComponent;
import com.ustudents.engine.graphic.*;
import com.ustudents.engine.gui.GuiBuilder;
import com.ustudents.engine.scene.Scene;
import com.ustudents.engine.utility.DateUtil;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.component.EconomicComponent;
import com.ustudents.farmland.component.GridComponent;
import com.ustudents.farmland.component.PlayerMovementComponent;
import com.ustudents.farmland.component.TurnTimerComponent;
import com.ustudents.farmland.core.Save;
import com.ustudents.farmland.core.grid.Cell;
import com.ustudents.farmland.core.item.*;
import com.ustudents.farmland.core.player.Player;
import com.ustudents.farmland.network.actions.EndGameMessage;
import com.ustudents.farmland.network.general.LoadSaveResponse;
import com.ustudents.farmland.core.system.Caravan;
import com.ustudents.farmland.core.system.Research;
import com.ustudents.farmland.scene.menus.MainMenu;
import com.ustudents.farmland.scene.menus.ResultMenu;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.util.*;

@SuppressWarnings("unchecked")
public class InGameScene extends Scene {
    public static class PauseChanged extends Event {
        public boolean inPause;

        public PauseChanged(boolean inPause) {
            this.inPause = inPause;
        }
    }

    public ImBoolean showInventory;

    public ImBoolean showMarket;

    public ImBoolean showCaravan;

    public ImBoolean showResearch;

    public ImBoolean showBank;

    public boolean shouldShowBackInventory = false;

    public boolean shouldShowBackMarket = false;

    public boolean shouldShowBackCaravan = false;

    public boolean shouldShowBackResearch = false;

    public boolean shouldShowBackBank = false;

    public ImInt selectBorrow;

    public EconomicComponent economicComponent;

    public boolean sellMenu;

    public boolean caravanMenu;

    public String lastSelectedItemId;

    public boolean inPause = false;

    public EventDispatcher<PauseChanged> pauseChanged = new EventDispatcher<>();

    public boolean refundMenu;

    @Override
    public void initialize() {
        ConsoleCommands.show = true;
        forceImGui = true;

        initializeEntities();
        initializeGui();
        initializeGameplay();

        updatePlayerFrame();
        updateGameplayButtons();
        moneyUpdate();
    }

    public void initializeEntities() {
        if (hasEntityWithName("grid")) {
            killEntity(getEntityByName("grid"), true);
        }

        NineSlicedSprite gridBackground = new NineSlicedSprite(Resources.loadSpritesheet("ui/map_background.json"));
        Texture cellBackground = Resources.loadTexture("terrain/grass.png");
        AnimatedSprite selectionCursor = new AnimatedSprite(Resources.loadSpritesheet("ui/map_cell_cursor.json"));
        Spritesheet territoryTexture = Resources.loadSpritesheet("ui/map_territory_indicator_white.json");

        Entity grid = createEntityWithName("grid");
        grid.addComponent(new TransformComponent());
        grid.addComponent(new WorldRendererComponent());
        grid.addComponent(new GridComponent(new Vector2i(Farmland.get().getLoadedSave().cells.size(), Farmland.get().getLoadedSave().cells.get(0).size()), new Vector2i(24, 24), gridBackground, cellBackground, selectionCursor, territoryTexture));
        grid.addComponent(new TurnTimerComponent(Save.timePerTurn));
        Farmland.get().getLoadedSave().itemUsed.add((dataType, data) -> updateMoneyItemLabel());

        if (Farmland.get().getNetMode() != NetMode.DedicatedServer) {
            Farmland.get().getLoadedSave().players.get(Farmland.get().getLoadedSave().getLocalPlayer().getId()).moneyChanged.add((dataType, data) -> updateMoneyItemLabel());
        }

        Entity player = createEntityWithName("player");
        player.addComponent(new PlayerMovementComponent(500.0f));
    }

    public void initializeGui() {
        showInventory = new ImBoolean(false);
        showMarket = new ImBoolean(false);
        showCaravan = new ImBoolean(false);
        showResearch = new ImBoolean(false);
        showBank = new ImBoolean(false);
        selectBorrow = new ImInt(Farmland.get().getLoadedSave().maxBorrow/10);

        GuiBuilder guiBuilder = new GuiBuilder();

        getEntityByName("canvas").createChildWithName("gameplayButtons");

        Texture bankTexture = Resources.loadTexture("ui/bank.png");
        GuiBuilder.ImageData imageDataBank= new GuiBuilder.ImageData(bankTexture);
        imageDataBank.id = "bankButtonImage";
        imageDataBank.parentId = "gameplayButtons";
        imageDataBank.origin = new Origin(Origin.Vertical.Bottom, Origin.Horizontal.Right);
        imageDataBank.anchor = new Anchor(Anchor.Vertical.Bottom, Anchor.Horizontal.Right);

        if (Resources.getConfig().language.equals("fr")) {
            imageDataBank.position = new Vector2f(-800, -110);
        } else {
            imageDataBank.position = new Vector2f(-775, -110);
        }

        imageDataBank.scale = new Vector2f(3f, 3f);
        imageDataBank.zIndex = 2;
        guiBuilder.addImage(imageDataBank);

        Texture researchTexture = Resources.loadTexture("ui/research.png");
        GuiBuilder.ImageData imageDataResearch= new GuiBuilder.ImageData(researchTexture);
        imageDataResearch.id = "researchButtonImage";
        imageDataResearch.parentId = "gameplayButtons";
        imageDataResearch.origin = new Origin(Origin.Vertical.Bottom, Origin.Horizontal.Right);
        imageDataResearch.anchor = new Anchor(Anchor.Vertical.Bottom, Anchor.Horizontal.Right);

        if (Resources.getConfig().language.equals("fr")) {
            imageDataResearch.position = new Vector2f(-642, -110);
        } else {
            imageDataResearch.position = new Vector2f(-613, -110);
        }

        imageDataResearch.scale = new Vector2f(3f, 3f);
        imageDataResearch.zIndex = 2;
        guiBuilder.addImage(imageDataResearch);

        Texture caravanTexture = Resources.loadTexture("ui/caravan.png");
        GuiBuilder.ImageData imageDataCaravan= new GuiBuilder.ImageData(caravanTexture);
        imageDataCaravan.id = "caravanButtonImage";
        imageDataCaravan.parentId = "gameplayButtons";
        imageDataCaravan.origin = new Origin(Origin.Vertical.Bottom, Origin.Horizontal.Right);
        imageDataCaravan.anchor = new Anchor(Anchor.Vertical.Bottom, Anchor.Horizontal.Right);

        if (Resources.getConfig().language.equals("fr")) {
            imageDataCaravan.position = new Vector2f(-465, -110);
        } else {
            imageDataCaravan.position = new Vector2f(-435, -110);
        }

        imageDataCaravan.scale = new Vector2f(3f, 3f);
        imageDataCaravan.zIndex = 2;
        guiBuilder.addImage(imageDataCaravan);


        Texture marketTexture = Resources.loadTexture("ui/market.png");
        GuiBuilder.ImageData imageDataMarket= new GuiBuilder.ImageData(marketTexture);
        imageDataMarket.id = "marketButtonImage";
        imageDataMarket.parentId = "gameplayButtons";
        imageDataMarket.origin = new Origin(Origin.Vertical.Bottom, Origin.Horizontal.Right);
        imageDataMarket.anchor = new Anchor(Anchor.Vertical.Bottom, Anchor.Horizontal.Right);

        if (Resources.getConfig().language.equals("fr")) {
            imageDataMarket.position = new Vector2f(-320, -112);
        } else {
            imageDataMarket.position = new Vector2f(-290, -112);
        }

        imageDataMarket.scale = new Vector2f(3f, 3f);
        imageDataMarket.zIndex = 2;
        guiBuilder.addImage(imageDataMarket);

        Texture inventoryTexture = Resources.loadTexture("ui/inventory.png");
        GuiBuilder.ImageData imageDataInventory= new GuiBuilder.ImageData(inventoryTexture);
        imageDataInventory.id = "inventoryButtonImage";
        imageDataInventory.parentId = "gameplayButtons";
        imageDataInventory.origin = new Origin(Origin.Vertical.Bottom, Origin.Horizontal.Right);
        imageDataInventory.anchor = new Anchor(Anchor.Vertical.Bottom, Anchor.Horizontal.Right);

        if (Resources.getConfig().language.equals("fr")) {
            imageDataInventory.position = new Vector2f(-145, -113);
        } else {
            imageDataInventory.position = new Vector2f(-105, -113);
        }

        imageDataInventory.scale = new Vector2f(3f, 3f);
        imageDataInventory.zIndex = 2;
        guiBuilder.addImage(imageDataInventory);

        Texture turnEndTexture = Resources.loadTexture("ui/time.png");
        GuiBuilder.ImageData imageDataTurnEnd= new GuiBuilder.ImageData(turnEndTexture);
        imageDataTurnEnd.id = "turnEndButtonImage";
        imageDataTurnEnd.parentId = "gameplayButtons";
        imageDataTurnEnd.origin = new Origin(Origin.Vertical.Bottom, Origin.Horizontal.Right);
        imageDataTurnEnd.anchor = new Anchor(Anchor.Vertical.Bottom, Anchor.Horizontal.Right);

        if (Resources.getConfig().language.equals("fr")) {
            imageDataTurnEnd.position = new Vector2f(40, -114);
        } else {
            imageDataTurnEnd.position = new Vector2f(70, -114);
        }

        imageDataTurnEnd.scale = new Vector2f(3f, 3f);
        imageDataTurnEnd.zIndex = 2;
        guiBuilder.addImage(imageDataTurnEnd);

        Texture frameTexture = Resources.loadTexture("ui/frame.png");
        GuiBuilder.ImageData imageDataFrame = new GuiBuilder.ImageData(frameTexture);
        imageDataFrame.id = "frameImage";
        imageDataFrame.origin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Left);
        imageDataFrame.anchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Left);
        imageDataFrame.scale = new Vector2f(3f, 3f);
        imageDataFrame.position.y = 10;
        imageDataFrame.position.x = 3;
        imageDataFrame.zIndex = 2;
        guiBuilder.addImage(imageDataFrame);

        initializeAvatar(guiBuilder);

        Texture moneyTexture = Resources.loadTexture("ui/coin3.png");
        GuiBuilder.ImageData imageDataMoney = new GuiBuilder.ImageData(moneyTexture);
        imageDataMoney.id = "MoneyImage";
        imageDataMoney.origin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Left);
        imageDataMoney.anchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Left);
        imageDataMoney.scale = new Vector2f(3f, 3f);
        imageDataMoney.position.y = 40;
        imageDataMoney.position.x = 185;
        imageDataMoney.zIndex = 2;
        guiBuilder.addImage(imageDataMoney);

        Texture goldTrophyTexture = Resources.loadTexture("ui/goldtrophy.png");
        GuiBuilder.ImageData imageDataGT = new GuiBuilder.ImageData(goldTrophyTexture);
        imageDataGT.id = "GTImage";
        imageDataGT.origin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Right);
        imageDataGT.anchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Right);
        imageDataGT.scale = new Vector2f(3f, 3f);
        imageDataGT.position = new Vector2f(-10, 40);
        imageDataGT.zIndex = 2;
        guiBuilder.addImage(imageDataGT);

        if (Farmland.get().getLoadedSave().players.size() >= 2) {
            Texture silverTrophyTexture = Resources.loadTexture("ui/silvertrophy.png");
            GuiBuilder.ImageData imageDataST = new GuiBuilder.ImageData(silverTrophyTexture);
            imageDataST.id = "STImage";
            imageDataST.origin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Right);
            imageDataST.anchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Right);
            imageDataST.scale = new Vector2f(3f, 3f);
            imageDataST.position = new Vector2f(-10, 85);
            imageDataST.zIndex = 2;
            guiBuilder.addImage(imageDataST);
        }

        if (Farmland.get().getLoadedSave().players.size() >= 3) {
            Texture bronzeTrophyTexture = Resources.loadTexture("ui/bronzetrophy.png");
            GuiBuilder.ImageData imageDataBT = new GuiBuilder.ImageData(bronzeTrophyTexture);
            imageDataBT.id = "BTImage";
            imageDataBT.origin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Right);
            imageDataBT.anchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Right);
            imageDataBT.scale = new Vector2f(3f, 3f);
            imageDataBT.position = new Vector2f(-10, 130);
            imageDataBT.zIndex = 2;
            guiBuilder.addImage(imageDataBT);
        }

        Texture leaderBoardTexture = Resources.loadTexture("ui/leaderboard.png");
        GuiBuilder.ImageData imageDataLB = new GuiBuilder.ImageData(leaderBoardTexture);
        imageDataLB.id = "LBImage";
        imageDataLB.origin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Right);
        imageDataLB.anchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Right);
        imageDataLB.scale = new Vector2f(2f, 2f);
        imageDataLB.position = new Vector2f(-20, -8);
        imageDataLB.zIndex = 2;
        guiBuilder.addImage(imageDataLB);

        GuiBuilder.ButtonData buttonData = new GuiBuilder.ButtonData(Resources.getLocalizedText("endTurn"), (dataType, data) -> {
            Farmland.get().getLoadedSave().endTurn();
        });
        buttonData.id = "endTurnButton";
        buttonData.parentId = "gameplayButtons";
        buttonData.origin = new Origin(Origin.Vertical.Bottom, Origin.Horizontal.Right);
        buttonData.anchor = new Anchor(Anchor.Vertical.Bottom, Anchor.Horizontal.Right);
        buttonData.position = new Vector2f(-10, -10);

        guiBuilder.addButton(buttonData);

        GuiBuilder.ButtonData buttonData1 = new GuiBuilder.ButtonData(Resources.getLocalizedText("inventory"), (dataType, data) -> {
            showInventory.set(!showInventory.get());
        });
        buttonData1.id = "inventoryButton";
        buttonData1.parentId = "gameplayButtons";
        buttonData1.origin = new Origin(Origin.Vertical.Bottom, Origin.Horizontal.Right);
        buttonData1.anchor = new Anchor(Anchor.Vertical.Bottom, Anchor.Horizontal.Right);
        if (Resources.getConfig().language.equals("fr")) {
            buttonData1.position = new Vector2f(-220, -10);
        } else {
            buttonData1.position = new Vector2f(-185, -10);
        }
        guiBuilder.addButton(buttonData1);

        GuiBuilder.ButtonData buttonData3 = new GuiBuilder.ButtonData(Resources.getLocalizedText("market"), (dataType, data) -> {
            showMarket.set(!showMarket.get());
        });
        buttonData3.id = "marketButton";
        buttonData3.parentId = "gameplayButtons";
        buttonData3.origin = new Origin(Origin.Vertical.Bottom, Origin.Horizontal.Right);
        buttonData3.anchor = new Anchor(Anchor.Vertical.Bottom, Anchor.Horizontal.Right);
        if (Resources.getConfig().language.equals("fr")) {
            buttonData3.position = new Vector2f(-400, -8);
        } else {
            buttonData3.position = new Vector2f(-370, -10);
        }
        guiBuilder.addButton(buttonData3);

        GuiBuilder.ButtonData buttonDataC = new GuiBuilder.ButtonData(Resources.getLocalizedText("caravans"), (dataType, data) -> {
            showCaravan.set(!showCaravan.get());
        });
        buttonDataC.id = "caravanButton";
        buttonDataC.parentId = "gameplayButtons";
        buttonDataC.origin = new Origin(Origin.Vertical.Bottom, Origin.Horizontal.Right);
        buttonDataC.anchor = new Anchor(Anchor.Vertical.Bottom, Anchor.Horizontal.Right);
        if (Resources.getConfig().language.equals("fr")) {
            buttonDataC.position = new Vector2f(-535, -10);
        } else {
            buttonDataC.position = new Vector2f(-515, -10);
        }
        guiBuilder.addButton(buttonDataC);

        GuiBuilder.ButtonData buttonDataR = new GuiBuilder.ButtonData(Resources.getLocalizedText("research"), (dataType, data) -> {
            showResearch.set(!showResearch.get());
        });
        buttonDataR.id = "researchButton";
        buttonDataR.parentId = "gameplayButtons";
        buttonDataR.origin = new Origin(Origin.Vertical.Bottom, Origin.Horizontal.Right);
        buttonDataR.anchor = new Anchor(Anchor.Vertical.Bottom, Anchor.Horizontal.Right);
        if (Resources.getConfig().language.equals("fr")) {
            buttonDataR.position = new Vector2f(-715, -10);
        } else {
            buttonDataR.position = new Vector2f(-695, -10);
        }
        guiBuilder.addButton(buttonDataR);

        GuiBuilder.ButtonData buttonDataB = new GuiBuilder.ButtonData(Resources.getLocalizedText("bank"), (dataType, data) -> {
            showBank.set(!showBank.get());
        });
        buttonDataB.id = "bankButton";
        buttonDataB.parentId = "gameplayButtons";
        buttonDataB.origin = new Origin(Origin.Vertical.Bottom, Origin.Horizontal.Right);
        buttonDataB.anchor = new Anchor(Anchor.Vertical.Bottom, Anchor.Horizontal.Right);
        if (Resources.getConfig().language.equals("fr")) {
            buttonDataB.position = new Vector2f(-880, -10);
        } else {
            buttonDataB.position = new Vector2f(-865, -10);
        }
        guiBuilder.addButton(buttonDataB);

        GuiBuilder.ButtonData buttonData2 = new GuiBuilder.ButtonData(Resources.getLocalizedText("mainMenu"), (dataType, data) -> {
            setPause(false);
            Farmland.get().writeLoadedSave();
            Farmland.get().unloadSave();
            if (getGame().isConnectedToServer()) {
                getGame().disconnectFromServer();
            }
            changeScene(new MainMenu());
        });
        buttonData2.id = "mainMenuButton";
        buttonData2.origin = new Origin(Origin.Vertical.Bottom, Origin.Horizontal.Left);
        buttonData2.anchor = new Anchor(Anchor.Vertical.Bottom, Anchor.Horizontal.Left);
        buttonData2.position = new Vector2f(10, -10);
        buttonData2.zIndex = 50;
        guiBuilder.addButton(buttonData2);
        getEntityByName("mainMenuButton").getComponent(ButtonComponent.class).bypassDisableInput = true;

        GuiBuilder.WindowData windowData = new GuiBuilder.WindowData();
        windowData.origin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Center);
        windowData.anchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Center);
        windowData.position.y += 5;
        guiBuilder.beginWindow(windowData);

        // Happens within the window.
        {
            GuiBuilder.TextData textData = new GuiBuilder.TextData(Resources.getLocalizedText("turnInfo", Farmland.get().getLoadedSave().turn + 1, Farmland.get().getLoadedSave().getCurrentPlayer().name));
            textData.id = "stateLabel";
            textData.origin = new Origin(Origin.Vertical.Middle, Origin.Horizontal.Center);
            textData.anchor = new Anchor(Anchor.Vertical.Middle, Anchor.Horizontal.Center);
            textData.color = Color.BLACK;
            guiBuilder.addText(textData);
        }

        guiBuilder.endWindow();

        GuiBuilder.TextData textDataTime = new GuiBuilder.TextData(Resources.getLocalizedText("timePassed",DateUtil.secondsToText(Farmland.get().getLoadedSave().timePassed)));
        textDataTime.id = "timePassedLabel";
        textDataTime.origin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Center);
        textDataTime.anchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Center);
        textDataTime.position = new Vector2f(0, 95);
        textDataTime.color = Color.BLACK;
        guiBuilder.addText(textDataTime);

        GuiBuilder.TextData textData = new GuiBuilder.TextData(Resources.getLocalizedText("timeRemaining", DateUtil.secondsToText(Save.timePerTurn - Farmland.get().getLoadedSave().turnTimePassed)));
        textData.id = "timeRemainingLabel";
        textData.origin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Center);
        textData.anchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Center);
        textData.position = new Vector2f(0, 65);
        textData.color = Color.BLACK;
        guiBuilder.addText(textData);

        String text = "";
        GuiBuilder.TextData textData2 = new GuiBuilder.TextData(text);
        textData2.id = "selectedLabel";
        textData2.origin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Left);
        textData2.anchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Left);
        textData2.position = new Vector2f(10, 155);
        textData2.color = Color.BLACK;

        guiBuilder.addText(textData2);

        String text2 = "    " + Farmland.get().getLoadedSave().getLocalPlayer().money;
        GuiBuilder.TextData textDataMoney = new GuiBuilder.TextData(text2);
        textDataMoney.id = "MoneyLabel";
        textDataMoney.origin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Left);
        textDataMoney.anchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Left);
        textDataMoney.position = new Vector2f(200, 50);
        textDataMoney.color = Color.BLACK;

        guiBuilder.addText(textDataMoney);

        if (Farmland.get().getLoadedSave().deadPlayers == null) {
            Farmland.get().getLoadedSave().deadPlayers = new ArrayList<>();
        }

        List<Player> leaderBoardList = leaderBoardMaker(Farmland.get().getLoadedSave().players);
        StringBuilder leaderBoard = new StringBuilder(Resources.getLocalizedText("leaderboard"));
        for (Player player : leaderBoardList) {
            leaderBoard.append("\n\n").append(player.name).append(" : ").append(Farmland.get().getLoadedSave().deadPlayers.contains(player.getId()) ? "dead" : player.money);
        }
        GuiBuilder.TextData textData3 = new GuiBuilder.TextData(leaderBoard.toString());
        textData3.id = "LeaderBoardLabel";
        textData3.origin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Right);
        textData3.anchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Right);
        textData3.position = new Vector2f(-80, 10);
        textData3.color = Color.BLACK;
        guiBuilder.addText(textData3);

        GuiBuilder.TextData pauseText = new GuiBuilder.TextData(Resources.getLocalizedText("theWorld"));
        pauseText.id = "pauseLabel";
        pauseText.origin = new Origin(Origin.Vertical.Middle, Origin.Horizontal.Center);
        pauseText.anchor = new Anchor(Anchor.Vertical.Middle, Anchor.Horizontal.Center);
        pauseText.position = new Vector2f(0, 0);
        pauseText.color = Color.WHITE;
        pauseText.zIndex = 15;
        guiBuilder.addText(pauseText);

        GuiBuilder.RectangleData rectangleData = new GuiBuilder.RectangleData(new Vector2f(Game.get().getWindow().getSize()));
        rectangleData.id = "pauseBackgroundRectangle";
        rectangleData.origin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Left);
        rectangleData.anchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Left);

        rectangleData.position = new Vector2f(0, 0);
        rectangleData.color = new Color(0, 0, 0, .30f);
        rectangleData.zIndex = 10;
        guiBuilder.addRectangle(rectangleData);
        Window.get().getSizeChanged().add((dataType, data) -> {
            if (hasEntityWithName("pauseBackgroundRectangle")) {
                if (getEntityByName("pauseBackgroundRectangle").getComponentSafe(RectangleComponent.class) != null) {
                    getEntityByName("pauseBackgroundRectangle").getComponentSafe(RectangleComponent.class).setSize(new Vector2f(Game.get().getWindow().getSize()));
                }
            }
        });

        getEntityByName("pauseLabel").setEnabled(false);
        getEntityByName("pauseBackgroundRectangle").setEnabled(false);

        pauseChanged.add((dataType, data) -> {
            if (inPause) {
                getEntityByName("pauseLabel").setEnabled(true);
                getEntityByName("pauseBackgroundRectangle").setEnabled(true);
            } else {
                getEntityByName("pauseLabel").setEnabled(false);
                getEntityByName("pauseBackgroundRectangle").setEnabled(false);
            }
        });

        updateMoneyItemLabel();
    }

    public void initializeAvatar(GuiBuilder guiBuilder) {
        Spritesheet avatarSprites = Resources.loadSpritesheet("ui/player.json");

        GuiBuilder.ImageData base = new GuiBuilder.ImageData(avatarSprites.getSprite("base").getTexture());
        base.id = "localPlayerAvatarBase";
        base.origin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Left);
        base.anchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Left);
        base.scale = new Vector2f(3f, 3f);
        base.position.y = 52;
        base.position.x = 46;
        base.region = avatarSprites.getSprite("base").getRegion();
        base.zIndex = 0;
        guiBuilder.addImage(base);

        GuiBuilder.ImageData braces = new GuiBuilder.ImageData(avatarSprites.getSprite("layerBraces").getTexture());
        braces.id = "localPlayerAvatarBraces";
        braces.origin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Left);
        braces.anchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Left);
        braces.scale = new Vector2f(3f, 3f);
        braces.position.y = 52;
        braces.position.x = 46;
        braces.region = avatarSprites.getSprite("layerBraces").getRegion();
        braces.zIndex = 1;
        braces.tint = Farmland.get().getLoadedSave().getLocalPlayer().avatar.bracesColor;
        guiBuilder.addImage(braces);

        GuiBuilder.ImageData shirt = new GuiBuilder.ImageData(avatarSprites.getSprite("layerShirt").getTexture());
        shirt.id = "localPlayerAvatarShirt";
        shirt.origin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Left);
        shirt.anchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Left);
        shirt.scale = new Vector2f(3f, 3f);
        shirt.position.y = 52;
        shirt.position.x = 46;
        shirt.region = avatarSprites.getSprite("layerShirt").getRegion();
        shirt.zIndex = 1;
        shirt.tint = Farmland.get().getLoadedSave().getLocalPlayer().avatar.shirtColor;
        guiBuilder.addImage(shirt);

        GuiBuilder.ImageData hat = new GuiBuilder.ImageData(avatarSprites.getSprite("layerHat").getTexture());
        hat.id = "localPlayerAvatarHat";
        hat.origin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Left);
        hat.anchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Left);
        hat.scale = new Vector2f(3f, 3f);
        hat.position.y = 52;
        hat.position.x = 46;
        hat.region = avatarSprites.getSprite("layerHat").getRegion();
        hat.zIndex = 1;
        hat.tint = Farmland.get().getLoadedSave().getLocalPlayer().avatar.hatColor;
        guiBuilder.addImage(hat);

        GuiBuilder.ImageData buttons = new GuiBuilder.ImageData(avatarSprites.getSprite("layerButtons").getTexture());
        buttons.id = "localPlayerAvatarButtons";
        buttons.origin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Left);
        buttons.anchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Left);
        buttons.scale = new Vector2f(3f, 3f);
        buttons.position.y = 52;
        buttons.position.x = 46;
        buttons.region = avatarSprites.getSprite("layerButtons").getRegion();
        buttons.zIndex = 1;
        buttons.tint = Farmland.get().getLoadedSave().getLocalPlayer().avatar.buttonsColor;
        guiBuilder.addImage(buttons);
    }

    public void initializeGameplay() {
        if (Farmland.get().getNetMode() == NetMode.Standalone) {
            Farmland.get().clientPlayerId.set(0);
        }

        if (Farmland.get().getNetMode() == NetMode.DedicatedServer) {
            Farmland.get().serverGameStarted.set(true);
        }

        Farmland.get().getLoadedSave().localPlayerId = Farmland.get().clientPlayerId.get();
        Farmland.get().getLoadedSave().turnEnded.add((dataType, data) -> onTurnEnded());
        getEntityByName("grid").getComponent(TurnTimerComponent.class).secondElapsed.add(((dataType, data) -> updateTimer()));
        getSceneManager().getCurrentScene().getWorldCamera().mouseMoved.add(new EventListener() {
            @Override
            public void onReceived(Class dataType, Event data) {
                if (!inPause && (Input.isKeyDown(Key.LeftAlt) || Input.isKeyDown(Key.RightAlt)) && Input.isMouseDown(MouseButton.Left)) {
                    Camera.MousePositionChanged realData = (Camera.MousePositionChanged) data;
                    getSceneManager().getCurrentScene().getWorldCamera().moveTo(realData.oldMousePosition, realData.newMousePosition, 1);
                }
            }
        }, "gameplayCameraMoved");
        economicComponent = new EconomicComponent();
    }

    @Override
    public void destroy() {
        ConsoleCommands.show = false;
        getSceneManager().getCurrentScene().getWorldCamera().mouseMoved.remove("gameplayCameraMoved");
    }

    @Override
    public void renderImGui() {
        if (Farmland.get().getLoadedSave() == null) {
            return;
        }

        if (showInventory.get()) {
            ImGuiUtils.setNextWindowWithSizeCentered(500, 300, ImGuiCond.Appearing);

            ImGui.begin(Resources.getLocalizedText("inventory"), showInventory);
            ImGui.text(Resources.getLocalizedText("yourMoney", Farmland.get().getLoadedSave().getLocalPlayer().money + "\n\n"));
            makeListOfPlayerItem();
            ImGui.end();
        }

        if (showMarket.get()) {
            ImGuiUtils.setNextWindowWithSizeCentered(500, 300, ImGuiCond.Appearing);

            ImGui.begin(Resources.getLocalizedText("market"), showMarket);
            ImGui.text(Resources.getLocalizedText("yourMoney", Farmland.get().getLoadedSave().getLocalPlayer().money + "\n\n"));
            ImGuiBuyingItem();
            ImGui.end();
        }

        if (showCaravan.get()) {
            ImGuiUtils.setNextWindowWithSizeCentered(600, 300, ImGuiCond.Appearing);

            ImGui.begin(Resources.getLocalizedText("caravans"), showCaravan);
            ImGui.text(Resources.getLocalizedText("yourMoney", Farmland.get().getLoadedSave().getLocalPlayer().money + "\n\n"));
            ImGuiBuyingCaravan();
            ImGui.end();
        }

        if (showResearch.get()) {
            ImGuiUtils.setNextWindowWithSizeCentered(500, 300, ImGuiCond.Appearing);

            ImGui.begin(Resources.getLocalizedText("research"), showResearch);
            ImGui.text(Resources.getLocalizedText("yourMoney", Farmland.get().getLoadedSave().getLocalPlayer().money + "\n\n"));
            ImGuiBuyingResearch();
            ImGui.end();
        }

        if (showBank.get()) {
            ImGuiUtils.setNextWindowWithSizeCentered(500, 300, ImGuiCond.Appearing);

            ImGui.begin(Resources.getLocalizedText("bank"), showBank);
            ImGui.text(Resources.getLocalizedText("yourMoney", Farmland.get().getLoadedSave().getLocalPlayer().money));
            ImGuiBank();
            ImGui.end();
        }
    }

    private void ImGuiBank(){
        Player player = Farmland.get().getLoadedSave().getLocalPlayer();
        int maxBorrow = Farmland.get().getLoadedSave().maxBorrow;

        if(ImGui.button(Resources.getLocalizedText("loan"))){
            selectBorrow.set(maxBorrow/10);
            refundMenu = false;
        }
        ImGui.sameLine();
        if(ImGui.button(Resources.getLocalizedText("refund"))){
            selectBorrow.set(1);
            refundMenu = true;
        }

        if(!refundMenu){
            if(player.remainingDebt == 0){
                player.loan = 0;
                ImGui.text(Resources.getLocalizedText("loanMaximum", player.remainingDebt, maxBorrow));
                ImGui.inputInt(Resources.getLocalizedText("loanAmount"), selectBorrow,maxBorrow/10);
                ImGui.text(Resources.getLocalizedText("loanSelected", selectBorrow.get()));
                if(!(selectBorrow.get() >= 5 && selectBorrow.get() <= maxBorrow)) {
                    if (selectBorrow.get() < 5)
                        selectBorrow.set(maxBorrow/10);
                    else if (selectBorrow.get() > player.remainingDebt)
                        selectBorrow.set(maxBorrow);
                }else{
                    if(ImGui.button(Resources.getLocalizedText("confirm"))){
                        player.takeLoan(selectBorrow.get());
                        updateMoneyItemLabel();
                        updateLeaderboard();
                        selectBorrow.set(maxBorrow/10);
                    }
                    ImGui.sameLine();
                    if(ImGui.button(Resources.getLocalizedText("cancel"))){
                        selectBorrow.set(maxBorrow/10);
                    }
                }
            }else{
                ImGui.text(Resources.getLocalizedText("loanToPay", player.remainingDebt));
                ImGui.text(Resources.getLocalizedText("loanNeedRefund"));
            }

        }else{
            if(player.remainingDebt > 0) {
                ImGui.text(Resources.getLocalizedText("debtYours", player.remainingDebt));
                ImGui.inputInt(Resources.getLocalizedText("debtAmount"), selectBorrow,maxBorrow/10);
                ImGui.text(Resources.getLocalizedText("debtSelected", selectBorrow.get()));
                if(!(selectBorrow.get() >= 1 && selectBorrow.get() <= player.remainingDebt)) {
                    if (selectBorrow.get() < 0)
                        selectBorrow.set(1);
                    else if (selectBorrow.get() > player.remainingDebt)
                        selectBorrow.set(player.remainingDebt);
                }else{
                    if(ImGui.button(Resources.getLocalizedText("confirm"))){
                        player.payLoan(selectBorrow.get());
                        updateMoneyItemLabel();
                        updateLeaderboard();
                        selectBorrow.set(1);
                    }
                    ImGui.sameLine();
                    if(ImGui.button(Resources.getLocalizedText("cancel"))){
                        selectBorrow.set(1);
                    }
                }
            }else{
                player.loan = 0;
                ImGui.text(Resources.getLocalizedText("debtNothing"));
            }
        }
    }

    private void ImGuiBuyingResearch(){
        Player player = Farmland.get().getLoadedSave().getLocalPlayer();

        ImGui.text(Resources.getLocalizedText("researches"));
        for (int i = 0; i < player.researches.size(); i++){
            Research research = player.researches.get(i);
            if (ImGui.button(Resources.getLocalizedText("researchUpgrade", research.getLocalizedName(), research.getPrice()))  && player.money > research.getPrice()) {
                if (Game.isDebugging()) {
                    Out.println("Upgrade: " + research.getType().name());
                }

                player.upgradeResearch(research.getType());

                updateMoneyItemLabel();
                updateLeaderboard();
                updatePlayerFrame();
            }
            ImGui.sameLine();
            ImGui.text(Resources.getLocalizedText("researchInfo", research.getLocalizedName(), research.getLevel(), research.getEffect()));
        }
    }

    private void ImGuiBuyingCaravan(){
        if(ImGui.button(Resources.getLocalizedText("send"))){
            caravanMenu = false;
        }
        ImGui.sameLine();
        if(ImGui.button(Resources.getLocalizedText("receive"))){
            caravanMenu = true;
        }

        Map<String, Item> playerInventory = Objects.requireNonNull(Farmland.get().getLoadedSave()).getLocalPlayer().getAllItemOfSellInventory();
        Set<String> uniqueItems = playerInventory.keySet();

        Player player = Farmland.get().getLoadedSave().getLocalPlayer();
        int playerMoney = player.money;

        if (!caravanMenu) {
            if (uniqueItems.isEmpty()) {
                ImGui.text(Resources.getLocalizedText("noSend"));
            } else {
                ImGui.text(Resources.getLocalizedText("sentCaravans"));

                for (String item : uniqueItems) {
                    int currentQuantity = playerInventory.get(item).quantity;
                    if (currentQuantity > 1) {
                        int fEffect = 0;
                        int eEffect = 0;
                        for (int i = 0; i < player.researches.size(); i++){
                            Research re = player.researches.get(i);
                            if (re.getType() == Research.Type.Farmer){
                                fEffect = re.getEffect();
                            } else if (re.getType() == Research.Type.Breeder){
                                eEffect = re.getEffect();
                            }
                        }

                        int researchBonus = (playerInventory.get(item) instanceof Crop)? fEffect : eEffect;

                        int sellValueOfCaravan = (int) (((Farmland.get().getLoadedSave().getResourceDatabase().get(item).sellingValue + researchBonus) * 1.5) * currentQuantity / 2);
                        int travelTime = 4;
                        int travelPrice = 10;
                        if (ImGui.button(Resources.getLocalizedText("sended") + playerInventory.get(item).getLocalizedName().toLowerCase() + " [" + travelPrice + "]") && playerInventory.get(item) != null) {
                            player.sendCaravan(travelPrice, travelTime, sellValueOfCaravan, item);
                        }
                        ImGui.sameLine();
                        if (playerInventory.get(item) == null) {
                            ImGui.text(Resources.getLocalizedText("noMoreCaravans"));
                        } else {
                            ImGui.text(playerInventory.get(item).getLocalizedName() + " (x" + currentQuantity / 2 + ")");
                        }
                        ImGui.sameLine();
                        ImGui.text(Resources.getLocalizedText("sellingPrice") + sellValueOfCaravan + Resources.getLocalizedText("estimatedTime") + travelTime);
                    } else {
                        ImGui.text(Resources.getLocalizedText("lackOf") + playerInventory.get(item).getLocalizedName().toLowerCase() + Resources.getLocalizedText("sent"));
                    }
                }
            }
        } else {
            if (player.caravans.isEmpty()){
                ImGui.text(Resources.getLocalizedText("noCaravans"));
            } else {
                ImGui.text(Resources.getLocalizedText("seeCaravans"));

                for (int i = 0; i < player.caravans.size() ; i++){
                    Caravan caravan = player.caravans.get(i);
                    ImGui.text(Resources.getLocalizedText("caravansFor", caravan.getReward(), caravan.getTotalTurn() - caravan.getTravelTurn()));
                }
            }
        }
    }

    private void ImGuiBuyingItem(){
        if(ImGui.button(Resources.getLocalizedText("purchase"))){
            sellMenu = false;
        }

        ImGui.sameLine();

        if(ImGui.button(Resources.getLocalizedText("sale"))){
            sellMenu = true;
        }

        Player player = Farmland.get().getLoadedSave().getLocalPlayer();
        int playerMoney = player.money;

        if(sellMenu){
            ImGui.text(Resources.getLocalizedText("sellItem"));

            Map<String, Item> playerInventory = Objects.requireNonNull(Farmland.get().getLoadedSave()).getLocalPlayer().getAllItemOfSellInventory();
            Set<String> uniqueItems = playerInventory.keySet();
            List<Item> toDelete = new ArrayList<>();

            if(uniqueItems.isEmpty()){
                ImGui.text(Resources.getLocalizedText("noSell"));
            }

            for (String item: uniqueItems){
                if (ImGui.button(sellnickNameItem(playerInventory.get(item)))) {
                    player.sellItem(item, 1);

                    updateMoneyItemLabel();
                    updateLeaderboard();
                }
                ImGui.sameLine();
                if(playerInventory.get(item) == null){
                    ImGui.text(Resources.getLocalizedText("possess") + ": x0" );
                }else{
                    ImGui.text(Resources.getLocalizedText("possess") + ": x" + playerInventory.get(item).quantity);
                }
                ImGui.sameLine();
                ImGui.text(Resources.getLocalizedText("resellPrice", Farmland.get().getLoadedSave().getResourceDatabase().get(item).sellingValue));
            }

            player.deleteFromInventory(toDelete, Player.InventoryType.WaitingToBeSold);

        }else{
            ImGui.text(Resources.getLocalizedText("products"));

            for(Item item : Farmland.get().getLoadedSave().getResourceDatabase().values()) {
                if (ImGui.button(buyNickNameItem(item)) && playerMoney>=item.buyingValue) {
                    Farmland.get().getLoadedSave().getLocalPlayer().buyItem(item, 1);
                    updateLeaderboard();
                }
                ImGui.sameLine();
                ImGui.text(Resources.getLocalizedText("sellPrice", item.buyingValue));
            }
        }
    }

    private String buyNickNameItem(Item item) {
        if (item instanceof Crop) {
            return ((Crop)item).getLocalizedSeedName();
        } else if (item instanceof Animal) {
            return ((Animal)item).getLocalizedBabyName();
        }

        return item.getLocalizedName();
    }

    private String sellnickNameItem(Item item){
        if (item instanceof Animal) {
            return Resources.getLocalizedText("meatOf", item.getLocalizedName().toLowerCase());
        } else {
            return item.getLocalizedName();
        }
    }

    private void makeListOfPlayerItem(){
        if (Farmland.get().getLoadedSave() != null && Farmland.get().getLoadedSave().getLocalPlayer().selectedItemId != null &&
                ImGui.button(Resources.getLocalizedText("unselect"))) {
            Farmland.get().getLoadedSave().getLocalPlayer().selectedItemId = null;
            updateMoneyItemLabel();
        }

        Map<String, Item> playerInventory = Objects.requireNonNull(Farmland.get().getLoadedSave()).getLocalPlayer().getAllItemOfBoughtInventory();
        Set<String> uniqueItems = playerInventory.keySet();

        if (uniqueItems.isEmpty()) {
            ImGui.text(Resources.getLocalizedText("emptyInventory"));
        } else {
            for(String item : uniqueItems){
                ImGui.text(buyNickNameItem(playerInventory.get(item)) + " (x" + playerInventory.get(item).quantity + ")");

                ImGui.sameLine();

                ImGui.pushID(item);

                if (ImGui.button(Resources.getLocalizedText("select"))) {
                    Farmland.get().getLoadedSave().getLocalPlayer().selectItem(item);
                    updateMoneyItemLabel();
                }

                ImGui.popID();
            }
        }
    }

    public void decreasePlayerDept(Player player, float percent){
        player.payLoan(Math.max((int)(player.loan * (percent / 100)), 1));

        updateLeaderboard();
        updateMoneyItemLabel();
    }

    public void onTurnEnded() {
        if (Farmland.get().getLoadedSave() == null) {
            return;
        }

        Player currentPlayer = Farmland.get().getLoadedSave().getCurrentPlayer();

        if(currentPlayer.remainingDebt > 0) {
            decreasePlayerDept(currentPlayer, Farmland.get().getLoadedSave().debtRate);
        }

        if (!Farmland.get().getLoadedSave().deadPlayers.contains(currentPlayer.getId())) {
            getEntityByName("stateLabel").getComponent(TextComponent.class).setText(Resources.getLocalizedText("turnInfo", (Farmland.get().getLoadedSave().turn + 1), Farmland.get().getLoadedSave().getCurrentPlayer().name));
            checkCaravan();
        }

        if (Game.get().hasAuthority() && Farmland.get().getLoadedSave().getCurrentPlayer().getId().equals(0)) {
            for (int x = 0; x < Farmland.get().getLoadedSave().cells.size(); x++) {
                for (int y = 0; y < Farmland.get().getLoadedSave().cells.get(x).size(); y++) {
                    Cell cell = Farmland.get().getLoadedSave().cells.get(x).get(y);

                    if (cell.isOwned()){
                        Player player = Farmland.get().getLoadedSave().players.get(cell.ownerId);
                        player.setMoney(player.money - 1);
                    }

                    if (cell.hasItem()) {
                        if (!cell.item.shouldBeDestroyed()) {
                           cell.item.endTurn();
                        } else if (Farmland.get().fastHarvest || cell.isOwnedByBot()) {
                            Player player = Farmland.get().getLoadedSave().players.get(cell.ownerId);
                            player.addToInventory(cell.item, Player.InventoryType.WaitingToBeSold);
                            cell.item = null;
                        }
                    }
                }
            }

            onCompletedTurnEnd();

            updateLeaderboard();
        }

        if (Farmland.get().getNetMode() != NetMode.DedicatedServer) {
            if (Farmland.get().getLoadedSave() == null) {
                return;
            }

            updateGameplayButtons();
        }
    }

    public void updatePlayerFrame(){
        Player player = Farmland.get().getLoadedSave().getLocalPlayer();
        int fLevel = 0;
        int eLevel = 0;
        for (int i = 0; i < player.researches.size(); i++){
            Research re = player.researches.get(i);
            if (re.getType() == Research.Type.Farmer){
                fLevel = re.getLevel();
            } else if (re.getType() == Research.Type.Breeder){
                eLevel = re.getLevel();
            }
        }

        if (eLevel > 4){
            if (fLevel > 4){
                getEntityByName("frameImage").getComponent(SpriteComponent.class).setSprite(new Sprite(Resources.loadTexture("ui/farmer2breeder2.png")));
            } else if (fLevel > 2){
                getEntityByName("frameImage").getComponent(SpriteComponent.class).setSprite(new Sprite(Resources.loadTexture("ui/farmerbreeder2.png")));
            } else {
                getEntityByName("frameImage").getComponent(SpriteComponent.class).setSprite(new Sprite(Resources.loadTexture("ui/breeder2.png")));
            }
        } else if (eLevel > 2){
            if (fLevel > 4){
                getEntityByName("frameImage").getComponent(SpriteComponent.class).setSprite(new Sprite(Resources.loadTexture("ui/farmer2breeder.png")));
            } else if (fLevel > 2){
                getEntityByName("frameImage").getComponent(SpriteComponent.class).setSprite(new Sprite(Resources.loadTexture("ui/farmerbreeder.png")));
            } else {
                getEntityByName("frameImage").getComponent(SpriteComponent.class).setSprite(new Sprite(Resources.loadTexture("ui/breeder.png")));
            }
        } else if (fLevel > 4){
            getEntityByName("frameImage").getComponent(SpriteComponent.class).setSprite(new Sprite(Resources.loadTexture("ui/farmer2.png")));

        } else if (fLevel > 2){
            getEntityByName("frameImage").getComponent(SpriteComponent.class).setSprite(new Sprite(Resources.loadTexture("ui/farmer.png")));
        }
    }

    public void updateGameplayButtons() {
        if (Farmland.get().getNetMode() != NetMode.DedicatedServer) {
            if (Farmland.get().getLoadedSave() != null &&
                    !Farmland.get().getLoadedSave().getCurrentPlayer().getId().equals(Farmland.get().getLoadedSave().getLocalPlayer().getId())) {
                getEntityByName("gameplayButtons").setEnabled(false);
                checkIfWeShouldHideUi();
            } else {
                getEntityByName("gameplayButtons").setEnabled(true);
                checkIfWeShouldShowBackUi();
            }
        }
    }

    public void transitionToResultScene(boolean hasWon) {
        ResultMenu resultMenu = new ResultMenu();
        resultMenu.currentSave = Farmland.get().getLoadedSave();
        resultMenu.isWin = hasWon;
        Farmland.get().unloadSave();
        changeScene(resultMenu);
    }

    public void onCompletedTurnEnd() {
        // Update the economy
        Farmland.get().getLoadedSave().fillBuyItemDataBasePerTurn();
        economicComponent.changeValueOfRessource();
        Farmland.get().getLoadedSave().clearTurnItemDatabase();

        // Check for any bot that have lost
        for (Player player : Farmland.get().getLoadedSave().players) {
            if (player.isBot() && player.hasLost() && !player.isDead()) {
                Farmland.get().getLoadedSave().kill(player);
            }
        }

        // Check for any human that have lost
        for (Player player : Farmland.get().getLoadedSave().players) {
            if (player.isHuman() && player.hasLost() && !player.isDead()) {
                Farmland.get().getLoadedSave().kill(player);

                if (Game.get().getNetMode() == NetMode.Standalone) {
                    transitionToResultScene(false);

                    return;
                } else if (Game.get().getNetMode() == NetMode.DedicatedServer) {
                    Game.get().getServer().send(Farmland.get().getClientId(player.getId()), new EndGameMessage(false));
                }
            }
        }

        // Check for any bot that have won
        for (Player player : Farmland.get().getLoadedSave().players) {
            if (player.isBot() && player.hasWon()) {
                if (Game.get().getNetMode() == NetMode.Standalone) {
                    transitionToResultScene(false);
                } else if (Game.get().getNetMode() == NetMode.DedicatedServer) {
                    Game.get().getServer().broadcast(new EndGameMessage(false));
                    Farmland.get().serverGameEnded();
                }

                return;
            }
        }

        // Check for any human that have won
        for (Player player : Farmland.get().getLoadedSave().players) {
            if (player.isHuman() && (player.hasWon() || (!player.isAloneInGame() && player.isOnlySurvivor()))) {
                if (Game.get().getNetMode() == NetMode.Standalone) {
                    transitionToResultScene(true);
                } else if (Game.get().getNetMode() == NetMode.DedicatedServer) {
                    for (Player player2 : Farmland.get().getLoadedSave().players) {
                        if (player2.isHuman()) {
                            Game.get().getServer().send(Farmland.get().getClientId(player2.getId()),
                                    new EndGameMessage(player == player2));
                        }
                    }
                    Farmland.get().serverGameEnded();
                }

                return;
            }
        }

        // Check if all players are dead on the dedicated server (to stop the game).
        if (Farmland.get().getLoadedSave().areAllPlayersDead() && Game.get().getNetMode() == NetMode.DedicatedServer) {
            Farmland.get().serverGameEnded();
        }
    }

    public List<Player> leaderBoardMaker(List<Player> list){
        Player[] tmp = new Player[list.size()];
        for (int i = 0; i < list.size(); i++) {
            tmp[i] = list.get(i);
        }
        for (int i = 0; i < list.size(); i++) {
            for (int j = i; j > 0; j--){
                if (Farmland.get().getLoadedSave().deadPlayers.contains(tmp[j].getId()) || tmp[j-1].money > tmp[j].money){
                    Player tmpP = tmp[j-1];
                    tmp[j-1] = tmp[j];
                    tmp[j] = tmpP;
                }
            }
        }
        List<Player> res = new ArrayList<>();
        for (int i = tmp.length-1; i >= 0 ; i--){
            res.add(tmp[i]);
        }
        return res;
    }

    public void updateLeaderboard(){
        if (Farmland.get().getLoadedSave() == null) {
            return;
        }

        List<Player> leaderBoardList = leaderBoardMaker(Farmland.get().getLoadedSave().players);
        StringBuilder leaderBoard = new StringBuilder(Resources.getLocalizedText("leaderboard"));
        for (Player player : leaderBoardList) {
            leaderBoard.append("\n\n").append(player.name).append(" : ").append(Farmland.get().getLoadedSave().deadPlayers.contains(player.getId()) ? "dead" : player.money);
        }
        getEntityByName("LeaderBoardLabel").getComponent(TextComponent.class).setText(leaderBoard.toString());
    }

    public void checkCaravan() {
        Player player = Farmland.get().getLoadedSave().getCurrentPlayer();

        if (!player.caravans.isEmpty()){
            List<Caravan> toDelete = new ArrayList<>();

            for (int i = 0; i < player.caravans.size() ; i++) {
                Caravan caravan = player.caravans.get(i);

                caravan.turnDone();

                if (caravan.hasArrived()) {
                    player.setMoney(player.money + caravan.getReward());
                    toDelete.add(player.caravans.get(i));
                }

            }
            player.caravans.removeAll(toDelete);
        }
    }

    public void updateMoneyItemLabel() {
        if (Farmland.get().getLoadedSave() == null) {
            return;
        }

        if (Game.get().getNetMode() == NetMode.Client || Game.get().getNetMode() == NetMode.Standalone) {
            String selectedId = Farmland.get().getLoadedSave().getLocalPlayer().selectedItemId;
            String text = "";
            if (Farmland.get().getLoadedSave().getLocalPlayer().selectedItemId != null && Farmland.get().getLoadedSave().getLocalPlayer().getAllItemOfBoughtInventory().containsKey(selectedId)) {
                String itemName = Farmland.get().getItem(selectedId).getLocalizedName();

                if (Farmland.get().getItem(selectedId) instanceof Crop) {
                    itemName = ((Crop)Farmland.get().getItem(selectedId)).getLocalizedSeedName();
                } else if (Farmland.get().getItem(selectedId) instanceof Animal) {
                    itemName = ((Animal)Farmland.get().getItem(selectedId)).getLocalizedBabyName();
                }

                text += Resources.getLocalizedText("selected", itemName, Farmland.get().getLoadedSave().getLocalPlayer().getAllItemOfBoughtInventory().get(selectedId).quantity);
            }
            String text2 = "    " + Farmland.get().getLoadedSave().getLocalPlayer().money;
            getEntityByName("selectedLabel").getComponent(TextComponent.class).setText(text);
            getEntityByName("MoneyLabel").getComponent(TextComponent.class).setText(text2);
            moneyUpdate();
        }
    }

    public void moneyUpdate(){
        Player player = Farmland.get().getLoadedSave().getLocalPlayer();
        int playerMoney = player.money;
        if (playerMoney >= 750){
            getEntityByName("MoneyImage").getComponent(SpriteComponent.class).setSprite(new Sprite(Resources.loadTexture("ui/coin4.png")));
        } else if (playerMoney >= 500){
            getEntityByName("MoneyImage").getComponent(SpriteComponent.class).setSprite(new Sprite(Resources.loadTexture("ui/coin3.png")));
        } else if (playerMoney >= 250){
            getEntityByName("MoneyImage").getComponent(SpriteComponent.class).setSprite(new Sprite(Resources.loadTexture("ui/coin2.png")));
        } else {
            getEntityByName("MoneyImage").getComponent(SpriteComponent.class).setSprite(new Sprite(Resources.loadTexture("ui/coin.png")));
        }
    }

    public void updateTimer() {
        getEntityByName("timeRemainingLabel").getComponent(TextComponent.class).setText(Resources.getLocalizedText("timeRemaining", DateUtil.secondsToText(Save.timePerTurn - Farmland.get().getLoadedSave().turnTimePassed)));
        getEntityByName("timePassedLabel").getComponent(TextComponent.class).setText(Resources.getLocalizedText("timePassed", DateUtil.secondsToText(Farmland.get().getLoadedSave().timePassed)));
    }

    @Override
    public void update(float dt) {
        if (Farmland.get().isConnectedToServer()) {
            Save serverSave = LoadSaveResponse.getUpdatedSaveGame();

            if (serverSave != null) {
                Save currentSave = Farmland.get().getLoadedSave();

                if (currentSave != null) {
                    serverSave.turnEnded = currentSave.turnEnded;
                }

                Farmland.get().saves.put(Farmland.get().loadedSaveId, serverSave);

                if (currentSave != null) {
                    if (!serverSave.currentPlayerId.equals(currentSave.currentPlayerId) || !serverSave.turn.equals(currentSave.turn)) {
                        Farmland.get().getLoadedSave().turnEnded.dispatch();
                    }
                }


                updateMoneyItemLabel();
                updateLeaderboard();
                updateTimer();
                updatePlayerFrame();
            }
        }

        if (Input.isMouseRelease(MouseButton.Middle)) {
            if (Farmland.get().getLoadedSave().getLocalPlayer().getId().equals(Farmland.get().getLoadedSave().getCurrentPlayer().getId())) {
                if (Farmland.get().getLoadedSave().getLocalPlayer().selectedItemId != null) {
                    lastSelectedItemId = Farmland.get().getLoadedSave().getLocalPlayer().selectedItemId;
                    Farmland.get().getLoadedSave().getLocalPlayer().selectItem(null);
                } else if (lastSelectedItemId != null) {
                    Farmland.get().getLoadedSave().getLocalPlayer().selectedItemId = null;
                    Farmland.get().getLoadedSave().getLocalPlayer().selectItem(lastSelectedItemId);
                }

                updateMoneyItemLabel();
            }
        }

        if (Game.get().hasAuthority() && (Input.isKeyReleased(Key.P) || Input.isKeyReleased(Key.Escape))) {
            setPause(!inPause);
        }
    }

    public void setPause(boolean inPause) {
        if (Farmland.get().getLoadedSave() == null) {
            return;
        }

        this.inPause = inPause;
        pauseChanged.dispatch(new PauseChanged(inPause));
        ButtonComponent.disableInput = inPause;

        if (inPause) {
            if (Game.get().getNetMode() != NetMode.DedicatedServer) {
                checkIfWeShouldHideUi();
            }
        } else if (Farmland.get().getLoadedSave().getLocalPlayer().getId().equals(Farmland.get().getLoadedSave().getCurrentPlayer().getId())) {
            if (Game.get().getNetMode() != NetMode.DedicatedServer) {
                checkIfWeShouldShowBackUi();
            }
        }
    }

    public boolean getPause() {
        return inPause;
    }

    public void updateUi() {
        updateMoneyItemLabel();
        updateLeaderboard();
        updateTimer();
        updatePlayerFrame();
    }

    public void checkIfWeShouldHideUi() {
        if (showMarket.get()) {
            shouldShowBackMarket = true;
        }
        if (showInventory.get()) {
            shouldShowBackInventory = true;
        }
        if (showCaravan.get()) {
            shouldShowBackCaravan = true;
        }
        if (showResearch.get()) {
            shouldShowBackResearch = true;
        }
        if (showBank.get()) {
            shouldShowBackBank = true;
        }
        showInventory.set(false);
        showMarket.set(false);
        showCaravan.set(false);
        showResearch.set(false);
        showBank.set(false);
    }

    public void checkIfWeShouldShowBackUi() {
        if (shouldShowBackInventory) {
            showInventory.set(true);
            shouldShowBackInventory = false;
        }
        if (shouldShowBackMarket) {
            showMarket.set(true);
            shouldShowBackMarket = false;
        }
        if (shouldShowBackCaravan) {
            showCaravan.set(true);
            shouldShowBackCaravan = false;
        }
        if (shouldShowBackResearch) {
            showResearch.set(true);
            shouldShowBackResearch = false;
        }
        if (shouldShowBackBank) {
            showBank.set(true);
            shouldShowBackBank = false;
        }
    }
}
