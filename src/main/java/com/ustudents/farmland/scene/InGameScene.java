package com.ustudents.farmland.scene;

import com.ustudents.engine.core.Resources;
import com.ustudents.engine.graphic.imgui.ImGuiUtils;
import com.ustudents.engine.scene.component.graphics.SpriteComponent;
import com.ustudents.engine.scene.component.graphics.TextureComponent;
import com.ustudents.engine.scene.ecs.Component;
import com.ustudents.engine.scene.ecs.Entity;
import com.ustudents.engine.scene.component.core.TransformComponent;
import com.ustudents.engine.scene.component.graphics.WorldRendererComponent;
import com.ustudents.engine.scene.component.gui.TextComponent;
import com.ustudents.engine.graphic.*;
import com.ustudents.engine.gui.GuiBuilder;
import com.ustudents.engine.scene.Scene;
import com.ustudents.engine.utility.DateUtil;
import com.ustudents.engine.utility.Pair;
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
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.util.*;

public class InGameScene extends Scene {
    public ImBoolean showInventory;

    public ImBoolean showMarket;

    public ImBoolean showCaravan;

    public ImBoolean showResearch;

    public EconomicComponent economicComponent;

    public boolean sellMenu;

    public boolean caravanMenu;

    @Override
    public void initialize() {
        forceImGui = true;

        showInventory = new ImBoolean(false);
        showMarket = new ImBoolean(false);
        showCaravan = new ImBoolean(false);
        showResearch = new ImBoolean(false);

        initializeEntities();
        initializeGui();
        initializeGameplay();
        economicComponent = new EconomicComponent();

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

        Texture frameTexture = Resources.loadTexture("ui/frame.png");
        GuiBuilder.ImageData imageDataFrame = new GuiBuilder.ImageData(frameTexture);
        imageDataFrame.id = "FrameImage";
        imageDataFrame.origin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Left);
        imageDataFrame.anchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Left);
        imageDataFrame.scale = new Vector2f(3f, 3f);
        imageDataFrame.position.y = 30;
        imageDataFrame.position.x = 3;
        imageDataFrame.zIndex = 2;
        guiBuilder.addImage(imageDataFrame);

        initializeAvatar(guiBuilder);

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
        buttonData1.position = new Vector2f(-220, -12);
        guiBuilder.addButton(buttonData1);

        GuiBuilder.ButtonData buttonData3 = new GuiBuilder.ButtonData("Marché", (dataType, data) -> {
            showMarket.set(!showMarket.get());
        });
        buttonData3.id = "marketButton";
        buttonData3.origin = new Origin(Origin.Vertical.Bottom, Origin.Horizontal.Right);
        buttonData3.anchor = new Anchor(Anchor.Vertical.Bottom, Anchor.Horizontal.Right);
        buttonData3.position = new Vector2f(-400, -10);
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

        GuiBuilder.TextData textData = new GuiBuilder.TextData("Temps restant: " + DateUtil.secondsToText(SaveGame.timePerTurn - Farmland.get().getCurrentSave().turnTimePassed));
        textData.id = "timeRemainingLabel";
        textData.origin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Center);
        textData.anchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Center);
        textData.position = new Vector2f(0, 75);
        textData.color = Color.BLACK;
        guiBuilder.addText(textData);

        String selectedId = Farmland.get().getCurrentSave().getCurrentPlayer().selectedItemID;
        String text = "Argent: " + Farmland.get().getCurrentSave().getCurrentPlayer().money;
        if (Farmland.get().getCurrentSave().getCurrentPlayer().selectedItemID != null) {
            text += "\n\nSélectionné: " + Farmland.get().getItem(selectedId).name + " (x" + Farmland.get().getCurrentSave().getCurrentPlayer().buyInventory.get(selectedId).quantity + ")";
        }
        GuiBuilder.TextData textData2 = new GuiBuilder.TextData(text);
        textData2.id = "selectedLabel";
        textData2.origin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Left);
        textData2.anchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Left);
        textData2.position = new Vector2f(10, 10);
        textData2.color = Color.BLACK;

        guiBuilder.addText(textData2);

        if (Farmland.get().getCurrentSave().deadPlayers == null) {
            Farmland.get().getCurrentSave().deadPlayers = new ArrayList<>();
        }

        List<Player> leaderBoardList = leaderBoardMaker(Farmland.get().getCurrentSave().players);
        String leaderBoard = "LeaderBoard : ";
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
        base.position.y = 56;
        base.position.x = 30;
        base.region = avatarSprites.getSprite("base").getRegion();
        base.zIndex = 0;
        guiBuilder.addImage(base);

        GuiBuilder.ImageData braces = new GuiBuilder.ImageData(avatarSprites.getSprite("layerBraces").getTexture());
        braces.id = "localPlayerAvatarBraces";
        braces.origin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Left);
        braces.anchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Left);
        braces.scale = new Vector2f(3f, 3f);
        braces.position.y = 56;
        braces.position.x = 30;
        braces.region = avatarSprites.getSprite("layerBraces").getRegion();
        braces.zIndex = 1;
        braces.tint = Farmland.get().getCurrentSave().players.get(0).avatar.bracesColor;
        guiBuilder.addImage(braces);

        GuiBuilder.ImageData shirt = new GuiBuilder.ImageData(avatarSprites.getSprite("layerShirt").getTexture());
        shirt.id = "localPlayerAvatarShirt";
        shirt.origin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Left);
        shirt.anchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Left);
        shirt.scale = new Vector2f(3f, 3f);
        shirt.position.y = 56;
        shirt.position.x = 30;
        shirt.region = avatarSprites.getSprite("layerShirt").getRegion();
        shirt.zIndex = 1;
        shirt.tint = Farmland.get().getCurrentSave().players.get(0).avatar.shirtColor;
        guiBuilder.addImage(shirt);

        GuiBuilder.ImageData hat = new GuiBuilder.ImageData(avatarSprites.getSprite("layerHat").getTexture());
        hat.id = "localPlayerAvatarHat";
        hat.origin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Left);
        hat.anchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Left);
        hat.scale = new Vector2f(3f, 3f);
        hat.position.y = 56;
        hat.position.x = 30;
        hat.region = avatarSprites.getSprite("layerHat").getRegion();
        hat.zIndex = 1;
        hat.tint = Farmland.get().getCurrentSave().players.get(0).avatar.hatColor;
        guiBuilder.addImage(hat);

        GuiBuilder.ImageData buttons = new GuiBuilder.ImageData(avatarSprites.getSprite("layerButtons").getTexture());
        buttons.id = "localPlayerAvatarButtons";
        buttons.origin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Left);
        buttons.anchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Left);
        buttons.scale = new Vector2f(3f, 3f);
        buttons.position.y = 56;
        buttons.position.x = 30;
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

        Map<String, Item> playerInventory = Objects.requireNonNull(Farmland.get().getCurrentSave()).getCurrentPlayer().sellInventory;
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

                        int sellValueOfCaravan = (int) (((playerInventory.get(item).value + researchBonus) * 1.25) * currentQuantity / 2);
                        int travelTime = 4;
                        int travelPrice = 10;
                        if (ImGui.button("Envoyé " + playerInventory.get(item).name + " [" + travelPrice + "]") && playerInventory.get(item) != null) {
                            player.setMoney(playerMoney - travelPrice);
                            player.caravanList.add(new Caravan(sellValueOfCaravan,travelTime,playerInventory.get(item).name));
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
                    ImGui.text("Caravane de " + caravan.getProduct() + " pour " + caravan.getReward() + " pieces / arrivée dans " + (caravan.getTotalTurn() - caravan.getTravelTurn()) + " tours");
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

            Map<String, Item> playerInventory = Objects.requireNonNull(Farmland.get().getCurrentSave()).getCurrentPlayer().sellInventory;
            Set<String> uniqueItems = playerInventory.keySet();
            /*Map<String, Item> playerInventory = sortByGrow();
            Set<String> uniqueItems = playerInventory.keySet();*/
            List<Item> toDelete = new ArrayList<>();

            if(uniqueItems.isEmpty()){
                ImGui.text("Vous n'avez pas de ressource à vendre");
            }

            for (String item: uniqueItems){
                int sellValueOfItem = (int) (playerInventory.get(item).value/1.5);
                if (ImGui.button(playerInventory.get(item).name) && playerMoney >= playerInventory.get(item).value) {
                    player.setMoney(playerMoney + sellValueOfItem);
                    toDelete.add(playerInventory.get(item));
                }
                ImGui.sameLine();
                if(playerInventory.get(item) == null){
                    ImGui.text("  Possédé : x0" );
                }else{
                    ImGui.text(" Possédé : x" + playerInventory.get(item).quantity);
                }
                ImGui.sameLine();
                ImGui.text("Prix de revente : " + sellValueOfItem);
            }

            player.deleteFromInventory(toDelete, "Sell");

        }else{
            ImGui.text("Objets en magasin : \n\n");


            for(Item item : Farmland.get().getResourceDatabase().values()){
                if(ImGui.button(nickNameItem(item)) && playerMoney>=item.value){
                    player.setMoney(playerMoney-item.value);
                    player.addToInventory(item, "Buy");
                    Farmland.get().getCurrentSave().itemsTurn.add(item);
                }
                ImGui.sameLine();
                ImGui.text("Prix d'achat : " + item.value);

            }
        }

    }

    private void makeListOfPlayerItem(){
        if (Farmland.get().getCurrentSave() != null && Farmland.get().getCurrentSave().getCurrentPlayer().selectedItemID != null && ImGui.button("Désélectionner")) {
            Farmland.get().getCurrentSave().getCurrentPlayer().selectedItemID = null;
            onSelectedItemOrMoneyChanged();
        }

        Map<String, Item> playerInventory = Objects.requireNonNull(Farmland.get().getCurrentSave()).getCurrentPlayer().buyInventory;
        Set<String> uniqueItems = playerInventory.keySet();

        if (uniqueItems.isEmpty()) {
            ImGui.text("Votre inventaire est vide !");
        } else {
            for(String item : uniqueItems){
                ImGui.text(nickNameItem(playerInventory.get(item)) + " (x" + playerInventory.get(item).quantity + ")");

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

    private String nickNameItem(Item item){
        if(item instanceof Crop){
            return "Graine de " + item.name.toLowerCase();
        }
        return item.name;
    }

    public void onTurnEnded() {
        Player currentPlayer = Farmland.get().getCurrentSave().getCurrentPlayer();

        if(Farmland.get().getCurrentSave().turn%2 == 0){
            economicComponent.changeValueOfRessource();
            economicComponent.lastItemTurn = new ArrayList<>();
            economicComponent.lastItemTurn.addAll(Farmland.get().getCurrentSave().itemsTurn);
            Farmland.get().getCurrentSave().itemsTurn= new ArrayList<>();
        }
        if (!Farmland.get().getCurrentSave().deadPlayers.contains(currentPlayer.getId())) {
            getEntityByName("stateLabel").getComponent(TextComponent.class).setText("Tour " + (Farmland.get().getCurrentSave().turn + 1) + " de " + Farmland.get().getCurrentSave().getCurrentPlayer().name);
            checkCaravan();
        }

        if (Farmland.get().getCurrentSave().getCurrentPlayer().getId().equals(0)) {
            //checkCaravan();
            for (int x = 0; x < Farmland.get().getCurrentSave().cells.size(); x++) {
                for (int y = 0; y < Farmland.get().getCurrentSave().cells.get(x).size(); y++) {
                    Cell cell = Farmland.get().getCurrentSave().cells.get(x).get(y);

                    if (cell.isOwnedByCurrentPlayer()){
                        Player player = Farmland.get().getCurrentSave().players.get(cell.ownerId);
                        player.setMoney(player.money - 1);
                    }

                    if (cell.hasItem()/* && Farmland.get().getCurrentSave().getCurrentPlayer().getId().equals(cell.ownerId)*/) {
                        cell.item.endTurn();

                        if (cell.item.shouldBeDestroyed()) {
                            Player player = Farmland.get().getCurrentSave().players.get(cell.ownerId);
                            //player.setMoney(player.money + (int)((cell.item.value) * 1.5f));
                            boolean check = player.sellInventory.containsKey(cell.item.id);
                            player.addToInventory(cell.item, "Sell");
                            if(!check){
                                player.deleteFromInventory(cell.item, "Sell");
                            }
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
            showInventory.set(false);
            showMarket.set(false);
            showCaravan.set(false);
            showResearch.set(false);
        } else {
            getEntityByName("endTurnButton").setEnabled(true);
            getEntityByName("inventoryButton").setEnabled(true);
            getEntityByName("marketButton").setEnabled(true);
            getEntityByName("caravanButton").setEnabled(true);
            getEntityByName("researchButton").setEnabled(true);
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
        String leaderBoard = "LeaderBoard : ";
        for (Player player : leaderBoardList) {
            leaderBoard += "\n\n" + player.name + " : " + (Farmland.get().getCurrentSave().deadPlayers.contains(player.getId()) ? "dead" : player.money);
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
            }

            currentPlayer.caravanList.removeAll(toDelete);
        }
    }

    public void onSelectedItemOrMoneyChanged() {
        String selectedId = Farmland.get().getCurrentSave().getCurrentPlayer().selectedItemID;
        String text = "Argent: " + Farmland.get().getCurrentSave().getCurrentPlayer().money;
        if (Farmland.get().getCurrentSave().getCurrentPlayer().selectedItemID != null) {
            text += "\n\nSélectionné: " + Farmland.get().getItem(selectedId).name + " (x" + Farmland.get().getCurrentSave().getCurrentPlayer().buyInventory.get(selectedId).quantity + ")";
        }
        getEntityByName("selectedLabel").getComponent(TextComponent.class).setText(text);
    }

    public void onSecondElapsed(int secondsElapsed) {
        getEntityByName("timeRemainingLabel").getComponent(TextComponent.class).setText("Temps restant: " + DateUtil.secondsToText(SaveGame.timePerTurn - secondsElapsed));
    }
}
