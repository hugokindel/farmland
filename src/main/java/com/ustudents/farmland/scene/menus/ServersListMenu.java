package com.ustudents.farmland.scene.menus;

import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.core.event.EventListener;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.network.general.GameInformationsRequest;
import com.ustudents.farmland.network.general.GameInformationsResponse;

@SuppressWarnings("unchecked")
public class ServersListMenu extends MenuScene {
    @Override
    public void initialize() {
        if (!Farmland.get().getClient().isAlive()) {
            Farmland.get().getClient().start();
        }

        boolean localServerExists = Farmland.get().getClient().isAlive();

        int i = 0;
        String[] buttonNames;
        String[] buttonIds;

        if (localServerExists) {
            GameInformationsResponse informations = Farmland.get().getClient().request(new GameInformationsRequest(), GameInformationsResponse.class);
            buttonNames = new String[] {informations.getName() + " (" + informations.getNumberOfConnectedPlayers() + "/" + informations.getCapacity() + ")", "Rechercher une partie", "Recharger"};
            buttonIds = new String[] {"localButton", "addressButton", "reloadButton"};
        } else {
            buttonNames = new String[] {"Rechercher une partie", "Recharger"};
            buttonIds = new String[] {"addressButton", "reloadButton"};
        }

        EventListener[] eventListeners = new EventListener[buttonNames.length];

        for (i = 0; i < buttonNames.length; i++) {
            int j = i;
            eventListeners[i] = (dataType, data) -> {
                switch (buttonIds[j]) {
                    case "localButton":
                        Out.println("Connected to server");

                        changeScene(new ServerWaitingRoomMenu());

                        break;
                    case "addressButton":
                        if (Farmland.get().isConnectedToServer()) {
                            Farmland.get().disconnectFromServer();
                            Farmland.get().getClient().start();
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
            Farmland.get().getClient().start();
        }
    }
}
