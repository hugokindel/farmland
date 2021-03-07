package com.ustudents.farmland.scene.menus;

import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.core.event.EventListener;
import com.ustudents.engine.graphic.Anchor;
import com.ustudents.engine.graphic.Origin;
import com.ustudents.engine.graphic.Texture;
import com.ustudents.engine.gui.GuiBuilder;
import com.ustudents.engine.scene.SceneManager;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.core.SaveGame;
import com.ustudents.farmland.core.player.Player;
import com.ustudents.farmland.scene.InGameScene;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResultMenu extends MenuScene {
    public Player currentPlayer;
    public SaveGame currentSave;
    public boolean isWin;

    @Override
    public void initialize() {
        String[] buttonNames;
        String[] buttonIds;
        EventListener[] eventListeners;

        /*if (isWin){
            Texture titleTexture = Resources.loadTexture("");
        }else{

        }*/


        buttonNames = new String[]{"Rejouer","Menu Principal"};
        buttonIds = new String[]{"Rejouer","Menu Principal"};

        eventListeners = new EventListener[buttonNames.length];

        removeSavedGame();

        for (int i = 0; i < buttonNames.length; i++) {
            int j = i;
            eventListeners[i] = (dataType, data) -> {
                if ("Rejouer".equals(buttonIds[j])) {
                    Player player = currentPlayer;
                    Vector2i vector = new Vector2i(this.currentSave.mapWidth,this.currentSave.mapHeight);
                    Farmland.get().saveId = currentSave.name;
                    Farmland.get().setCurrentSave(new SaveGame(this.currentSave.name, player.name, player.village.name, player.color, vector, this.currentSave.seed, this.currentSave.players.size()-1));
                    changeScene(new InGameScene());
                }else{
                    Farmland.get().getSaveGames().remove(currentSave.name);
                    if (getGame().isConnectedToServer()) {
                        getGame().disconnectFromServer();
                    }
                    changeScene(new MainMenu());
                }
            };
        }
        initializeMenu(buttonNames, buttonIds, eventListeners, false, true,isWin,false, false, false);

        super.initialize();
    }

    /**
     * Allow to remove an outdated game saved.
     */
    private void removeSavedGame(){
        try{
            Files.delete(Path.of(Resources.getSavesDirectoryName() + "/" + this.currentSave.path));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
