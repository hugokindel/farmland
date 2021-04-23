package com.ustudents.farmland.scene.menus;

import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.event.EventListener;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.core.Save;

import java.nio.file.Files;
import java.nio.file.Path;

public class DeleteGameMenu extends MenuScene{
    @Override
    public void initialize() {
        int i = 0;
        String[] buttonNames = new String[Farmland.get().getSaves().size()+1];
        String[] buttonIds = new String[buttonNames.length+1];
        EventListener[] eventListeners = new EventListener[buttonNames.length+1];

        for (Save save : Farmland.get().getSaves().values()) {
            int j = i;
            buttonNames[i] = save.name;
            buttonIds[i] = save.path.replace(".json", "") + "Button";
            eventListeners[i] = (dataType, data) -> {
                Farmland.get().loadSave(Farmland.get().getSaveWithFilename(buttonIds[j].replace("Button", "")).name);
                try {
                    Files.delete(Path.of(Resources.getSavesDirectoryName() + "/" + save.path));
                }catch (Exception e){
                    e.printStackTrace();
                }
                Farmland.get().getSaves().remove(save.name);
                if(Farmland.get().getSaves().isEmpty())
                    changeScene(new SingleplayerMenu());
                else
                    changeScene(new DeleteGameMenu());
            };
            i++;
        }

        buttonNames[Farmland.get().getSaves().size()] = Resources.getLocalizedText("goBack");
        buttonIds[Farmland.get().getSaves().size()] = "back";
        eventListeners[buttonNames.length-1] = (dataType, data) -> {
            changeScene(new SingleplayerMenu());
        };

        initializeMenu(buttonNames, buttonIds, eventListeners, true, false, false, false);

        super.initialize();
    }
}
