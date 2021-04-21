package com.ustudents.farmland.scene.menus;

import com.ustudents.engine.core.cli.print.Out;
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
    float[] bannerColor = {1, 0, 0, 1};
    float[] bracesColor = {0.7098f, 0.2745f, 0.3921f, 1f};
    float[] shirtColor = {0.2627f, 0.5607f, 0.4392f, 1f};
    float[] hatColor = {0.7098f, 0.2745f, 0.3921f, 1f};
    float[] buttonsColor = {0.9843f, 0.7764f, 0.2117f, 1f};
    int[] size = {16, 16};
    ImLong seed = new ImLong(System.currentTimeMillis());
    List<String> errors = new ArrayList<>();
    int[] numberOfBots = new int[1];
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
        ImGuiUtils.setNextWindowWithSizeCentered(850, 400, ImGuiCond.Appearing);
        ImGui.begin("Nouvelle partie");

        ImGui.text("Entrez les informations de la sauvegarde:");
        ImGui.inputText("Nom de la partie", saveName);
        ImGui.inputText("Votre nom de joueur", playerName);
        ImGui.inputText("Votre nom de village", villageName);
        ImGui.colorEdit4("Couleur de votre bannière", bannerColor);
        ImGui.colorEdit4("Couleur de votre combinaison à bretelles", bracesColor);
        ImGui.colorEdit4("Couleur de votre chemise", shirtColor);
        ImGui.colorEdit4("Couleur de votre chapeau", hatColor);
        ImGui.colorEdit4("Couleur de vos boutons de bretelles", buttonsColor);
        ImGui.inputInt2("Taille de la carte", size);
        ImGui.inputScalar("Graine de la carte", ImGuiDataType.S64, seed);
        ImGui.sliderInt("Nombre de robots", numberOfBots, 0, 3);
        ImGui.inputInt("Somme maximal à emprunter", maxBorrow,100);
        ImGui.inputInt("taux de remboursement de l'emprunt", percentDebt,10);

        if (maxBorrow.get() < 100 || maxBorrow.get()%100 != 0 || maxBorrow.get()%10 != 0)
            maxBorrow.set(100);

        if (maxBorrow.get() > 500)
            maxBorrow.set(500);

        if (percentDebt.get() < 10 || percentDebt.get()%10 != 0)
            percentDebt.set(10);

        if (percentDebt.get() > 30)
            percentDebt.set(30);

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

                SaveGame saveGame = new SaveGame(saveName.get(), playerName.get(), villageName.get(),
                        new Color(bannerColor), braces, shirt, hat, buttons,
                        new Vector2i(size[0], size[1]), seed.get(), numberOfBots[0], maxBorrow.get(), percentDebt.get());

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

    public boolean isColorClose(float[] color1, float[] color2) {
        return new Color(color1).isCloseTo(new Color(color2));
    }
}
