package com.ustudents.farmland.scene.menus;

import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.event.EventListener;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.core.Save;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class DeleteGameMenu extends MenuScene{
    @Override
    public void initialize() {
        int i = 0;
        String[] buttonNames = new String[Farmland.get().getSaves().size()];
        String[] buttonIds = new String[buttonNames.length];
        EventListener[] eventListeners = new EventListener[buttonNames.length];

        for (Save save : Farmland.get().getSaves().values()) {
            int j = i;
            buttonNames[i] = save.name;
            buttonIds[i] = save.path.replace(".json", "") + "Button";
            eventListeners[i] = (dataType, data) -> {
                Farmland.get().loadSave(Farmland.get().getSaveWithFilename(buttonIds[j].replace("Button", "")).name);
                try {
                    Files.delete(new File(Resources.getSavesDirectoryName() + "/" + save.path).toPath());
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

        initializeMenu(buttonNames, buttonIds, eventListeners, true, false, false, true);

        super.initialize();
    }
}
