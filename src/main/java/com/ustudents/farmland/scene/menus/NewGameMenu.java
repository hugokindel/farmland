package com.ustudents.farmland.scene.menus;

import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.event.EventListener;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.graphic.imgui.ImGuiUtils;
import com.ustudents.engine.scene.SceneManager;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.core.Save;
import com.ustudents.farmland.scene.InGameScene;
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
    int[] numberOfBots = new int[1];

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

        initializeMenu(buttonNames, buttonIds, eventListeners, true, false, false, true);

        super.initialize();
    }

    @Override
    public void renderImGui() {
        ImGuiUtils.setNextWindowWithSizeCentered(550, 300, ImGuiCond.Appearing);
        ImGui.begin(Resources.getLocalizedText(Resources.getLocalizedText("newGame")));

        ImGui.text(Resources.getLocalizedText("newGameDescription"));
        ImGui.inputText(Resources.getLocalizedText("ngName"), saveName);
        ImGui.inputText(Resources.getLocalizedText("ngPlayerName"), playerName);
        ImGui.inputText(Resources.getLocalizedText("ngVillageName"), villageName);
        ImGui.colorEdit4(Resources.getLocalizedText("ngBannerColor"), color);
        ImGui.inputInt2(Resources.getLocalizedText("ngMapSize"), size);
        ImGui.inputScalar(Resources.getLocalizedText("ngMapSeed"), ImGuiDataType.S64, seed);
        ImGui.sliderInt(Resources.getLocalizedText("ngNumBots"), numberOfBots, 0, 3);

        if (ImGui.button(Resources.getLocalizedText("return"))) {
            SceneManager.get().goBack();
        }

        ImGui.sameLine();

        if (ImGui.button("create")) {
            errors.clear();

            if (saveName.isEmpty()) {
                errors.add(Resources.getLocalizedText("saveName"));
            }
            if (playerName.isEmpty()) {
                errors.add(Resources.getLocalizedText("playerName"));
            }
            if (villageName.isEmpty()) {
                errors.add(Resources.getLocalizedText("villageName"));
            }
            if (size[0] < 16 || size[1] < 16) {
                errors.add(Resources.getLocalizedText("correctSize"));
            }
            if (seed.get() <= 0) {
                errors.add(Resources.getLocalizedText("correctSeed)"));
            }

            if (errors.isEmpty()) {
                Save save = new Save(saveName.get(), playerName.get(), villageName.get(), new Color(color[0], color[1], color[2], color[3]), new Vector2i(size[0], size[1]), seed.get(), numberOfBots[0]);

                Farmland.get().getSaves().put(save.name, save);
                Farmland.get().loadSave(save.name);
                Farmland.get().writeAllSaves();

                SceneManager.get().popTypeOfLastScene();

                changeScene(new InGameScene());
            }
        }

        for (String error : errors) {
            ImGui.text(error);
        }

        ImGui.end();
    }
}
