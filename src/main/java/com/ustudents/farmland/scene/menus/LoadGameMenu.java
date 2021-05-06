package com.ustudents.farmland.scene.menus;

import com.ustudents.engine.core.event.EventListener;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.core.Save;
import com.ustudents.farmland.scene.InGameScene;

public class LoadGameMenu extends MenuScene {
    @Override
    public void initialize() {
        int i = 0;
        String[] buttonNames = new String[Farmland.get().getSaves().size() - (Farmland.get().hasServerSave() ? 1 : 0)];
        String[] buttonIds = new String[buttonNames.length];
        EventListener[] eventListeners = new EventListener[buttonNames.length];

        for (Save save : Farmland.get().getSaves().values()) {
            if (!save.path.startsWith("save-server")) {
                int j = i;
                buttonNames[i] = save.name;
                buttonIds[i] = save.path.replace(".json", "") + "Button";
                eventListeners[i] = (dataType, data) -> {
                    Farmland.get().loadSave(Farmland.get().getSaveWithFilename(buttonIds[j].replace("Button", "")).name);
                    changeScene(new InGameScene());
                };
                i++;
            }
        }

        initializeMenu(buttonNames, buttonIds, eventListeners, true, false, false, true);

        super.initialize();
    }
}