package com.ustudents.farmland.scene.menus;

import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.core.event.EventListener;
import com.ustudents.engine.core.window.Window;
import com.ustudents.farmland.Farmland;
import imgui.ImGui;
import imgui.ImVec2;

public class MainMenu extends MenuScene {
    @Override
    public void initialize() {
        Farmland.get().saveId = null;

        String[] buttonNames = {"Solo", "Multijoueur", "Paramètres", "Crédits"};
        String[] buttonIds = {"singleplayerButton", "multiplayerButton", "settingsButton", "creditsButton"};
        EventListener[] eventListeners = new EventListener[buttonNames.length];

        for (int i = 0; i < buttonNames.length; i++) {
            int j = i;
            eventListeners[i] = (dataType, data) -> {
                switch (buttonIds[j]) {
                    case "singleplayerButton":
                        changeScene(new SingleplayerMenu());
                        break;
                    case "multiplayerButton":
                        changeScene(new MultiplayerMenu());
                        break;
                    case "settingsButton":
                        changeScene(new SettingsMenu());
                        break;
                    case "creditsButton":
                        changeScene(new CreditsMenu());
                        break;
                }
            };
        }

        initializeMenu(buttonNames, buttonIds, eventListeners, true, false, true, false);

        super.initialize();
    }

    @Override
    public void update(float dt) {

    }
}
