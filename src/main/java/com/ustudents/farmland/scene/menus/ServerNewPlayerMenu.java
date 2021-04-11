package com.ustudents.farmland.scene.menus;

import com.ustudents.engine.core.event.EventListener;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.graphic.imgui.ImGuiUtils;
import com.ustudents.engine.scene.SceneManager;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.network.PlayerCreateMessage;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.type.ImString;

import java.util.ArrayList;
import java.util.List;

public class ServerNewPlayerMenu extends MenuScene {
    ImString playerName = new ImString();
    ImString villageName = new ImString();
    float[] color = {1, 0, 0, 1};
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
        ImGuiUtils.setNextWindowWithSizeCentered(550, 300, ImGuiCond.Appearing);
        ImGui.begin("Création du joueur " + (playerId + 1));

        ImGui.text("Entrez les informations de la sauvegarde:");
        ImGui.inputText("Votre nom de joueur", playerName);
        ImGui.inputText("Votre nom de village", villageName);
        ImGui.colorEdit4("Couleur de votre bannière", color);

        if (ImGui.button("Retour")) {
            SceneManager.get().goBack();
        }

        ImGui.sameLine();

        if (ImGui.button("Créer")) {
            errors.clear();

            if (playerName.isEmpty()) {
                errors.add("Veuillez entrer un nom de joueur !");
            }
            if (villageName.isEmpty()) {
                errors.add("Veuillez entrer un nom de village !");
            }

            if (errors.isEmpty()) {
                Farmland.get().getClient().send(new PlayerCreateMessage(playerId, playerName.get(), villageName.get(), new Color(color[0], color[1], color[2], color[3])));
                Farmland.get().clientPlayerId.set(playerId);
                changeScene(new ServerWaitingPlayersMenu());
            }
        }

        for (String error : errors) {
            ImGui.text(error);
        }

        ImGui.end();
    }
}
