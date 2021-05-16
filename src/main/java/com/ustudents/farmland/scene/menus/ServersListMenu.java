package com.ustudents.farmland.scene.menus;

import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.core.event.EventListener;
import com.ustudents.engine.network.Controller;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.network.general.GameInformationsRequest;
import com.ustudents.farmland.network.general.GameInformationsResponse;

public class ServersListMenu extends MenuScene {
    @Override
    public void initialize() {
        if (Farmland.get().isConnectedToServer()) {
            Farmland.get().disconnectFromServer();
        }

        if (!Farmland.get().getClient().isAlive()) {
            Farmland.get().getClient().start(100);
        }

        boolean localServerExists = Farmland.get().getClient().isAlive();

        int i = 0;
        String[] buttonNames;
        String[] buttonIds;

        if (localServerExists) {
            GameInformationsResponse informations = Farmland.get().getClient().request(new GameInformationsRequest(), GameInformationsResponse.class);

            if (informations.getNumberOfConnectedPlayers() < informations.getCapacity()) {
                buttonNames = new String[] {informations.getName() + " (" + informations.getNumberOfConnectedPlayers() + "/" + informations.getCapacity() + ")", Resources.getLocalizedText("researchServer"), Resources.getLocalizedText("reloadServer")};
                buttonIds = new String[] {"localButton", "addressButton", "reloadButton"};
            } else {
                buttonNames = new String[] {Resources.getLocalizedText("researchServer"), Resources.getLocalizedText("reloadServer")};
                buttonIds = new String[] {"addressButton", "reloadButton"};
            }
        } else {
            buttonNames = new String[] {Resources.getLocalizedText("researchServer"), Resources.getLocalizedText("reloadServer")};
            buttonIds = new String[] {"addressButton", "reloadButton"};
        }

        EventListener[] eventListeners = new EventListener[buttonNames.length];

        for (i = 0; i < buttonNames.length; i++) {
            int j = i;
            eventListeners[i] = (dataType, data) -> {
                switch (buttonIds[j]) {
                    case "localButton":
                        Out.println("Connected to server");
                        Farmland.get().clientServerIp = Controller.DEFAULT_ADDRESS;
                        Farmland.get().clientServerPort = Controller.DEFAULT_PORT;
                        changeScene(new ServerWaitingRoomMenu());

                        break;
                    case "addressButton":
                        if (Farmland.get().isConnectedToServer()) {
                            Farmland.get().disconnectFromServer();
                        }

                        changeScene(new ServerCustomMenu());
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

    @Override
    public void destroy() {
        if (Farmland.get().isConnectedToServer()) {
            Farmland.get().disconnectFromServer();
        }
    }
}
