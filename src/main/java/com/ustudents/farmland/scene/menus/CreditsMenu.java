package com.ustudents.farmland.scene.menus;

import com.ustudents.engine.core.event.EventListener;

public class CreditsMenu extends MenuScene {
    @Override
    public void initialize() {
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

        initializeMenu(buttonNames, buttonIds, eventListeners, true, false,false,false, false, true);

        super.initialize();
    }
}
