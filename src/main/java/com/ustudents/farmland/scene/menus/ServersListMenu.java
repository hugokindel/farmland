package com.ustudents.farmland.scene.menus;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.core.event.EventListener;
import com.ustudents.engine.core.json.Json;
import com.ustudents.engine.network.Client;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.core.SaveGame;
import com.ustudents.farmland.scene.InGameScene;

import java.util.Map;

@SuppressWarnings("unchecked")
public class ServersListMenu extends MenuScene {
    @Override
    public void initialize() {
        boolean localServerExists;

        Map<String, Object> json = Client.commandExists();
        localServerExists = json != null;

        int i = 0;
        String[] buttonNames;
        String[] buttonIds;

        if (localServerExists) {
            buttonNames = new String[] {(String)json.get("name") + " (" + ((Map<String,Object>)json.get("connected")).size() + "/" + json.get("capacity") + ")", "Recharger"};
            buttonIds = new String[] {"localButton", "reloadButton"};
        } else {
            buttonNames = new String[] {"Recharger"};
            buttonIds = new String[] {"reloadButton"};
        }

        EventListener[] eventListeners = new EventListener[buttonNames.length];

        for (i = 0; i < buttonNames.length; i++) {
            int j = i;
            eventListeners[i] = (dataType, data) -> {
                switch (buttonIds[j]) {
                    case "localButton":
                        if (Client.isConnectedToServer()) {
                            Client.commandDisconnect();
                        }
                        Client.commandConnect();

                        Out.println("Connected to server with ID: " + Client.clientId);

                        changeScene(new ServerWaitingRoomMenu());

                        break;
                    case "reloadButton":
                        changeScene(new ServersListMenu(), false);
                        break;
                }
            };
        }

        initializeMenu(buttonNames, buttonIds, eventListeners, true, false, false, true);

        super.initialize();
    }
}
