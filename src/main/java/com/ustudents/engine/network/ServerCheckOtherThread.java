package com.ustudents.engine.network;

import com.ustudents.engine.core.json.Json;
import com.ustudents.farmland.core.SaveGame;
import com.ustudents.farmland.scene.InGameScene;

import java.util.Map;

public class ServerCheckOtherThread extends Thread {
    static boolean canStart = false;

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        final double ns = 1000000000.0 / 10.0;
        double delta = 0;
        while(!canStart && Client.socket != null && Client.socket.isBound()) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while(delta >= 1){
                askForUpdate();
                delta--;
            }
        }
    }

    public static boolean checkForUpdate() {
        return canStart;
    }

    private static void askForUpdate() {
        if (Client.commandCanPlay()) {
            canStart = true;
        }
    }
}
