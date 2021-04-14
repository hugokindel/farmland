package com.ustudents.farmland.scene.menus;

import com.ustudents.engine.core.cli.print.Out;
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

public class ServerCustomMenu extends MenuScene {
    ImString address = new ImString();
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

        initializeMenu(buttonNames, buttonIds, eventListeners, true, false, false, true);

        super.initialize();
    }

    @Override
    public void renderImGui() {
        ImGuiUtils.setNextWindowWithSizeCentered(550, 300, ImGuiCond.Appearing);
        ImGui.begin("Rechercher une partie");

        ImGui.text("Entrez l'adresse ip et le port du serveur:");
        ImGui.pushItemWidth(-1);
        ImGui.inputText("##label", address);
        ImGui.popItemWidth();
        ImGui.text("Exemple: 127.0.0.1:8533");

        if (ImGui.button("Retour")) {
            SceneManager.get().goBack();
        }

        ImGui.sameLine();

        if (ImGui.button("Rejoindre")) {
            errors.clear();

            if (address.isEmpty()) {
                errors.add("Veuillez entrer une adresse et un port!");
            }

            if (!address.isEmpty() && address.get().split(":").length != 2) {
                errors.add("Veuillez entrer un format valide!");
            }

            if (errors.isEmpty()) {
                SceneManager.get().popTypeOfLastScene();

                if (!Farmland.get().getClient().isAlive()) {
                    String[] rAddress = address.get().split(":");
                    Farmland.get().getClient().start(rAddress[0], Integer.parseInt(rAddress[1]));
                }

                boolean serverExists = Farmland.get().getClient().isAlive();

                if (serverExists) {
                    changeScene(new ServerWaitingRoomMenu());
                } else {
                    Out.println("Ce serveur ne répond pas.");
                }
            }
        }

        for (String error : errors) {
            ImGui.text(error);
        }

        ImGui.end();
    }
}
