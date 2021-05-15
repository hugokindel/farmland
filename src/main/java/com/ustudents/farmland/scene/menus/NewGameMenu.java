package com.ustudents.farmland.scene.menus;

import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.core.event.EventListener;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.graphic.imgui.ImGuiUtils;
import com.ustudents.engine.scene.SceneManager;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.core.Save;
import com.ustudents.farmland.core.player.Avatar;
import com.ustudents.farmland.core.player.Bot;
import com.ustudents.farmland.core.player.Player;
import com.ustudents.farmland.scene.InGameScene;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiDataType;
import imgui.type.ImInt;
import imgui.type.ImLong;
import imgui.type.ImString;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

public class NewGameMenu extends MenuScene {
    ImString saveName = new ImString();
    ImString playerName = new ImString();
    ImString villageName = new ImString();
    float[] bannerColor = Player.DEFAULT_BANNER_COLOR.toArray();
    float[] bracesColor = Avatar.DEFAULT_BRACES_COLOR.toArray();
    float[] shirtColor = Avatar.DEFAULT_SHIRT_COLOR.toArray();
    float[] hatColor = Avatar.DEFAULT_HAT_COLOR.toArray();
    float[] buttonsColor = Avatar.DEFAULT_BUTTONS_COLOR.toArray();
    int[] size = {16, 16};
    ImLong seed = new ImLong(System.currentTimeMillis());
    List<String> errors = new ArrayList<>();
    int[] numberOfBots = new int[1];
    String[] difficulties = {
            Resources.getLocalizedText("diffEasy"),
            Resources.getLocalizedText("diffNormal"),
            Resources.getLocalizedText("diffHard"),
            Resources.getLocalizedText("diffImpossible")
    };
    ImInt currentDifficulty = new ImInt(1);
    ImInt percentDebt = new ImInt(10);
    ImInt maxBorrow = new ImInt(100);

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
        ImGuiUtils.setNextWindowWithSizeCentered(850, 500, ImGuiCond.Appearing);
        ImGui.begin(Resources.getLocalizedText(Resources.getLocalizedText("newGame")));

        ImGui.text(Resources.getLocalizedText("newGameDescription"));
        ImGui.inputText(Resources.getLocalizedText("ngName"), saveName);
        ImGui.inputText(Resources.getLocalizedText("ngPlayerName"), playerName);
        ImGui.inputText(Resources.getLocalizedText("ngVillageName"), villageName);
        ImGui.colorEdit4(Resources.getLocalizedText("ngBannerColor"), bannerColor);
        ImGui.colorEdit4(Resources.getLocalizedText("ngSuitColor"), bracesColor);
        ImGui.colorEdit4(Resources.getLocalizedText("ngShirtColor"), shirtColor);
        ImGui.colorEdit4(Resources.getLocalizedText("ngHatColor"), hatColor);
        ImGui.colorEdit4(Resources.getLocalizedText("ngButtonsColor"), buttonsColor);
        ImGui.inputInt2(Resources.getLocalizedText("ngMapSize"), size);
        ImGui.inputScalar(Resources.getLocalizedText("ngMapSeed"), ImGuiDataType.S64, seed);
        ImGui.sliderInt(Resources.getLocalizedText("ngNumBots"), numberOfBots, 0, 3);
        ImGui.combo(Resources.getLocalizedText("ngDiff"), currentDifficulty, difficulties, difficulties.length);
        ImGui.inputInt(Resources.getLocalizedText("ngMaxLoan"), maxBorrow,100);
        ImGui.inputInt(Resources.getLocalizedText("ngLoanPer"), percentDebt,10);

        // TODO: REMOVE ?
        if (maxBorrow.get() < 100 || maxBorrow.get()%100 != 0 || maxBorrow.get()%10 != 0)
            maxBorrow.set(100);

        if (maxBorrow.get() > 500)
            maxBorrow.set(500);

        if (percentDebt.get() < 10 || percentDebt.get()%10 != 0)
            percentDebt.set(10);

        if (percentDebt.get() > 30)
            percentDebt.set(30);

        if (ImGui.button(Resources.getLocalizedText("return"))) {
            SceneManager.get().goBack();
        }

        ImGui.sameLine();

        if (ImGui.button(Resources.getLocalizedText("create"))) {
            errors.clear();

            if (saveName.isEmpty()) {
                errors.add(Resources.getLocalizedText("saveName"));
            } else if (saveName.get().length() > 16) {
                errors.add(Resources.getLocalizedText("saveTooLong"));
            } else if (Farmland.get().hasSaveWithName(saveName.get())) {
                errors.add(Resources.getLocalizedText("saveAlreadyUsed"));
            }
            if (playerName.isEmpty()) {
                errors.add(Resources.getLocalizedText("playerName"));
            } else {
                if (playerName.get().length() > 10) {
                    errors.add(Resources.getLocalizedText("playerNameTooLong"));
                }
                if (playerName.get().contains("\"")) {
                    errors.add(Resources.getLocalizedText("playerIllegalChar"));
                }
            }
            if (villageName.isEmpty()) {
                errors.add(Resources.getLocalizedText("villageName"));
            } else if (villageName.get().length() > 10) {
                errors.add(Resources.getLocalizedText("villageNameTooLong"));
            }
            if (size[0] < 16 || size[1] < 16 || size[0] > 64 || size[1] > 64) {
                errors.add(Resources.getLocalizedText("correctSize"));
            }
            if (seed.get() <= 0) {
                errors.add(Resources.getLocalizedText("correctSeed"));
            }

            if (errors.isEmpty()) {
                Color braces = new Color(bracesColor);
                Color shirt = new Color(shirtColor);
                Color hat = new Color(hatColor);
                Color buttons = new Color(buttonsColor);

                if (isColorClose(shirtColor, bracesColor)) {
                    braces.contrast(10);
                }

                if (isColorClose(shirtColor, buttonsColor)) {
                    buttons.contrast(10);
                }

                if (isColorClose(bracesColor, buttonsColor)) {
                    buttons.contrast(10);
                }

                Save save = new Save(saveName.get(), playerName.get(), villageName.get(),
                        new Color(bannerColor), braces, shirt, hat, buttons,
                        new Vector2i(size[0], size[1]), seed.get(), numberOfBots[0], maxBorrow.get(), percentDebt.get(), Bot.Difficulty.values()[currentDifficulty.get()]);

                Farmland.get().getSaves().put(save.name, save);
                Farmland.get().loadSave(save.name);
                Farmland.get().writeLoadedSave();

                SceneManager.get().popTypeOfLastScene();

                changeScene(new InGameScene());
            }
        }

        for (String error : errors) {
            ImGui.text(error);
        }

        ImGui.end();
    }

    public boolean isColorClose(float[] color1, float[] color2) {
        return new Color(color1).isCloseTo(new Color(color2));
    }
}
