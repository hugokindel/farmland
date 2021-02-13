package com.ustudents.farmland.scene;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.json.JsonReader;
import com.ustudents.engine.core.json.JsonWriter;
import com.ustudents.engine.graphic.imgui.ImGuiUtils;
import com.ustudents.engine.scene.Scene;
import com.ustudents.farmland.player.Human;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.type.ImString;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class DefineUser extends Scene {
    private final ImString userName = new ImString();
    private final ImString villageName = new ImString();
    private final ArrayList<ImString> newUserName = new ArrayList<ImString>();
    private File humanFolder;
    private File[] list;



    @Override
    public void initialize() {
        humanFolder = new File(Resources.getKindPlayerDirectoryName("human"));
        list = humanFolder.listFiles();
    }

    @Override
    public void update(double dt) {

    }

    @Override
    public void render() {

    }

    private boolean checkIfFileExist(){
        for(int i = 0; i < Objects.requireNonNull(list).length ; i++){
            if(list[i].getName().equals(userName.get())){
                return true;
            }
        }
        return false;
    }

    private void establishPlayer(){
        Human currentPlayer;
        boolean hasFile = checkIfFileExist();
        if(list.length<=5 && !hasFile){
            currentPlayer = new Human(userName.get(), villageName.get());
            currentPlayer.serializePlayer(currentPlayer);
        }else{
            Map<String, Object> json = JsonReader.readMap(getClass().getClassLoader().getResourceAsStream("players/human/" + userName.get() + ".json"));
            if(villageName != json.get("villageName")){
                json.put("villageName",villageName);
                JsonWriter.writeToFile(humanFolder + "/"+userName.get() +".json",json);
            }
            currentPlayer = (Human) Human.deserializePlayer(Resources.getKindPlayerDirectoryName("human"),userName.get());
        }
        Game.setPlayers(currentPlayer);
    }

    private void applyNewUserName(File rename,int index){
        File renameFile = new File(System.getProperty("user.dir") + "/" + humanFolder + "/" + newUserName.get(index) + ".json");
        if(rename != null && !renameFile.exists()){
            Path source = Paths.get(rename.getPath());
            Path destination = Paths.get(renameFile + ".json");
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    try {
                        Files.move(source, destination);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            run.run();
            Map<String, Object> json = null;
            if(rename.exists()){
                json = JsonReader.readMap(rename.getPath());
            }
            boolean other = false;
            if(json == null){
                json = JsonReader.readMap(humanFolder + "/" + newUserName.get(index) + ".json");
                other = true;
            }
            json.put("userName",newUserName.get(index).get());
            if(!other){
                JsonWriter.writeToFile(rename.getPath(),json);
            }else{
                JsonWriter.writeToFile(humanFolder + "/"+newUserName.get(index) + ".json",json);
            }

        }
    }

    private void renameButton(){
        for(int i=0;i<list.length;i++){
            if(ImGui.button(list[i].getName())){
                Game.get().getSceneManager().changeScene(WaitingRoom.class);
            }
            ImGui.sameLine();
            if(ImGui.button("clear " + (i+1))){
                list[i].delete();
                Game.get().getSceneManager().changeScene(DefineUser.class);
            }
            newUserName.add(new ImString());
            ImGui.sameLine();
            ImGui.inputText(i + "", newUserName.get(i));

        }
        ImGui.text("\n");
        if(ImGui.button("rename")){
            File rename = null;
            int index = 0;
            for(int i = 0;i< list.length;i++){
                if(newUserName.get(i).get().length() > 0){
                    rename = new File(System.getProperty("user.dir") + "/" + list[i]);
                    index=i;
                    break;
                }

            }
            applyNewUserName(rename,index);
            Game.get().getSceneManager().changeScene(WaitingRoom.class);
        }

    }

    public void clearAllButton(){
        ImGui.sameLine();
        if(list.length> 0 && ImGui.button("Clear All")){
            for (File file : list) {
                file.delete();
            }
            Game.get().getSceneManager().changeScene(DefineUser.class);
        }
    }

    @Override
    public void renderImGui(){
        ImGuiUtils.setNextWindowWithSizeCentered(300, 300, ImGuiCond.Appearing);
        ImGui.begin("Define User Scene");
        if (ImGui.button("Main Menu")) {
            Game.get().getSceneManager().changeScene(MainMenu.class);
        }

        ImGui.separator();

        if(list.length>0){
            ImGui.text("Choose a player:");
            ImGui.text("\n");
        }

        renameButton();
        clearAllButton();

        if (list.length>0){
            ImGui.separator();
            ImGui.text("\n");
        }
        ImGui.text("Create a new player:");
        ImGui.inputText("UserName", userName);
        ImGui.inputText("Village name", villageName);
        ImGui.text("\n");
        if(ImGui.button("Create player")){
            establishPlayer();
            if (Game.getKindOfGame().equals("MultiPlayer")){
                Game.get().getSceneManager().changeScene(WaitingRoom.class);
            }else{
                Game.get().getSceneManager().changeScene(WaitingRoom.class);
            }
        }
        ImGui.end();
    }

    @Override
    public void destroy() {

    }
}
