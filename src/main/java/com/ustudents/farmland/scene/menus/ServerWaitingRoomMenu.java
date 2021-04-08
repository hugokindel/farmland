package com.ustudents.farmland.scene.menus;

import com.ustudents.engine.core.event.EventListener;
import com.ustudents.engine.network.Client;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.network.GameInformationsRequest;
import com.ustudents.farmland.network.GameInformationsResponse;
import com.ustudents.farmland.network.PlayerExistsRequest;
import com.ustudents.farmland.network.PlayerExistsResponse;

import java.util.Map;

@SuppressWarnings("unchecked")
public class ServerWaitingRoomMenu extends MenuScene {
    @Override
    public void initialize() {
        boolean localServerExists = Farmland.get().getClient().isServerAlive();
        GameInformationsResponse informations = Farmland.get().getClient().request(new GameInformationsRequest(), GameInformationsResponse.class);

        int capacity = informations.getCapacity();
        int connectedPlayers = informations.getNumberOfConnectedPlayers();

        int i = 0;
        String[] buttonNames = new String[capacity - connectedPlayers];
        String[] buttonIds = new String[capacity - connectedPlayers];

        int j = 0;
        for (i = 0; i < capacity; i++) {
            if (!informations.getConnectedPlayerIds().contains(i)) {
                buttonNames[j] = "Joueur " + (i + 1);
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

                    if (Farmland.get().getClient().request(new PlayerExistsRequest(), PlayerExistsResponse.class).exists()) {
                        Farmland.get().clientPlayerId = player;
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
