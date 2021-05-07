package com.ustudents.farmland.scene.menus;

import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.event.EventListener;
import com.ustudents.farmland.Farmland;

public class SingleplayerMenu extends MenuScene {
    @Override
    public void initialize() {
        String[] buttonNames;
        String[] buttonIds;
        EventListener[] eventListeners;

        if (Farmland.get().getSaves().size() > 0 && (Farmland.get().getSaves().size() > 2 || Farmland.get().getSaves().values().stream().noneMatch(e -> e.path.startsWith("save-server")))) {
            buttonNames = new String[] {
                    Resources.getLocalizedText("newGame"),
                    Resources.getLocalizedText("loadGame"),
                    Resources.getLocalizedText("removeGame"),
                    Resources.getLocalizedText("goBack")
            };
            buttonIds = new String[] {"newButton", "loadButton", "deleteButton","goBack"};
        } else {
            buttonNames = new String[] {
                    Resources.getLocalizedText("newGame"),
                    Resources.getLocalizedText("goBack")
            };
            buttonIds = new String[] {"newButton", "goBack"};
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
                    case "goBack":
                        changeScene(new MainMenu());
                        break;
                }
            };
        }

        initializeMenu(buttonNames, buttonIds, eventListeners, true, false, false, false);

        super.initialize();
    }
}
