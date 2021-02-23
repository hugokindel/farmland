package com.ustudents.farmland.scene.menus;

import com.ustudents.engine.core.event.EventListener;

public class SingleplayerMenu extends MenuScene {
    @Override
    public void initialize() {
        String[] buttonNames = {"Nouvelle partie", "Charger partie"};
        String[] buttonIds = {"newButton", "loadButton"};
        EventListener[] eventListeners = new EventListener[buttonNames.length];

        for (int i = 0; i < buttonNames.length; i++) {
            int j = i;
            eventListeners[i] = (dataType, data) -> {
                switch (buttonIds[j]) {
                    case "newButton":
                        changeScene(new NewGameMenu());
                        break;
                    case "loadButton":
                        changeScene(new LoadGameMenu());
                        break;
                }
            };
        }

        initializeMenu(buttonNames, buttonIds, eventListeners, false, false, true);

        super.initialize();
    }
}
