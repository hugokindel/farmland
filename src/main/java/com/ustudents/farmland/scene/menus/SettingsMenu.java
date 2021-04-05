package com.ustudents.farmland.scene.menus;

import com.ustudents.engine.audio.Sound;
import com.ustudents.engine.audio.SoundManager;
import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.event.EventListener;
import com.ustudents.engine.scene.component.audio.SoundComponent;
import com.ustudents.engine.scene.ecs.Entity;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.scene.InGameScene;

public class SettingsMenu extends MenuScene {
    @Override
    public void initialize() {
        SoundManager currentSoundManager = Farmland.get().getSoundManager();
        String[] buttonNames = new String[2];
        String[] buttonIds = new String[2];

        if(currentSoundManager.getNoSound()){
            buttonNames[0] = "Activer le son";
            buttonIds[0] = "Activer le son";
        }else{
            buttonNames[0] = "Désactiver le son";
            buttonIds[0] = "Désactiver le son";
        }

        buttonNames[1] = "Retour";
        buttonIds[1] = "Retour";

        EventListener[] eventListeners = new EventListener[buttonNames.length];

        for (int i = 0; i < buttonNames.length; i++) {
            int j = i;
            eventListeners[i] = (dataType, data) -> {
                switch (buttonIds[j]) {
                    case "Activer le son":
                        initializeMusic();
                        Sound musicSound = Resources.loadSound("music/main_menu_background.ogg");
                        Entity music = createEntityWithName("backgroundMusic");
                        music.keepOnLoad(true);
                        new SoundComponent(musicSound, true, true).play();
                        changeScene(new SettingsMenu());
                        break;
                    case "Désactiver le son":
                        currentSoundManager.stopAll();
                        changeScene(new SettingsMenu());
                        break;
                    case "Retour":
                        changeScene(new MainMenu());
                        break;
                }
            };
        }

        initializeMenu(buttonNames, buttonIds, eventListeners, true, false, false, false);

        super.initialize();
    }
}
