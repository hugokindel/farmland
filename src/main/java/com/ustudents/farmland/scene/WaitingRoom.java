package com.ustudents.farmland.scene;

import com.ustudents.engine.Game;
import com.ustudents.engine.scene.Scene;
import com.ustudents.engine.graphic.imgui.ImGuiUtils;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.player.Robot;
import imgui.ImGui;
import imgui.flag.ImGuiCond;

public class WaitingRoom extends Scene {
    @Override
    public void initialize() {
        if (Farmland.getPlayers().size()>1){
            Farmland.setPlayersIsInit(false);
            Farmland.throwPlayer();
        }
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void render(){

    }

    private void buttonForMultiPlayer(){
        if (ImGui.button("Multiplayer Mode")) {
            Game.get().getSceneManager().changeScene(MultiplayerMenu.class);
        }
    }

    private void fillTheGameWithBot(int number){
        for(int i = 1; i <= number; i++){
            Farmland.setPlayers(new Robot("Bot " + i,"Village " + i));
        }
    }

    private void buttonForSinglePlayer(){
        if (ImGui.button("Singleplayer Mode")) {
            Game.get().getSceneManager().changeScene(SinglePlayerMenu.class);
        }
        ImGui.separator();
        ImGui.text("Choose your number of opponent :");
        for(int i = 1;i<4;i++){
            if(ImGui.button(i + " Robot(s)")){
                if(Farmland.isPlayersIsInit()){
                    Farmland.throwPlayer();
                }
                fillTheGameWithBot(i);
                Farmland.setPlayersIsInit(true);
            }
            if(i<3){
                ImGui.sameLine();
            }
        }
        ImGui.text("\n");
    }

    @Override
    public void renderImGui() {
        ImGuiUtils.setNextWindowWithSizeCentered(300, 300, ImGuiCond.Appearing);
        ImGui.begin("Waiting Room");
        if(Farmland.getKindOfGame().equals("MultiPlayer")){
            buttonForMultiPlayer();
        }else{
            buttonForSinglePlayer();
        }
        if (Farmland.numberOfPlayer()>1 && ImGui.button("Start the game")){
            Game.get().getSceneManager().changeScene(InGameScene.class);
        }

        ImGui.end();
    }

    @Override
    public void destroy() {

    }
}
