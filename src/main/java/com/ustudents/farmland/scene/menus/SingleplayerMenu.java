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
            buttonNames = new String[] {"Nouvelle partie", "Charger partie", "Supprimer une partie", "Retour"};
            buttonIds = new String[] {"newButton", "loadButton", "deleteButton", "backButton"};
        } else {
            buttonNames = new String[] {"Nouvelle partie", "Retour"};
            buttonIds = new String[] {"newButton", "backButton"};
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
                    case "deleteButton":
                        changeScene(new DeleteGameMenu());
                        break;
                    case "backButton":
                        changeScene(new MainMenu());
                        break;
                }
            };
        }

        initializeMenu(buttonNames, buttonIds, eventListeners, true, false, false, false);

        super.initialize();
    }
}
