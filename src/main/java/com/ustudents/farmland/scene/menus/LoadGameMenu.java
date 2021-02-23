package com.ustudents.farmland.scene.menus;

import com.ustudents.engine.core.event.EventListener;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.core.SaveGame;
import com.ustudents.farmland.scene.InGameScene;

public class LoadGameMenu extends MenuScene {
    @Override
    public void initialize() {
        int i = 0;
        String[] buttonNames = new String[Farmland.get().getSaveGames().size()];
        String[] buttonIds = new String[buttonNames.length];
        EventListener[] eventListeners = new EventListener[buttonNames.length];

        for (SaveGame save : Farmland.get().getSaveGames().values()) {
            int j = i;
            buttonNames[i] = save.name;
            buttonIds[i] = save.path.replace(".json", "") + "Button";
            eventListeners[i] = (dataType, data) -> {
                Farmland.get().saveId = Farmland.get().getSaveGame(buttonIds[j].replace("Button", "")).name;
                changeScene(new InGameScene());
            };
            i++;
        }

        initializeMenu(buttonNames, buttonIds, eventListeners, false, false, true);

        super.initialize();
    }
}
