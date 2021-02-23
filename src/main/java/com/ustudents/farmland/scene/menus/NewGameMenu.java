package com.ustudents.farmland.scene.menus;

import com.ustudents.engine.core.event.EventListener;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.graphic.imgui.ImGuiUtils;
import com.ustudents.engine.scene.SceneManager;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.core.SaveGame;
import com.ustudents.farmland.scene.InGameScene;
import imgui.ImColor;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiDataType;
import imgui.type.ImLong;
import imgui.type.ImString;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

public class NewGameMenu extends MenuScene {
    ImString saveName = new ImString();
    ImString playerName = new ImString();
    ImString villageName = new ImString();
    float[] color = {1, 0, 0, 1};
    int[] size = {16, 16};
    ImLong seed = new ImLong(System.currentTimeMillis());
    List<String> errors = new ArrayList<>();

    @Override
    public void initialize() {
        forceImGui = true;

        String[] buttonNames = {};
        String[] buttonIds = {};
        EventListener[] eventListeners = new EventListener[buttonNames.length];

        for (int i = 0; i < buttonNames.length; i++) {
            int j = i;
            eventListeners[i] = (dataType, data) -> {
                switch (buttonIds[j]) {

                }
            };
        }

        initializeMenu(buttonNames, buttonIds, eventListeners, false, false, true);

        super.initialize();
    }

    @Override
    public void renderImGui() {
        ImGuiUtils.setNextWindowWithSizeCentered(450, 300, ImGuiCond.Appearing);
        ImGui.begin("New Game");

        ImGui.text("Enter savegame informations:");
        ImGui.inputText("Savegame name", saveName);
        ImGui.inputText("Player name", playerName);
        ImGui.inputText("Village name", villageName);
        ImGui.colorEdit4("Banner color", color);
        ImGui.inputInt2("Map size", size);
        ImGui.inputScalar("Map seed", ImGuiDataType.S64, seed);

        if (ImGui.button("Go back")) {
            SceneManager.get().goBack();
        }

        ImGui.sameLine();

        if (ImGui.button("Create")) {
            errors.clear();

            if (saveName.isEmpty()) {
                errors.add("Please enter a savegame name!");
            }
            if (playerName.isEmpty()) {
                errors.add("Please enter a player name!");
            }
            if (villageName.isEmpty()) {
                errors.add("Please enter a village name!");
            }
            if (size[0] <= 0 || size[1] <= 0) {
                errors.add("Please enter a valid map size (greater than 0)!");
            }
            if (seed.get() <= 0) {
                errors.add("Please enter a valid seed (greater than 0)!");
            }

            if (errors.isEmpty()) {
                SaveGame saveGame = new SaveGame(saveName.get(), playerName.get(), villageName.get(), new Color(color[0], color[1], color[2], color[3]), new Vector2i(size[0], size[1]), seed.get());

                Farmland.get().getSaveGames().add(saveGame);
                Farmland.get().currentSave = saveGame;

                SceneManager.get().getTypeOfLastScene();
                changeScene(new InGameScene());
            }
        }

        for (String error : errors) {
            ImGui.text(error);
        }

        ImGui.end();
    }
}
