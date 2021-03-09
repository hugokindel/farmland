package com.ustudents.farmland.scene.menus;

import com.ustudents.engine.core.event.EventListener;
import com.ustudents.farmland.Farmland;

public class SingleplayerMenu extends MenuScene {
    @Override
    public void initialize() {
        String[] buttonNames;
        String[] buttonIds;
        EventListener[] eventListeners;

        if (Farmland.get().getSaveGames().size() > 0) {
            buttonNames = new String[] {"Nouvelle partie", "Charger partie"};
            buttonIds = new String[] {"newButton", "loadButton"};
        } else {
            buttonNames = new String[] {"Nouvelle partie"};
            buttonIds = new String[] {"newButton"};
        }

        eventListeners = new EventListener[buttonNames.length];

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

        initializeMenu(buttonNames, buttonIds, eventListeners, true, false, false, true);

        super.initialize();
    }
}
