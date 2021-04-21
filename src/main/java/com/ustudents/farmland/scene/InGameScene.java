package com.ustudents.farmland.scene;

import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.graphic.imgui.ImGuiUtils;
import com.ustudents.engine.scene.component.graphics.SpriteComponent;
import com.ustudents.engine.scene.ecs.Entity;
import com.ustudents.engine.scene.component.core.TransformComponent;
import com.ustudents.engine.scene.component.graphics.WorldRendererComponent;
import com.ustudents.engine.scene.component.gui.TextComponent;
import com.ustudents.engine.graphic.*;
import com.ustudents.engine.gui.GuiBuilder;
import com.ustudents.engine.scene.Scene;
import com.ustudents.engine.utility.DateUtil;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.component.EconomicComponent;
import com.ustudents.farmland.component.GridComponent;
import com.ustudents.farmland.component.PlayerMovementComponent;
import com.ustudents.farmland.component.TurnTimerComponent;
import com.ustudents.farmland.core.SaveGame;
import com.ustudents.farmland.core.grid.Cell;
import com.ustudents.farmland.core.item.*;
import com.ustudents.farmland.core.player.Player;
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

public class InGameScene extends Scene {
    public ImBoolean showInventory;

    public ImBoolean showMarket;

    public ImBoolean showCaravan;

    public ImBoolean showResearch;

    public ImBoolean showBank;

    public ImInt selectBorrow;

    public EconomicComponent economicComponent;

    public boolean sellMenu;

    public boolean caravanMenu;

    public boolean refundMenu;

    @Override
    public void initialize() {
        forceImGui = true;

        showInventory = new ImBoolean(false);
        showMarket = new ImBoolean(false);
        showCaravan = new ImBoolean(false);
        showResearch = new ImBoolean(false);
        showBank = new ImBoolean(false);
        selectBorrow = new ImInt(Farmland.get().getCurrentSave().maxBorrow/10);

        initializeEntities();
        initializeGui();
        initializeGameplay();
        economicComponent = new EconomicComponent();
        checkPlayerFrame();
        moneyUpdate();

        if (Farmland.get().getCurrentSave().getCurrentPlayer().name.contains("Robot")) {
            getEntityByName("endTurnButton").setEnabled(false);
            getEntityByName("inventoryButton").setEnabled(false);
            getEntityByName("marketButton").setEnabled(false);
            getEntityByName("caravanButton").setEnabled(false);
            getEntityByName("researchButton").setEnabled(false);
        }
    }

    public void initializeEntities() {
        if (hasEntityWithName("grid")) {
            killEntity(getEntityByName("grid"), true);
        }

        NineSlicedSprite gridBackground = new NineSlicedSprite(Resources.loadSpritesheet("ui/map_background.json"));
        Texture cellBackground = Resources.loadTexture("map/grass.png");
        AnimatedSprite selectionCursor = new AnimatedSprite(Resources.loadSpritesheet("ui/map_cell_cursor.json"));
        Spritesheet territoryTexture = Resources.loadSpritesheet("ui/map_territory_indicator_white.json");

        Entity grid = createEntityWithName("map");
        grid.addComponent(new TransformComponent());
        grid.addComponent(new WorldRendererComponent());
        grid.addComponent(new GridComponent(new Vector2i(Farmland.get().getCurrentSave().cells.size(), Farmland.get().getCurrentSave().cells.get(0).size()), new Vector2i(24, 24), gridBackground, cellBackground, selectionCursor, territoryTexture));
        grid.addComponent(new TurnTimerComponent(SaveGame.timePerTurn));
        grid.getComponent(GridComponent.class).onItemUsed.add((dataType, data) -> onSelectedItemOrMoneyChanged());
        Farmland.get().getCurrentSave().players.get(0).moneyChanged.add((dataType, data) -> onSelectedItemOrMoneyChanged());

        Entity player = createEntityWithName("player");
        player.addComponent(new PlayerMovementComponent(500.0f));
    }

    public void initializeGui() {
        GuiBuilder guiBuilder = new GuiBuilder();


        Texture researchTexture = Resources.loadTexture("ui/research.png");
        GuiBuilder.ImageData imageDataResearch= new GuiBuilder.ImageData(researchTexture);
        imageDataResearch.id = "ResearchImage";
        imageDataResearch.origin = new Origin(Origin.Vertical.Bottom, Origin.Horizontal.Right);
        imageDataResearch.anchor = new Anchor(Anchor.Vertical.Bottom, Anchor.Horizontal.Right);
        imageDataResearch.position = new Vector2f(-642, -110);
        imageDataResearch.scale = new Vector2f(3f, 3f);
        imageDataResearch.zIndex = 2;
        guiBuilder.addImage(imageDataResearch);

        Texture caravanTexture = Resources.loadTexture("ui/caravan.png");
        GuiBuilder.ImageData imageDataCaravan= new GuiBuilder.ImageData(caravanTexture);
        imageDataCaravan.id = "ResearchImage";
        imageDataCaravan.origin = new Origin(Origin.Vertical.Bottom, Origin.Horizontal.Right);
        imageDataCaravan.anchor = new Anchor(Anchor.Vertical.Bottom, Anchor.Horizontal.Right);
        imageDataCaravan.position = new Vector2f(-465, -110);
        imageDataCaravan.scale = new Vector2f(3f, 3f);
        imageDataCaravan.zIndex = 2;
        guiBuilder.addImage(imageDataCaravan);

        Texture inventoryTexture = Resources.loadTexture("ui/inventory.png");
        GuiBuilder.ImageData imageDataInventory= new GuiBuilder.ImageData(inventoryTexture);
        imageDataInventory.id = "ResearchImage";
        imageDataInventory.origin = new Origin(Origin.Vertical.Bottom, Origin.Horizontal.Right);
        imageDataInventory.anchor = new Anchor(Anchor.Vertical.Bottom, Anchor.Horizontal.Right);
        imageDataInventory.position = new Vector2f(-145, -110);
        imageDataInventory.scale = new Vector2f(3f, 3f);
        imageDataInventory.zIndex = 2;
        guiBuilder.addImage(imageDataInventory);


        Texture marketTexture = Resources.loadTexture("ui/market.png");
        GuiBuilder.ImageData imageDataMarket= new GuiBuilder.ImageData(marketTexture);
        imageDataMarket.id = "ResearchImage";
        imageDataMarket.origin = new Origin(Origin.Vertical.Bottom, Origin.Horizontal.Right);
        imageDataMarket.anchor = new Anchor(Anchor.Vertical.Bottom, Anchor.Horizontal.Right);
        imageDataMarket.position = new Vector2f(-320, -112);
        imageDataMarket.scale = new Vector2f(3f, 3f);
        imageDataMarket.zIndex = 2;
        guiBuilder.addImage(imageDataMarket);

        Texture turnEndTexture = Resources.loadTexture("ui/time.png");
        GuiBuilder.ImageData imageDataTurnEnd= new GuiBuilder.ImageData(turnEndTexture);
        imageDataTurnEnd.id = "ResearchImage";
        imageDataTurnEnd.origin = new Origin(Origin.Vertical.Bottom, Origin.Horizontal.Right);
        imageDataTurnEnd.anchor = new Anchor(Anchor.Vertical.Bottom, Anchor.Horizontal.Right);
        imageDataTurnEnd.position = new Vector2f(40, -114);
        imageDataTurnEnd.scale = new Vector2f(3f, 3f);
        imageDataTurnEnd.zIndex = 2;
        guiBuilder.addImage(imageDataTurnEnd);

        Texture bankTexture = Resources.loadTexture("ui/bank.png");
        GuiBuilder.ImageData imageDataBank= new GuiBuilder.ImageData(bankTexture);
        imageDataBank.id = "ResearchImage";
        imageDataBank.origin = new Origin(Origin.Vertical.Bottom, Origin.Horizontal.Right);
        imageDataBank.anchor = new Anchor(Anchor.Vertical.Bottom, Anchor.Horizontal.Right);
        imageDataBank.position = new Vector2f(-800, -110);
        imageDataBank.scale = new Vector2f(3f, 3f);
        imageDataBank.zIndex = 2;
        guiBuilder.addImage(imageDataBank);

        Texture frameTexture = Resources.loadTexture("ui/frame.png");
        GuiBuilder.ImageData imageDataFrame = new GuiBuilder.ImageData(frameTexture);
        imageDataFrame.id = "FrameImage";
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

        if (Farmland.get().getCurrentSave().players.size() >= 2) {
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

        if (Farmland.get().getCurrentSave().players.size() >= 3) {
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
        imageDataLB.id = "BTImage";
        imageDataLB.origin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Right);
        imageDataLB.anchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Right);
        imageDataLB.scale = new Vector2f(2f, 2f);
        imageDataLB.position = new Vector2f(-200, -5);
        imageDataLB.zIndex = 2;
        guiBuilder.addImage(imageDataLB);

        GuiBuilder.ButtonData buttonData = new GuiBuilder.ButtonData("Finir le tour", (dataType, data) -> {
            Farmland.get().getCurrentSave().endTurn();
        });
        buttonData.id = "endTurnButton";
        buttonData.origin = new Origin(Origin.Vertical.Bottom, Origin.Horizontal.Right);
        buttonData.anchor = new Anchor(Anchor.Vertical.Bottom, Anchor.Horizontal.Right);
        buttonData.position = new Vector2f(-10, -10);
        guiBuilder.addButton(buttonData);

        GuiBuilder.ButtonData buttonData1 = new GuiBuilder.ButtonData("Inventaire", (dataType, data) -> {
            showInventory.set(!showInventory.get());
        });
        buttonData1.id = "inventoryButton";
        buttonData1.origin = new Origin(Origin.Vertical.Bottom, Origin.Horizontal.Right);
        buttonData1.anchor = new Anchor(Anchor.Vertical.Bottom, Anchor.Horizontal.Right);
        buttonData1.position = new Vector2f(-220, -10);
        guiBuilder.addButton(buttonData1);

        GuiBuilder.ButtonData buttonData3 = new GuiBuilder.ButtonData("Marché", (dataType, data) -> {
            showMarket.set(!showMarket.get());
        });
        buttonData3.id = "marketButton";
        buttonData3.origin = new Origin(Origin.Vertical.Bottom, Origin.Horizontal.Right);
        buttonData3.anchor = new Anchor(Anchor.Vertical.Bottom, Anchor.Horizontal.Right);
        buttonData3.position = new Vector2f(-400, -8);
        guiBuilder.addButton(buttonData3);

        GuiBuilder.ButtonData buttonDataC = new GuiBuilder.ButtonData("Caravanes", (dataType, data) -> {
            showCaravan.set(!showCaravan.get());
        });
        buttonDataC.id = "caravanButton";
        buttonDataC.origin = new Origin(Origin.Vertical.Bottom, Origin.Horizontal.Right);
        buttonDataC.anchor = new Anchor(Anchor.Vertical.Bottom, Anchor.Horizontal.Right);
        buttonDataC.position = new Vector2f(-535, -10);
        guiBuilder.addButton(buttonDataC);

        GuiBuilder.ButtonData buttonDataR = new GuiBuilder.ButtonData("Recherche", (dataType, data) -> {
            showResearch.set(!showResearch.get());
        });
        buttonDataR.id = "researchButton";
        buttonDataR.origin = new Origin(Origin.Vertical.Bottom, Origin.Horizontal.Right);
        buttonDataR.anchor = new Anchor(Anchor.Vertical.Bottom, Anchor.Horizontal.Right);
        buttonDataR.position = new Vector2f(-715, -10);
        guiBuilder.addButton(buttonDataR);

        GuiBuilder.ButtonData buttonDataB = new GuiBuilder.ButtonData("Banque", (dataType, data) -> {
            showBank.set(!showBank.get());
        });
        buttonDataB.id = "bankButton";
        buttonDataB.origin = new Origin(Origin.Vertical.Bottom, Origin.Horizontal.Right);
        buttonDataB.anchor = new Anchor(Anchor.Vertical.Bottom, Anchor.Horizontal.Right);
        buttonDataB.position = new Vector2f(-880, -10);
        guiBuilder.addButton(buttonDataB);

        GuiBuilder.ButtonData buttonData2 = new GuiBuilder.ButtonData("Menu principal", (dataType, data) -> {
            Farmland.get().saveId = null;
            if (getGame().isConnectedToServer()) {
                getGame().disconnectFromServer();
            }
            changeScene(new MainMenu());
        });
        buttonData2.origin = new Origin(Origin.Vertical.Bottom, Origin.Horizontal.Left);
        buttonData2.anchor = new Anchor(Anchor.Vertical.Bottom, Anchor.Horizontal.Left);
        buttonData2.position = new Vector2f(10, -10);
        guiBuilder.addButton(buttonData2);

        GuiBuilder.WindowData windowData = new GuiBuilder.WindowData();
        windowData.origin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Center);
        windowData.anchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Center);
        windowData.position.y += 5;
        guiBuilder.beginWindow(windowData);

        // Happens within the window.
        {
            GuiBuilder.TextData textData = new GuiBuilder.TextData("Tour " + (Farmland.get().getCurrentSave().turn + 1) + " de " + Farmland.get().getCurrentSave().getCurrentPlayer().name);
            textData.id = "stateLabel";
            textData.origin = new Origin(Origin.Vertical.Middle, Origin.Horizontal.Center);
            textData.anchor = new Anchor(Anchor.Vertical.Middle, Anchor.Horizontal.Center);
            textData.color = Color.BLACK;
            guiBuilder.addText(textData);
        }

        guiBuilder.endWindow();

        GuiBuilder.TextData textDataTime = new GuiBuilder.TextData("Temps : " + DateUtil.secondsToText(Farmland.get().getCurrentSave().timePassed));
        textDataTime.id = "timePassedLabel";
        textDataTime.origin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Center);
        textDataTime.anchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Center);
        textDataTime.position = new Vector2f(0, 95);
        textDataTime.color = Color.BLACK;
        guiBuilder.addText(textDataTime);

        GuiBuilder.TextData textData = new GuiBuilder.TextData("Temps restant: " + DateUtil.secondsToText(SaveGame.timePerTurn - Farmland.get().getCurrentSave().turnTimePassed));
        textData.id = "timeRemainingLabel";
        textData.origin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Center);
        textData.anchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Center);
        textData.position = new Vector2f(0, 65);
        textData.color = Color.BLACK;
        guiBuilder.addText(textData);

        String selectedId = Farmland.get().getCurrentSave().getCurrentPlayer().selectedItemID;
        String text = "";
        if (Farmland.get().getCurrentSave().getCurrentPlayer().selectedItemID != null) {
            text += "\n\nSélectionné: " + Farmland.get().getItem(selectedId).name + " (x" + Farmland.get().getCurrentSave().getCurrentPlayer().getAllItemOfBoughtInventory().get(selectedId).quantity + ")";
        }
        GuiBuilder.TextData textData2 = new GuiBuilder.TextData(text);
        textData2.id = "selectedLabel";
        textData2.origin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Left);
        textData2.anchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Left);
        textData2.position = new Vector2f(10, 155);
        textData2.color = Color.BLACK;

        guiBuilder.addText(textData2);

        String text2 = "    " + Farmland.get().getCurrentSave().getCurrentPlayer().money;
        GuiBuilder.TextData textDataMoney = new GuiBuilder.TextData(text2);
        textDataMoney.id = "MoneyLabel";
        textDataMoney.origin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Left);
        textDataMoney.anchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Left);
        textDataMoney.position = new Vector2f(190, 50);
        textDataMoney.color = Color.BLACK;

        guiBuilder.addText(textDataMoney);

        if (Farmland.get().getCurrentSave().deadPlayers == null) {
            Farmland.get().getCurrentSave().deadPlayers = new ArrayList<>();
        }

        List<Player> leaderBoardList = leaderBoardMaker(Farmland.get().getCurrentSave().players);
        String leaderBoard = "    LeaderBoard";
        for (Player player : leaderBoardList) {
            leaderBoard += "\n\n" + player.name + " : " + (Farmland.get().getCurrentSave().deadPlayers.contains(player.getId()) ? "dead" : player.money);
        }
        GuiBuilder.TextData textData3 = new GuiBuilder.TextData(leaderBoard);
        textData3.id = "LeaderBoardLabel";
        textData3.origin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Right);
        textData3.anchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Right);
        textData3.position = new Vector2f(-20, 10);
        textData3.color = Color.BLACK;

        guiBuilder.addText(textData3);

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
        braces.tint = Farmland.get().getCurrentSave().players.get(0).avatar.bracesColor;
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
        shirt.tint = Farmland.get().getCurrentSave().players.get(0).avatar.shirtColor;
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
        hat.tint = Farmland.get().getCurrentSave().players.get(0).avatar.hatColor;
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
        buttons.tint = Farmland.get().getCurrentSave().players.get(0).avatar.buttonsColor;
        guiBuilder.addImage(buttons);
    }

    public void initializeGameplay() {
        Farmland.get().getCurrentSave().turnEnded.add((dataType, data) -> onTurnEnded());
        getEntityByName("map").getComponent(TurnTimerComponent.class).secondElapsed.add(((dataType, data) -> onSecondElapsed(((TurnTimerComponent.SecondElapsed)data).numberOfSecondElapsed)));
    }

    @Override
    public void renderImGui() {
        if (Farmland.get().getCurrentSave() == null) {
            return;
        }

        if (showInventory.get()) {
            ImGuiUtils.setNextWindowWithSizeCentered(500, 300, ImGuiCond.Appearing);

            ImGui.begin("Inventaire", showInventory);
            ImGui.text("Votre argent : " + Farmland.get().getCurrentSave().getCurrentPlayer().money + "\n\n");
            makeListOfPlayerItem();
            ImGui.end();
        }

        if (showMarket.get()) {
            ImGuiUtils.setNextWindowWithSizeCentered(500, 300, ImGuiCond.Appearing);

            ImGui.begin("Marché", showMarket);
            ImGui.text("Votre argent : " + Farmland.get().getCurrentSave().getCurrentPlayer().money + "\n\n");
            ImGuiBuyingItem();
            ImGui.end();
        }

        if (showCaravan.get()) {
            ImGuiUtils.setNextWindowWithSizeCentered(500, 300, ImGuiCond.Appearing);

            ImGui.begin("Caravanes", showCaravan);
            ImGui.text("Votre argent : " + Farmland.get().getCurrentSave().getCurrentPlayer().money + "\n\n");
            ImGuiBuyingCaravan();
            ImGui.end();
        }

        if (showResearch.get()) {
            ImGuiUtils.setNextWindowWithSizeCentered(500, 300, ImGuiCond.Appearing);

            ImGui.begin("Recherche", showResearch);
            ImGui.text("Votre argent : " + Farmland.get().getCurrentSave().getCurrentPlayer().money + "\n\n");
            ImGuiBuyingResearch();
            ImGui.end();
        }

        if (showBank.get()) {
            ImGuiUtils.setNextWindowWithSizeCentered(500, 300, ImGuiCond.Appearing);

            ImGui.begin("Banque", showBank);
            ImGui.text("Votre argent : " + Farmland.get().getCurrentSave().getCurrentPlayer().money + "\n\n");
            ImGuiBank();
            ImGui.end();
        }
    }

    private void ImGuiBank(){
        Player player = Farmland.get().getCurrentSave().getCurrentPlayer();
        int maxBorrow = Farmland.get().getCurrentSave().maxBorrow;

        if(ImGui.button("Emprunt")){
            selectBorrow.set(maxBorrow/10);
            refundMenu = false;
        }
        ImGui.sameLine();
        if(ImGui.button("Remboursement")){
            selectBorrow.set(1);
            refundMenu = true;
        }

        if(!refundMenu){
            if(player.debtMoney == 0){
                player.loanMoney = 0;
                ImGui.text("\n\n" + "Maximum d'emprunt : " + player.debtMoney+ "/" + maxBorrow +"\n\n");
                ImGui.inputInt("Somme à emprunter", selectBorrow,maxBorrow/10);
                ImGui.text("Valeur d'emprunt sélectionné: " + selectBorrow.get() + "\n\n\n\n");
                if(!(selectBorrow.get() >= 5 && selectBorrow.get() <= maxBorrow)) {
                    if (selectBorrow.get() < 5)
                        selectBorrow.set(maxBorrow/10);
                    else if (selectBorrow.get() > player.debtMoney)
                        selectBorrow.set(maxBorrow);
                }else{
                    if(ImGui.button("Confirmer")){
                        player.money+= selectBorrow.get();
                        player.loanMoney = selectBorrow.get() + (int)(selectBorrow.get()*0.03f) + 1;
                        player.debtMoney += selectBorrow.get() + (int)(selectBorrow.get()*0.03f) + 1;
                        onSelectedItemOrMoneyChanged();
                        leaderBoardUpdate();
                        selectBorrow.set(maxBorrow/10);
                    }
                    ImGui.sameLine();
                    if(ImGui.button("Annuler")){
                        selectBorrow.set(maxBorrow/10);
                    }
                }
            }else{
                ImGui.text("\n\n" + "Somme à payer : " + player.debtMoney + " pièces d'or \n\n");
                ImGui.text("\n\n" + "Vous devez rembourser votre emprunt !" + "\n\n");
            }

        }else{
            if(player.debtMoney > 0) {
                ImGui.text("\n\n" + "Votre dette : " + player.debtMoney + " pièces d'or \n\n");
                ImGui.inputInt("Somme à emprunter", selectBorrow,maxBorrow/10);
                ImGui.text("Valeur de remboursement sélectionné: " + selectBorrow.get() + "\n\n\n\n");
                if(!(selectBorrow.get() >= 1 && selectBorrow.get() <= player.debtMoney)) {
                    if (selectBorrow.get() < 0)
                        selectBorrow.set(1);
                    else if (selectBorrow.get() > player.debtMoney)
                        selectBorrow.set(player.debtMoney);
                }else{
                    if(ImGui.button("Confirmer")){
                        player.money -= selectBorrow.get();
                        player.debtMoney -= selectBorrow.get();
                        onSelectedItemOrMoneyChanged();
                        leaderBoardUpdate();
                        selectBorrow.set(1);
                    }
                    ImGui.sameLine();
                    if(ImGui.button("Annuler")){
                        selectBorrow.set(1);
                    }
                }
            }else{
                player.loanMoney = 0;
                ImGui.text("\n\n" + "Vous n'avez pas de dette à rembourser" + "\n\n");
            }

        }
    }

    private void ImGuiBuyingResearch(){
        Player player = Farmland.get().getCurrentSave().getCurrentPlayer();

        ImGui.text("Recherches : \n\n");
        for (int i = 0; i < player.researchList.size(); i++){
            Research research = player.researchList.get(i);
            if (ImGui.button("Améliorer " + research.getName() + "[" + research.getPrice() + "]") && player.money > research.getPrice()){
                player.setMoney(player.money - research.getPrice());
                research.levelUp(10,1);
            }
            ImGui.sameLine();
            ImGui.text( research.getName() + " niveau : " + research.getLevel() + " bonus de revente : " + research.getEffect());
        }
    }

    private void ImGuiBuyingCaravan(){
        if(ImGui.button("Envoie")){
            caravanMenu = false;
        }
        ImGui.sameLine();
        if(ImGui.button("Reception")){
            caravanMenu = true;
        }

        Map<String, Item> playerInventory = Objects.requireNonNull(Farmland.get().getCurrentSave()).getCurrentPlayer().getAllItemOfSellInventory();
        Set<String> uniqueItems = playerInventory.keySet();

        Player player = Farmland.get().getCurrentSave().getCurrentPlayer();
        int playerMoney = player.money;

        if (!caravanMenu) {
            if (uniqueItems.isEmpty()) {
                ImGui.text("Pas de Ressources a envoyé !");
            } else {
                ImGui.text("Caravanes a envoyé : \n\n");

                for (String item : uniqueItems) {
                    int currentQuantity = playerInventory.get(item).quantity;
                    if (currentQuantity > 1) {
                        int fEffect = 0;
                        int eEffect = 0;
                        for (int i = 0; i < player.researchList.size(); i++){
                            Research re = player.researchList.get(i);
                            if (re.getName().equals("Fermier")){
                                fEffect = re.getEffect();
                            } else if (re.getName().equals("Eleveur")){
                                eEffect = re.getEffect();
                            }
                        }

                        int researchBonus = (playerInventory.get(item) instanceof Crop)? fEffect : eEffect;

                        int sellValueOfCaravan = (int) (((playerInventory.get(item).sellingValue + researchBonus) * 1.25) * currentQuantity / 2);
                        int travelTime = 4;
                        int travelPrice = 10;
                        if (ImGui.button("Envoyé " + playerInventory.get(item).name + " [" + travelPrice + "]") && playerInventory.get(item) != null) {
                            player.setMoney(playerMoney - travelPrice);
                            player.caravanList.add(new Caravan(sellValueOfCaravan,travelTime,item));
                            for (int i = 0; i < currentQuantity / 2; i++) {
                                player.deleteFromInventory(playerInventory.get(item), "Caravan");
                            }
                        }
                        ImGui.sameLine();
                        if (playerInventory.get(item) == null) {
                            ImGui.text("  Plus de caravanes");
                        } else {
                            ImGui.text(playerInventory.get(item).name + " x" + currentQuantity / 2);
                        }
                        ImGui.sameLine();
                        ImGui.text("Prix de vente : " + sellValueOfCaravan + " / Temps estimé :  " + travelTime);
                    } else {
                        ImGui.text("Pas assez de " + playerInventory.get(item).name + " a envoyé");
                    }
                }
            }
        } else {
            if (player.caravanList.isEmpty()){
                ImGui.text("Vous ne possedez pas de caravanes ");
            } else {
                ImGui.text("Parcour de vos caravanes : \n\n");

                for (int i = 0; i < player.caravanList.size() ; i++){
                    Caravan caravan = player.caravanList.get(i);
                    ImGui.text("Caravane pour " + caravan.getReward() + " pieces / arrivée dans " + (caravan.getTotalTurn() - caravan.getTravelTurn()) + " tours");
                }
            }
        }
    }

    private void ImGuiBuyingItem(){
        if(ImGui.button("Achat")){
            sellMenu = false;
        }
        ImGui.sameLine();
        if(ImGui.button("Vente")){
            sellMenu = true;
        }

        Player player = Farmland.get().getCurrentSave().getCurrentPlayer();
        int playerMoney = player.money;

        if(sellMenu){
            ImGui.text("Objets à vendre : \n\n");

            Map<String, Item> playerInventory = Objects.requireNonNull(Farmland.get().getCurrentSave()).getCurrentPlayer().getAllItemOfSellInventory();
            Set<String> uniqueItems = playerInventory.keySet();
            List<Item> toDelete = new ArrayList<>();

            if(uniqueItems.isEmpty()){
                ImGui.text("Vous n'avez pas de ressource à vendre");
            }

            for (String item: uniqueItems){
                if (ImGui.button(sellnickNameItem(playerInventory.get(item)))) {
                    player.setMoney(playerMoney + Farmland.get().getCurrentSave().getResourceDatabase().get(item).sellingValue);
                    if(player.debtMoney > 0)
                        decreasePlayerDept(player, Farmland.get().getCurrentSave().debtRate);
                    toDelete.add(playerInventory.get(item));
                    Farmland.get().getCurrentSave().fillTurnItemDataBase(Item.clone(playerInventory.get(item)), false);
                    leaderBoardUpdate();
                }
                ImGui.sameLine();
                if(playerInventory.get(item) == null){
                    ImGui.text("  Possédé : x0" );
                }else{
                    ImGui.text(" Possédé : x" + playerInventory.get(item).quantity);
                }
                ImGui.sameLine();
                ImGui.text("Prix de revente : " + Farmland.get().getCurrentSave().getResourceDatabase().get(item).sellingValue);
            }

            player.deleteFromInventory(toDelete, "Sell");

        }else{
            ImGui.text("Objets en magasin : \n\n");


            for(Item item : Farmland.get().getCurrentSave().getResourceDatabase().values()){
                if(ImGui.button(buyNickNameItem(item)) && playerMoney>=item.buyingValue){
                    player.setMoney(playerMoney-item.buyingValue);
                    player.addToInventory(item, "Buy");
                    Farmland.get().getCurrentSave().fillTurnItemDataBase(item, true);
                    leaderBoardUpdate();
                }
                ImGui.sameLine();
                ImGui.text("Prix d'achat : " + item.buyingValue);

            }
        }

    }

    private String buyNickNameItem(Item item){
        if(item instanceof Crop){
            if(item.name.charAt(0) == 'A' || item.name.charAt(0) == 'E' ||
                    item.name.charAt(0) == 'I' || item.name.charAt(0) == 'O' || item.name.charAt(0) == 'Y')
                return "Graine d'" + item.name.toLowerCase();
            return "Graine de " + item.name.toLowerCase();
        }else if(item instanceof Animal){
            return "Bébé " + item.name.toLowerCase();
        }
        return item.name;
    }

    private String sellnickNameItem(Item item){
        if(item instanceof Animal)
            return "Viande de " + item.name.toLowerCase();
        return item.name;
    }

    private void makeListOfPlayerItem(){
        if (Farmland.get().getCurrentSave() != null && Farmland.get().getCurrentSave().getCurrentPlayer().selectedItemID != null && ImGui.button("Désélectionner")) {
            Farmland.get().getCurrentSave().getCurrentPlayer().selectedItemID = null;
            onSelectedItemOrMoneyChanged();
        }

        Map<String, Item> playerInventory = Objects.requireNonNull(Farmland.get().getCurrentSave()).getCurrentPlayer().getAllItemOfBoughtInventory();
        Set<String> uniqueItems = playerInventory.keySet();

        if (uniqueItems.isEmpty()) {
            ImGui.text("Votre inventaire est vide !");
        } else {
            for(String item : uniqueItems){
                ImGui.text(buyNickNameItem(playerInventory.get(item)) + " (x" + playerInventory.get(item).quantity + ")");

                ImGui.sameLine();

                ImGui.pushID(item);

                if (ImGui.button("Sélectionner")) {
                    Farmland.get().getCurrentSave().getCurrentPlayer().selectedItemID = item;
                    onSelectedItemOrMoneyChanged();
                }

                ImGui.popID();
            }
        }
    }

    public void decreasePlayerDept(Player currentPlayer, float percent){
        percent = percent/100;
        int value = Math.max((int)(currentPlayer.loanMoney*percent), 1);
        if(currentPlayer.debtMoney >= value){
            currentPlayer.debtMoney -= value;
            currentPlayer.setMoney(currentPlayer.money - value);
        }else{
            currentPlayer.setMoney(currentPlayer.money - currentPlayer.debtMoney);
            currentPlayer.debtMoney = 0;
        }
        leaderBoardUpdate();
    }

    public void onTurnEnded() {
        if(Farmland.get().getCurrentSave() == null) return;
        Player currentPlayer = Farmland.get().getCurrentSave().getCurrentPlayer();
        
        if(currentPlayer.debtMoney > 0)
            decreasePlayerDept(currentPlayer, Farmland.get().getCurrentSave().debtRate);

        if (!Farmland.get().getCurrentSave().deadPlayers.contains(currentPlayer.getId())) {
            getEntityByName("stateLabel").getComponent(TextComponent.class).setText("Tour " + (Farmland.get().getCurrentSave().turn + 1) + " de " + Farmland.get().getCurrentSave().getCurrentPlayer().name);
            checkCaravan();
        }

        if (Farmland.get().getCurrentSave().getCurrentPlayer().getId().equals(0)) {
            for (int x = 0; x < Farmland.get().getCurrentSave().cells.size(); x++) {
                for (int y = 0; y < Farmland.get().getCurrentSave().cells.get(x).size(); y++) {
                    Cell cell = Farmland.get().getCurrentSave().cells.get(x).get(y);

                    if (cell.isOwnedByCurrentPlayer()){
                        Player player = Farmland.get().getCurrentSave().players.get(cell.ownerId);
                        player.setMoney(player.money - 1);
                    }

                    if (cell.hasItem()) {
                        cell.item.endTurn();

                        if (cell.item.shouldBeDestroyed()) {
                            Player player = Farmland.get().getCurrentSave().players.get(cell.ownerId);
                            player.addToInventory(cell.item, "Sell");
                            cell.item = null;
                        }
                    }
                }
            }
            if (!onCompletedTurnEnd()){
                leaderBoardUpdate();
            }
        }

        if (currentPlayer.getId() != 0) {
            getEntityByName("endTurnButton").setEnabled(false);
            getEntityByName("inventoryButton").setEnabled(false);
            getEntityByName("marketButton").setEnabled(false);
            getEntityByName("caravanButton").setEnabled(false);
            getEntityByName("researchButton").setEnabled(false);
            getEntityByName("bankButton").setEnabled(false);
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
            showBank.set(true);
        } else {
            getEntityByName("endTurnButton").setEnabled(true);
            getEntityByName("inventoryButton").setEnabled(true);
            getEntityByName("marketButton").setEnabled(true);
            getEntityByName("caravanButton").setEnabled(true);
            getEntityByName("researchButton").setEnabled(true);
            getEntityByName("bankButton").setEnabled(true);
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
            checkPlayerFrame();
        }
    }

    public void checkPlayerFrame(){
        Player player = Farmland.get().getCurrentSave().getCurrentPlayer();
        int fLevel = 0;
        int eLevel = 0;
        for (int i = 0; i < player.researchList.size(); i++){
            Research re = player.researchList.get(i);
            if (re.getName().equals("Fermier")){
                fLevel = re.getLevel();
            } else if (re.getName().equals("Eleveur")){
                eLevel = re.getLevel();
            }
        }

        if (eLevel > 4){
            if (fLevel > 4){
                getEntityByName("FrameImage").getComponent(SpriteComponent.class).setSprite(new Sprite(Resources.loadTexture("ui/farmer2breeder2.png")));
            } else if (fLevel > 2){
                getEntityByName("FrameImage").getComponent(SpriteComponent.class).setSprite(new Sprite(Resources.loadTexture("ui/farmerbreeder2.png")));
            } else {
                getEntityByName("FrameImage").getComponent(SpriteComponent.class).setSprite(new Sprite(Resources.loadTexture("ui/breeder2.png")));
            }
        } else if (eLevel > 2){
            if (fLevel > 4){
                getEntityByName("FrameImage").getComponent(SpriteComponent.class).setSprite(new Sprite(Resources.loadTexture("ui/farmer2breeder.png")));
            } else if (fLevel > 2){
                getEntityByName("FrameImage").getComponent(SpriteComponent.class).setSprite(new Sprite(Resources.loadTexture("ui/farmerbreeder.png")));
            } else {
                getEntityByName("FrameImage").getComponent(SpriteComponent.class).setSprite(new Sprite(Resources.loadTexture("ui/breeder.png")));
            }
        } else if (fLevel > 4){
            getEntityByName("FrameImage").getComponent(SpriteComponent.class).setSprite(new Sprite(Resources.loadTexture("ui/farmer2.png")));

        } else if (fLevel > 2){
            getEntityByName("FrameImage").getComponent(SpriteComponent.class).setSprite(new Sprite(Resources.loadTexture("ui/farmer.png")));
        }
    }

    public boolean onCompletedTurnEnd(){
        Player currentPlayer = Farmland.get().getCurrentSave().getCurrentPlayer();

        Farmland.get().getCurrentSave().fillBuyItemDataBasePerTurn();
        economicComponent.changeValueOfRessource();
        Farmland.get().getCurrentSave().clearTurnItemDatabase();

        if (Farmland.get().getCurrentSave().PlayerMeetCondition()) {

            Player human = null;
            for (Player player : Farmland.get().getCurrentSave().players) {
                if (player.typeOfPlayer.contains("Humain") ) {
                    human = player;
                }
            }
            if (getGame().isConnectedToServer()) {
                getGame().disconnectFromServer();
            }
            ResultMenu resultMenu = new ResultMenu();
            resultMenu.currentPlayer = human;
            resultMenu.currentSave = Farmland.get().getCurrentSave();
            if (human != null) {
                resultMenu.isWin = human.money >= 1000;
            }
            Farmland.get().saveId = null;
            changeScene(resultMenu);
            return true;

        } else if (Farmland.get().getCurrentSave().BotMeetCondition()) {

            int numberOfBots = 0;

            for (int i = 0; i < Farmland.get().getCurrentSave().players.size(); i++) {
                Player player = Farmland.get().getCurrentSave().players.get(i);

                if (player.typeOfPlayer.contains("Robot") && !Farmland.get().getCurrentSave().deadPlayers.contains(player.getId())) {
                    numberOfBots += 1;

                    if (player.money >= 1000) {
                        ResultMenu resultMenu = new ResultMenu();
                        resultMenu.currentPlayer = currentPlayer;
                        resultMenu.currentSave = Farmland.get().getCurrentSave();
                        resultMenu.isWin = false;
                        Farmland.get().saveId = null;
                        changeScene(resultMenu);
                        return true;
                    } else if (player.money <= 0) {
                        numberOfBots -= 1;
                        for (int x = 0; x < Farmland.get().getCurrentSave().cells.size(); x++) {
                            for (int y = 0; y < Farmland.get().getCurrentSave().cells.get(x).size(); y++) {
                                Cell cell = Farmland.get().getCurrentSave().cells.get(x).get(y);

                                if (cell.isOwned() && cell.ownerId.equals(player.getId())) {
                                    cell.setItem(null);
                                    cell.setOwned(false, -1);
                                }
                            }
                        }

                        Farmland.get().getCurrentSave().deadPlayers.add(player.getId());
                    }
                }
            }

            if (numberOfBots == 0 && Farmland.get().getCurrentSave().startWithBots) {
                ResultMenu resultMenu = new ResultMenu();
                resultMenu.currentPlayer = currentPlayer;
                resultMenu.currentSave = Farmland.get().getCurrentSave();
                resultMenu.isWin = true;
                Farmland.get().saveId = null;
                changeScene(resultMenu);
                return true;
            }
        }
        return false;
    }

    public boolean shouldShowBackInventory = false;

    public boolean shouldShowBackMarket = false;

    public boolean shouldShowBackCaravan = false;

    public boolean shouldShowBackResearch = false;

    public boolean shouldShowBackBank = false;

    public List<Player> leaderBoardMaker(List<Player> list){
        Player[] tmp = new Player[list.size()];
        for (int i = 0; i < list.size(); i++) {
            tmp[i] = list.get(i);
        }
        for (int i = 0; i < list.size(); i++) {
            for (int j = i; j > 0; j--){
                if (Farmland.get().getCurrentSave().deadPlayers.contains(tmp[j].getId()) || tmp[j-1].money > tmp[j].money){
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


    public void leaderBoardUpdate(){
        List<Player> leaderBoardList = leaderBoardMaker(Farmland.get().getCurrentSave().players);
        String leaderBoard = "    LeaderBoard";
        for (Player player : leaderBoardList) {
            leaderBoard += "\n\n" + player.name + " : " + (Farmland.get().getCurrentSave().deadPlayers.contains(player.getId()) ? "dead" : player.money) + "   ";
        }
        getEntityByName("LeaderBoardLabel").getComponent(TextComponent.class).setText(leaderBoard);
    }

    public void checkCaravan(){
        Player currentPlayer = Farmland.get().getCurrentSave().getCurrentPlayer();
        if (!currentPlayer.caravanList.isEmpty()){
            List<Caravan> toDelete = new ArrayList<>();

            for (int i = 0; i < currentPlayer.caravanList.size() ; i++){
                Caravan caravan = currentPlayer.caravanList.get(i);
                caravan.turnDone();
                if (caravan.hasArrived()){
                    currentPlayer.setMoney(currentPlayer.money + caravan.getReward());
                    toDelete.add(currentPlayer.caravanList.get(i));
                }

            currentPlayer.caravanList.removeAll(toDelete);
            }
        }
    }

    public void onSelectedItemOrMoneyChanged() {
        String selectedId = Farmland.get().getCurrentSave().getCurrentPlayer().selectedItemID;
        String text = "";
        if (Farmland.get().getCurrentSave().getCurrentPlayer().selectedItemID != null) {
            text += "\n\nSélectionné: " + Farmland.get().getItem(selectedId).name + " (x" + Farmland.get().getCurrentSave().getCurrentPlayer().getAllItemOfBoughtInventory().get(selectedId).quantity + ")";

        }
        String text2 = "    " + Farmland.get().getCurrentSave().getCurrentPlayer().money;
        getEntityByName("selectedLabel").getComponent(TextComponent.class).setText(text);
        getEntityByName("MoneyLabel").getComponent(TextComponent.class).setText(text2);
        moneyUpdate();
    }

    public void moneyUpdate(){
        Player player = Farmland.get().getCurrentSave().getCurrentPlayer();
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

    public void onSecondElapsed(int secondsElapsed) {
        getEntityByName("timeRemainingLabel").getComponent(TextComponent.class).setText("Temps restant: " + DateUtil.secondsToText(SaveGame.timePerTurn - secondsElapsed));
        getEntityByName("timePassedLabel").getComponent(TextComponent.class).setText("Temps : " + DateUtil.secondsToText( ++Farmland.get().getCurrentSave().timePassed));
    }
}
