package com.ustudents.farmland.scene.menus;

import com.ustudents.engine.audio.Sound;
import com.ustudents.engine.audio.SoundManager;
import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.event.EventListener;
import com.ustudents.engine.core.window.Window;
import com.ustudents.engine.ecs.component.audio.SoundComponent;
import com.ustudents.engine.ecs.Entity;
import com.ustudents.engine.graphic.Origin;
import com.ustudents.farmland.Farmland;

public class SettingsMenu extends MenuScene {
    @Override
    public void initialize() {
        hasButtonNearBack = true;

        goBackButtonOrigin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Right);
        goBackButtonSpacing.x = -10;
        buttonNearBackOrigin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Left);
        buttonNearBackSpacing.x = 10;

        if (Resources.getConfig().language.equals("fr")) {
            buttonNearBackSpacing.y = -2;
        }

        SoundManager currentSoundManager = Farmland.get().getSoundManager();
        String[] buttonNames = new String[5];
        String[] buttonIds = new String[5];

        if(getEntityByName("backgroundMusic").getComponent(SoundComponent.class).source.isPaused() || getEntityByName("backgroundMusic").getComponent(SoundComponent.class).source.isStopped()){
            buttonNames[0] = Resources.getLocalizedText("activateSound");
            buttonIds[0] = "activateSound";
        }else{
            buttonNames[0] = Resources.getLocalizedText("deactivateSound");
            buttonIds[0] = "deactivateSound";
        }

        buttonNames[1] = Resources.getLocalizedText("changeLanguage", Resources.getLocalizedText("language"));
        buttonIds[1] = "changeLanguage";

        buttonNames[2] = Resources.getLocalizedText("display", Resources.getLocalizedText(Window.Type.values()[Resources.getConfig().windowType.ordinal()].name().toLowerCase()));
        buttonIds[2] = "windowType";

        buttonNames[3] = Resources.getLocalizedText("commands");
        buttonIds[3] = "commands";

        buttonNames[4] = Resources.getLocalizedText("reset");
        buttonIds[4] = "reset";

        EventListener[] eventListeners = new EventListener[buttonNames.length];

        for (int i = 0; i < buttonNames.length; i++) {
            int j = i;
            eventListeners[i] = (dataType, data) -> {
                switch (buttonIds[j]) {
                    case "activateSound": {
                        getEntityByName("backgroundMusic").getComponent(SoundComponent.class).play();
                        changeScene(new SettingsMenu(), false);
                        break;
                    }
                    case "deactivateSound": {
                        getEntityByName("backgroundMusic").getComponent(SoundComponent.class).pause();
                        changeScene(new SettingsMenu(), false);
                        break;
                    }
                    case "changeLanguage": {
                        Resources.chooseNextLanguage();
                        changeScene(new SettingsMenu(), false);
                        break;
                    }
                    case "windowType": {
                        Window.get().chooseNextType();
                        changeScene(new SettingsMenu(), false);
                        break;
                    }
                    case "commands": {
                        changeScene(new SettingsKeybindMenu());
                        break;
                    }
                    case "reset": {
                        Farmland.get().resetConfig();
                        if (getEntityByName("backgroundMusic").getComponent(SoundComponent.class).source.isPaused() || getEntityByName("backgroundMusic").getComponent(SoundComponent.class).source.isStopped()) {
                            getEntityByName("backgroundMusic").getComponent(SoundComponent.class).play();
                        }
                        Resources.chooseDefaultLanguage();
                        Window.get().chooseDefaultType();
                        changeScene(new SettingsMenu(), false);
                        break;
                    }
                }
            };
        }

        initializeMenu(buttonNames, buttonIds, eventListeners, true, false, false, true);

        super.initialize();
    }
}
