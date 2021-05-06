package com.ustudents.farmland.scene.menus;

import com.ustudents.engine.core.Resources;
import com.ustudents.engine.graphic.*;
import com.ustudents.engine.gui.GuiBuilder;
import com.ustudents.engine.scene.Scene;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.core.Save;
import com.ustudents.farmland.network.general.LoadSaveRequest;
import com.ustudents.farmland.network.general.LoadSaveResponse;
import com.ustudents.farmland.scene.InGameScene;
import org.joml.Vector2f;

@SuppressWarnings("unchecked")
public class ServerWaitingPlayersMenu extends Scene {
    @Override
    public void initialize() {
        initializeGui();
    }

    public void initializeGui() {
        GuiBuilder guiBuilder = new GuiBuilder();

        GuiBuilder.ButtonData buttonData2 = new GuiBuilder.ButtonData(Resources.getLocalizedText("mainMenu"), (dataType, data) -> {
            Farmland.get().unloadSave();
            if (getGame().isConnectedToServer()) {
                getGame().disconnectFromServer();
            }
            changeScene(new MainMenu());
        });
        buttonData2.origin = new Origin(Origin.Vertical.Bottom, Origin.Horizontal.Left);
        buttonData2.anchor = new Anchor(Anchor.Vertical.Bottom, Anchor.Horizontal.Left);
        buttonData2.position = new Vector2f(10, -10);
        guiBuilder.addButton(buttonData2);

        GuiBuilder.TextData textData = new GuiBuilder.TextData(Resources.getLocalizedText("waitingPlayers"));
        textData.origin = new Origin(Origin.Vertical.Middle, Origin.Horizontal.Center);
        textData.anchor = new Anchor(Anchor.Vertical.Middle, Anchor.Horizontal.Center);
        guiBuilder.addText(textData);
    }

    @Override
    public void update(float dt) {
        if (Farmland.get().clientAllPlayersPresents.get()) {
            Farmland.get().clientAllPlayersPresents.set(false);
            Farmland.get().getClient().request(new LoadSaveRequest(), LoadSaveResponse.class);
            Save save = LoadSaveResponse.getUpdatedSaveGame();
            Farmland.get().getSaves().put(save.name, save);
            Farmland.get().loadSave(save.name);
            changeScene(new InGameScene());
        }
    }
}
