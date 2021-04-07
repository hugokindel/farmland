package com.ustudents.farmland.scene.menus;

import com.ustudents.engine.core.json.Json;
import com.ustudents.engine.graphic.*;
import com.ustudents.engine.gui.GuiBuilder;
import com.ustudents.engine.network.Client;
import com.ustudents.engine.network.ClientUpdatorThread;
import com.ustudents.engine.network.ServerCheckOtherThread;
import com.ustudents.engine.scene.Scene;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.core.SaveGame;
import com.ustudents.farmland.scene.InGameScene;
import org.joml.Vector2f;

import java.util.Map;

@SuppressWarnings("unchecked")
public class ServerWaitingPlayersMenu extends Scene {
    ServerCheckOtherThread thread;

    @Override
    public void initialize() {
        thread = new ServerCheckOtherThread();
        thread.start();

        initializeGui();
    }

    public void initializeGui() {
        GuiBuilder guiBuilder = new GuiBuilder();

        GuiBuilder.ButtonData buttonData2 = new GuiBuilder.ButtonData("Menu principal", (dataType, data) -> {
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

        GuiBuilder.TextData textData = new GuiBuilder.TextData("En attente des autres joueurs...");
        textData.origin = new Origin(Origin.Vertical.Middle, Origin.Horizontal.Center);
        textData.anchor = new Anchor(Anchor.Vertical.Middle, Anchor.Horizontal.Center);
        guiBuilder.addText(textData);
    }

    @Override
    public void update(float dt) {
        if (ServerCheckOtherThread.checkForUpdate()) {
            Map<String, Object> answer = Client.request("loadWorld");
            SaveGame saveGame = Json.deserialize((Map<String, Object>)answer.get("world"), SaveGame.class);
            assert saveGame != null;
            saveGame.path = "save-server.json";
            saveGame.localPlayerId = Client.playerId;
            Farmland.get().getSaveGames().put(saveGame.name, saveGame);
            Farmland.get().loadSave(saveGame.name);
            changeScene(new InGameScene());
        }
    }
}
