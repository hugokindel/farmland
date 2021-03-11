package com.ustudents.farmland.scene.menus;

import com.ustudents.engine.core.event.EventListener;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.graphic.imgui.ImGuiUtils;
import com.ustudents.engine.scene.SceneManager;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.core.SaveGame;
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
        ImGui.begin("Nouvelle partie");

        ImGui.text("Entrez les informations de la sauvegarde:");
        ImGui.inputText("Nom de la partie", saveName);
        ImGui.inputText("Votre nom de joueur", playerName);
        ImGui.inputText("Votre nom de village", villageName);
        ImGui.colorEdit4("Couleur de votre bannière", color);
        ImGui.inputInt2("Taille de la carte", size);
        ImGui.inputScalar("Graine de la carte", ImGuiDataType.S64, seed);
        ImGui.sliderInt("Nombre de robots", numberOfBots, 0, 3);

        if (ImGui.button("Retour")) {
            SceneManager.get().goBack();
        }

        ImGui.sameLine();

        if (ImGui.button("Créer")) {
            errors.clear();

            if (saveName.isEmpty()) {
                errors.add("Veuillez entrer un nom de partie !");
            }
            if (playerName.isEmpty()) {
                errors.add("Veuillez entrer un nom de joueur !");
            }
            if (villageName.isEmpty()) {
                errors.add("Veuillez entrer un nom de village !");
            }
            if (size[0] < 16 || size[1] < 16) {
                errors.add("Veuillez entrer une taille de carte valide (>= 16) !");
            }
            if (seed.get() <= 0) {
                errors.add("Veuillez entrer une graine valide (> 0) !");
            }

            if (errors.isEmpty()) {
                SaveGame saveGame = new SaveGame(saveName.get(), playerName.get(), villageName.get(), new Color(color[0], color[1], color[2], color[3]), new Vector2i(size[0], size[1]), seed.get(), numberOfBots[0]);

                Farmland.get().getSaveGames().put(saveGame.name, saveGame);
                Farmland.get().saveId = saveGame.name;
                Farmland.get().saveSavedGames();

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
