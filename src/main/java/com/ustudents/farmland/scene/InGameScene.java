package com.ustudents.farmland.scene;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.core.json.JsonReader;
import com.ustudents.engine.ecs.Entity;
import com.ustudents.engine.ecs.component.core.TransformComponent;
import com.ustudents.engine.ecs.component.graphics.WorldRendererComponent;
import com.ustudents.engine.graphic.*;
import com.ustudents.engine.graphic.imgui.ImGuiUtils;
import com.ustudents.engine.gui.GuiBuilder;
import com.ustudents.engine.scene.Scene;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.component.GridComponent;
import com.ustudents.farmland.component.PlayerMovementComponent;
import com.ustudents.farmland.component.TimerComponent;
import com.ustudents.farmland.core.player.Player;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class InGameScene extends Scene {
    private static boolean[] isTurnOf;
    private static Player currentPlayerTurn;

    private void giveMoney(int begin){
        ArrayList<Player> tmp = Farmland.getPlayers();
        for(Player p : tmp){
            p.setCurrentMoney(begin);
        }
    }

    private boolean checkIfPlayerHasMoney(){
        ArrayList<Player> tmp = Farmland.getPlayers();
        for(Player p : tmp){
            if(p.getCurrentMoney()>0){
                return true;
            }
        }
        return false;
    }

    private void initializeIsTurn(){
        ArrayList<Player> tmp = Farmland.getPlayers();
        int i = 0;
        for(Player p : tmp){
            if(currentPlayerTurn == p){
                isTurnOf[i] = true;
            }
            i++;
        }
    }



    @Override
    public void initialize() {
        if(!Farmland.isInGame()){
            TimerComponent.setCurrentTime(0);
            Farmland.setInGame(true);
        }
        isTurnOf = new boolean[Farmland.numberOfPlayer()];
        if(currentPlayerTurn == null){
            if(Farmland.getKindOfGame().equals("SinglePlayer")){
                int randomNum = (int) (Math.random() * isTurnOf.length);
                currentPlayerTurn = Farmland.getPlayers().get(randomNum);
                currentPlayerTurn.setCurrentActionPlayed(0);
            }
        }
        initializeIsTurn();

        if(!checkIfPlayerHasMoney()){
            giveMoney(500);
        }

        initializeGameplay();
        initializeGui();
    }

    public void initializeGameplay() {
        if (hasEntityWithName("grid")) {
            killEntity(getEntityByName("grid"), true);
        }

        NineSlicedSprite gridBackground = new NineSlicedSprite(Resources.loadSpritesheet("ui/map_background.json"));
        Texture cellBackground = Resources.loadTexture("map/grass.png");
        AnimatedSprite selectionCursor = new AnimatedSprite(Resources.loadSpritesheet("ui/map_cell_cursor.json"));
        Spritesheet territoryTexture = Resources.loadSpritesheet("ui/map_territory_indicator.json");

        Entity grid = createEntityWithName("grid");
        grid.addComponent(new TransformComponent());
        grid.addComponent(new WorldRendererComponent());
        grid.addComponent(new GridComponent(new Vector2i(32, 32), new Vector2i(24, 24), gridBackground, cellBackground, selectionCursor, territoryTexture));

        Entity player = createEntityWithName("player");
        player.addComponent(new PlayerMovementComponent(500.0f));
    }

    public void initializeGui() {
        GuiBuilder guiBuilder = new GuiBuilder();

        GuiBuilder.ButtonData buttonData = new GuiBuilder.ButtonData("Finir le tour", (dataType, data) -> {
            Out.println("Fin du tour");
        });
        buttonData.origin = new Origin(Origin.Vertical.Bottom, Origin.Horizontal.Right);
        buttonData.anchor = new Anchor(Anchor.Vertical.Bottom, Anchor.Horizontal.Right);
        buttonData.position = new Vector2f(-10, -10);
        guiBuilder.addButton(buttonData);

        GuiBuilder.WindowData windowData = new GuiBuilder.WindowData();
        windowData.origin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Center);
        windowData.anchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Center);
        windowData.position.y += 5;
        guiBuilder.beginWindow(windowData);

        // Happens within the window.
        {
            GuiBuilder.TextData textData = new GuiBuilder.TextData("Tour 1 de " + currentPlayerTurn.getUserName());
            textData.origin = new Origin(Origin.Vertical.Middle, Origin.Horizontal.Center);
            textData.anchor = new Anchor(Anchor.Vertical.Middle, Anchor.Horizontal.Center);
            textData.color = Color.BLACK;
            guiBuilder.addText(textData);
        }

        guiBuilder.endWindow();
    }

    private void ejectBot(){
        if(isTurnOf[0]){
            giveMoney(-1);
            Game.get().getSceneManager().changeScene(SingleplayerMenu.class);
        }else{
            int index = 0;
            for(int i = 1; i < isTurnOf.length; i++){
                if(isTurnOf[i]){
                    Farmland.throwBot(i);
                    isTurnOf = new boolean[3];
                    index = i;
                    break;
                }
            }
            if(index+1>=isTurnOf.length){
                isTurnOf[index%3] = true;
            }else{
                isTurnOf[index+1] = true;
            }
        }
    }

    @Override
    public void update(float dt) {
        TimerComponent.increaseCurrentTime(dt);
        if(TimerComponent.getCurrentTime() >= TimerComponent.getTimerPerPlayer()){
            TimerComponent.setCurrentTime(0);
            for(int i = 0; i < isTurnOf.length; i++){
                if(isTurnOf[i]){
                    isTurnOf[i] = false;
                    isTurnOf[(i+1)%isTurnOf.length] = true;
                    currentPlayerTurn = Farmland.getPlayers().get((i+1)%isTurnOf.length);
                    break;
                }
            }
        }
        if(Farmland.getGoal().checkIfPlayerLoose()){
            if(Farmland.getKindOfGame().equals("MultiPlayer")){

            }else{
                ejectBot();
            }
        }
        if (Farmland.getGoal().checkIfPlayerWin()){

        }
    }

    @Override
    public void render() {

    }

    public static void timerAction(){
        float printTime = (TimerComponent.getTimerPerPlayer()-TimerComponent.getCurrentTime());
        ImGui.text(makeSpace(13) + "Timer : " + (int)printTime/60 + "min" + (int)printTime%60 + "s");
    }

    private static boolean getYourPlayer(){
        File[] list = new File(Resources.getKindPlayerDirectoryName("human")).listFiles();
        if (list == null) return false;
        for(File f : list){
            Map<String,Object> json = JsonReader.readMap(f.getPath());
            if(json != null && json.get("userName").equals(currentPlayerTurn.getUserName())){
                return true;
            }
        }
        return false;
    }

    private static String makeSpace(int n){
        return " ".repeat(Math.max(0, n));
    }

    public static void printThePlayerTurn(){
        ImGui.text("\n");
        if (getYourPlayer()){
            ImGui.text("Is Playing: You");
        }else{
            ImGui.text("Is Playing: " + currentPlayerTurn.getUserName());
        }
        ImGui.text("Village Name : " + currentPlayerTurn.getVillageName());
        ImGui.text("\n");
        ImGui.text("Number of Players : " + isTurnOf.length);
        ImGui.text("\n");
        ImGui.text("Available money : " + currentPlayerTurn.getCurrentMoney());
        ImGui.text("\n");
    }

    public void printForPlayerTurn(){
        ImGui.text("Player options : \n");
        if (ImGui.button("Inventory")){
            if (currentPlayerTurn.getCurrentActionPlayed() == -1){
                currentPlayerTurn.setCurrentActionPlayed(0);
            }
            currentPlayerTurn.increaseCurrentActionPlayed();
            Game.get().getSceneManager().changeScene(PlayerInventory.class);
        }
        ImGui.sameLine();
        if (ImGui.button("Market")){
            if (currentPlayerTurn.getCurrentActionPlayed() == -1){
                currentPlayerTurn.setCurrentActionPlayed(0);
            }
            currentPlayerTurn.increaseCurrentActionPlayed();
            Game.get().getSceneManager().changeScene(MarketMenu.class);
        }
        ImGui.text("\n");
        if(isTurnOf[0]){
            if (ImGui.button("Finish your turn")){
                if (currentPlayerTurn.getCurrentActionPlayed() > 0 || currentPlayerTurn.getCurrentActionPlayed() == -1){
                    TimerComponent.setCurrentTime(TimerComponent.getTimerPerPlayer());
                }else{
                    currentPlayerTurn.setCurrentActionPlayed(-1);
                }
            }
            ImGui.sameLine();
        }

    }

    @Override
    public void renderImGui(){
        ImGuiUtils.setNextWindowWithSizeCentered(300, 300, ImGuiCond.Appearing);
        ImGui.begin("Game Menu");
        timerAction();
        printThePlayerTurn();
        ImGui.separator();
        printForPlayerTurn();
        if (ImGui.button("Leave the game")){
            if (Farmland.getKindOfGame().equals("MultiPlayer")){

            }
            TimerComponent.setCurrentTime(0);
            Farmland.setInGame(false);
            currentPlayerTurn = null;
            Game.get().getSceneManager().changeScene(MainMenu.class);
        }
        if(currentPlayerTurn != null && currentPlayerTurn.getCurrentActionPlayed() == -1){
            ImGui.text("Are you sure to end your turn ?");
        }
        ImGui.end();
    }

    @Override
    public void destroy() {

    }

    public static Player getCurrentPlayerTurn(){
        return currentPlayerTurn;
    }
}
