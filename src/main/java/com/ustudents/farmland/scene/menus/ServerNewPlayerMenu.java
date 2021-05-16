package com.ustudents.farmland.scene.menus;

import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.event.EventListener;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.graphic.imgui.ImGuiUtils;
import com.ustudents.engine.scene.SceneManager;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.core.player.Avatar;
import com.ustudents.farmland.core.player.Player;
import com.ustudents.farmland.network.general.PlayerCreateMessage;
import com.ustudents.farmland.network.general.PlayerExistsRequest;
import com.ustudents.farmland.network.general.PlayerExistsResponse;
import com.ustudents.farmland.network.general.PlayerWithNameExistsRequest;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.type.ImString;

import java.util.ArrayList;
import java.util.List;

public class ServerNewPlayerMenu extends MenuScene {
    ImString playerName = new ImString();
    ImString villageName = new ImString();
    float[] bannerColor = Player.DEFAULT_BANNER_COLOR.toArray();
    float[] bracesColor = Avatar.DEFAULT_BRACES_COLOR.toArray();
    float[] shirtColor = Avatar.DEFAULT_SHIRT_COLOR.toArray();
    float[] hatColor = Avatar.DEFAULT_HAT_COLOR.toArray();
    float[] buttonsColor = Avatar.DEFAULT_BUTTONS_COLOR.toArray();
    List<String> errors = new ArrayList<>();
    int playerId;

    public ServerNewPlayerMenu(int playerId) {
        this.playerId = playerId;
    }

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
        ImGuiUtils.setNextWindowWithSizeCentered(650, 300, ImGuiCond.Appearing);
        ImGui.begin(Resources.getLocalizedText("createPlayer") + (playerId + 1));

        ImGui.text(Resources.getLocalizedText("newGameDescription"));
        ImGui.inputText(Resources.getLocalizedText("ngPlayerName"), playerName);
        ImGui.inputText(Resources.getLocalizedText("ngVillageName"), villageName);
        ImGui.colorEdit4(Resources.getLocalizedText("ngBannerColor"), bannerColor);
        ImGui.colorEdit4(Resources.getLocalizedText("ngSuitColor"), bracesColor);
        ImGui.colorEdit4(Resources.getLocalizedText("ngShirtColor"), shirtColor);
        ImGui.colorEdit4(Resources.getLocalizedText("ngHatColor"), hatColor);
        ImGui.colorEdit4(Resources.getLocalizedText("ngButtonsColor"), buttonsColor);

        if (ImGui.button(Resources.getLocalizedText("return"))) {
            SceneManager.get().goBack();
        }

        ImGui.sameLine();

        if (ImGui.button(Resources.getLocalizedText("create"))) {
            errors.clear();

            if (playerName.isEmpty()) {
                errors.add(Resources.getLocalizedText("playerName"));
            } else if (playerName.get().length() > 10) {
                errors.add(Resources.getLocalizedText("playerNameTooLong"));
            }
            if (villageName.isEmpty()) {
                errors.add(Resources.getLocalizedText("villageName"));
            } else if (villageName.get().length() > 10) {
                errors.add(Resources.getLocalizedText("villageNameTooLong"));
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

                if (Farmland.get().getClient().request(new PlayerExistsRequest(playerId), PlayerExistsResponse.class).exists()) {
                    errors.add(Resources.getLocalizedText("playerExists2", playerId + 1));
                } else if (Farmland.get().getClient().request(new PlayerWithNameExistsRequest(playerName.get()), PlayerExistsResponse.class).exists()) {
                    errors.add(Resources.getLocalizedText("playerExists"));
                } else {
                    Farmland.get().getClient().send(new PlayerCreateMessage(playerId, playerName.get(), villageName.get(), new Color(bannerColor), braces, shirt, hat, buttons));
                    Farmland.get().clientPlayerId.set(playerId);
                    changeScene(new ServerWaitingPlayersMenu());
                }
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
