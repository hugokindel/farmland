package com.ustudents.farmland.scene;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.json.JsonReader;
import com.ustudents.engine.core.json.JsonWriter;
import com.ustudents.engine.graphic.imgui.ImGuiUtils;
import com.ustudents.engine.scene.Scene;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.component.TimerComponent;
import com.ustudents.farmland.core.player.Human;
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
    private final ArrayList<ImString> newUserName = new ArrayList<>();
    private File humanFolder;
    private File[] list;
    private boolean disableRename;

    @Override
    public void initialize() {
        if ((int)TimerComponent.getCurrentTime() != 0){
            disableRename = true;
        }else{
            TimerComponent.setCurrentTime(0);
        }
        humanFolder = new File(Resources.getKindPlayerDirectoryName("human"));
        list = humanFolder.listFiles();
    }

    @Override
    public void update(float dt) {
        if (disableRename){
            TimerComponent.increaseCurrentTime(dt);
        }
        if(TimerComponent.getCurrentTime() >= TimerComponent.getTimeBeforeRename()){
            TimerComponent.setCurrentTime(0);
            disableRename = false;
        }
    }

    @Override
    public void render() {

    }

    private boolean checkIfFileExist(String name){
        for(int i = 0; i < Objects.requireNonNull(list).length ; i++){
            if(list[i].getName().contains(name)){
                return true;
            }
        }
        return false;
    }
    private boolean checkArg(String s){
        if(s.length()<=0)return false;
        return ((s.charAt(0)>=49 && s.charAt(0)<=57)
                || (s.charAt(0)>=65 && s.charAt(0)<=90)
                || (s.charAt(0)>=97 && s.charAt(0)<=122));
    }

    private void establishPlayer(){
        Human currentPlayer;
        boolean hasFile = checkIfFileExist(userName.get());
        if(list.length<=5 && !hasFile){
            currentPlayer = new Human(userName.get(), villageName.get());
            currentPlayer.serializePlayer(currentPlayer);
        }else{
            Map<String, Object> json = JsonReader.readMap(humanFolder + "/" + userName + ".json");
            if(json != null && villageName.get().length() > 0 && !villageName.get().equals(json.get("villageName"))){
                json.put("villageName",villageName.get());
                JsonWriter.writeToFile(humanFolder + "/"+userName.get() +".json",json);
            }
            currentPlayer = (Human) Human.deserializePlayer(Resources.getKindPlayerDirectoryName("human"),userName.get());
        }
        Farmland.setPlayers(currentPlayer);
    }

    private void changeNameOfFile(File rename,File renameFile){
        Path source = Paths.get(rename.getPath());
        Path destination = Paths.get(renameFile + ".json");
        try {
            Files.move(source, destination);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void applyNewUserName(File rename,int index){
        File renameFile = new File(Resources.getKindPlayerDirectoryName("human") + "/" + newUserName.get(index));
        if(rename != null && !renameFile.exists()){
            changeNameOfFile(rename, renameFile);
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
                userName.set(list[i].getName());
                userName.set(userName.get().substring(0,userName.get().length()-5));
                establishPlayer();
                Game.get().getSceneManager().changeScene(WaitingRoom.class);
            }
            ImGui.sameLine();
            if(!disableRename && ImGui.button("Clear " + (i+1))){
                list[i].delete();
                Game.get().getSceneManager().changeScene(DefineUser.class);
            }
            newUserName.add(new ImString());
            ImGui.sameLine();
            String tmp = String.valueOf(i+1);
            ImGui.inputText(tmp.trim(), newUserName.get(i));

        }
        ImGui.text("\n");
        if(!disableRename){
            if(list.length>0 && ImGui.button("Rename")){
                File rename;
                int index;
                for(int i = 0;i< list.length;i++){
                    if(!checkIfFileExist(newUserName.get(i).get()) && checkArg(newUserName.get(i).get())){
                        rename = new File(list[i].getPath());
                        index=i;
                        disableRename = true;
                        applyNewUserName(rename,index);
                        TimerComponent.setCurrentTime(1);
                        break;
                    }
                }
                Game.get().getSceneManager().changeScene(DefineUser.class);
            }
            ImGui.sameLine();
        }
    }

    public void clearAllButton(){
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
        if(ImGui.button("Create player") && checkArg(userName.get()) && checkArg(villageName.get())){
            establishPlayer();
            Game.get().getSceneManager().changeScene(WaitingRoom.class);
        }
        ImGui.end();
    }

    @Override
    public void destroy() {

    }
}
