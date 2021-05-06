package com.ustudents.farmland.scene.menus;

import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.core.event.EventListener;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.network.general.*;

@SuppressWarnings("unchecked")
public class ServerWaitingRoomMenu extends MenuScene {
    @Override
    public void initialize() {
        if (!Farmland.get().isConnectedToServer()) {
            Farmland.get().getClient().start(Farmland.get().clientServerIp, Farmland.get().clientServerPort);
        }

        boolean localServerExists = Farmland.get().getClient().isAlive();
        GameInformationsResponse informations = Farmland.get().getClient().request(new GameInformationsRequest(), GameInformationsResponse.class);

        int capacity = informations.getCapacity();
        int connectedPlayers = informations.getNumberOfConnectedPlayers();

        int i;
        String[] buttonNames = new String[capacity - connectedPlayers];
        String[] buttonIds = new String[capacity - connectedPlayers];

        int j = 0;
        for (i = 0; i < capacity; i++) {
            if (!informations.getConnectedPlayerIds().contains(i)) {
                buttonNames[j] = Resources.getLocalizedText("player") + " " + (i + 1);
                buttonIds[j] = (i + 1) + "Button";
                j++;
            }
        }

        EventListener[] eventListeners = new EventListener[buttonNames.length];

        for (i = 0; i < buttonNames.length; i++) {
            int k = i;
            eventListeners[i] = (dataType, data) -> {
                if (Character.isDigit(buttonIds[k].charAt(0))) {
                    int player = Integer.parseInt(String.valueOf(buttonIds[k].charAt(0))) - 1;

                    if (Farmland.get().getClient().request(new PlayerExistsRequest(player), PlayerExistsResponse.class).exists()) {
                        Farmland.get().getClient().send(new PlayerAddMessage(player));
                        Farmland.get().clientPlayerId.set(player);
                        changeScene(new ServerWaitingPlayersMenu());
                    } else {
                        changeScene(new ServerNewPlayerMenu(player));
                    }
                }
            };
        }

        initializeMenu(buttonNames, buttonIds, eventListeners, true, false, false, true);

        super.initialize();
    }
}
