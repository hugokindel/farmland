package com.ustudents.farmland.scene.menus;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.event.EventListener;
import com.ustudents.engine.graphic.Anchor;
import com.ustudents.engine.graphic.Origin;
import com.ustudents.engine.graphic.Texture;
import com.ustudents.engine.gui.GuiBuilder;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.core.Save;
import com.ustudents.farmland.core.player.Player;
import com.ustudents.farmland.scene.InGameScene;
import org.joml.Vector2f;

public class ResultMenu extends MenuScene {
    public Save currentSave;
    public boolean isWin;
    public boolean comeFromServer;

    @Override
    public void initialize() {
        if (getGame().isConnectedToServer()) {
            getGame().disconnectFromServer();
        }

        String[] buttonNames;
        String[] buttonIds;
        EventListener[] eventListeners;

        GuiBuilder guiBuilder = new GuiBuilder();

        String pathStart = "ui/";

        if (Resources.getConfig().language.equals("fr")) {
            pathStart += "fr/";
        } else {
            pathStart += "en/";
        }

        String resultPath = pathStart + ((isWin) ? "victory.png" : "defeat.png");

        Texture resultTexture = Resources.loadTexture(resultPath);
        GuiBuilder.ImageData imageData = new GuiBuilder.ImageData(resultTexture);
        imageData.id = "resultImage";
        imageData.origin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Center);
        imageData.anchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Center);
        imageData.scale = new Vector2f(7f, 7f);
        imageData.position.y = 7;
        guiBuilder.addImage(imageData);

        if (Game.get().isConnectedToServer() || comeFromServer) {
            buttonNames = new String[]{};
            buttonIds = new String[]{};
        } else {
            buttonNames = new String[]{Resources.getLocalizedText("replay")};
            buttonIds = new String[]{"replay"};
        }

        eventListeners = new EventListener[buttonNames.length];

        currentSave.removeFile();
        Farmland.get().readAllSaves();

        for (int i = 0; i < buttonNames.length; i++) {
            int j = i;
            eventListeners[i] = (dataType, data) -> {
                if ("replay".equals(buttonIds[j])) {
                    Farmland.get().loadedSaveId = currentSave.name;
                    Farmland.get().replaceLoadedSave(new Save(currentSave));
                    Farmland.get().writeLoadedSave();

                    changeScene(new InGameScene());
                }
            };
        }

        initializeMenu(buttonNames, buttonIds, eventListeners, false, true, false, false);

        super.initialize();
    }
}
