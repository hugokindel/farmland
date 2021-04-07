package com.ustudents.engine.network;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.core.json.Json;
import com.ustudents.farmland.core.SaveGame;

import java.util.Map;

@SuppressWarnings("unchecked")
public class ClientUpdatorThread extends Thread {
    private static SaveGame updatedSaveGame = null;

    private static Map<String, Object> updatedSaveGameJson = null;

    public static boolean shouldStop = false;

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        final double ns = 1000000000.0 / 1.0;
        double delta = 0;
        while(!shouldStop && Client.socket != null && Client.socket.isBound()) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while(delta >= 1){
                askForUpdate();
                delta--;
            }
        }
    }

    public static SaveGame checkForUpdate() {
        if (updatedSaveGameJson == null) {
            return null;
        }

        SaveGame saveGame = Json.deserialize(updatedSaveGameJson, SaveGame.class);
        assert saveGame != null;
        saveGame.path = "save-server.json";
        saveGame.localPlayerId = Client.playerId;
        updatedSaveGameJson = null;
        return saveGame;
    }

    private static void askForUpdate() {
        try {
            Map<String, Object> answer = Client.request("loadWorld");
            if (Client.socket != null && Client.socket.isBound()) {
                updatedSaveGameJson = (Map<String, Object>)answer.get("world");
            }
        } catch (Exception e) {

        }
    }
}
