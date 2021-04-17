package com.ustudents.farmland.scene.menus;

import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.event.EventListener;
import com.ustudents.engine.graphic.Anchor;
import com.ustudents.engine.graphic.Origin;
import com.ustudents.engine.graphic.Texture;
import com.ustudents.engine.gui.GuiBuilder;
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

        GuiBuilder guiBuilder = new GuiBuilder();

        String resultPath = (isWin)?"ui/victory.png":"ui/defeat.png";
        Texture resultTexture = Resources.loadTexture(resultPath);
        GuiBuilder.ImageData imageData = new GuiBuilder.ImageData(resultTexture);
        imageData.id = "resultImage";
        imageData.origin = new Origin(Origin.Vertical.Top, Origin.Horizontal.Center);
        imageData.anchor = new Anchor(Anchor.Vertical.Top, Anchor.Horizontal.Center);
        imageData.scale = new Vector2f(7f, 7f);
        imageData.position.y = 7;
        guiBuilder.addImage(imageData);


        buttonNames = new String[]{"Rejouer","Menu Principal"};
        buttonIds = new String[]{"Rejouer","Menu Principal"};

        eventListeners = new EventListener[buttonNames.length];

        Farmland.get().loadItemDatabases();
        removeSavedGame();


        for (int i = 0; i < buttonNames.length; i++) {
            int j = i;
            eventListeners[i] = (dataType, data) -> {
                if ("Rejouer".equals(buttonIds[j])) {
                    Player player = currentPlayer;
                    Vector2i vector = new Vector2i(this.currentSave.mapWidth,this.currentSave.mapHeight);
                    Farmland.get().saveId = currentSave.name;
                    Farmland.get().setCurrentSave(new SaveGame(this.currentSave.name, player.name, player.village.name,
                            player.bannerColor, player.avatar.bracesColor, player.avatar.shirtColor,
                            player.avatar.hatColor, player.avatar.buttonsColor, vector, this.currentSave.seed,
                            this.currentSave.players.size()-1, this.currentSave.maxBorrow,
                            this.currentSave.debtRate));
                    Farmland.get().saveSavedGames();
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

        initializeMenu(buttonNames, buttonIds, eventListeners, false, false, false, false);

        super.initialize();
    }

    /**
     * Allow to remove an outdated game saved.
     */
    private void removeSavedGame(){
        String filePath = Resources.getSavesDirectoryName() + "/" + this.currentSave.path;
        if(!new File(filePath).exists())return;
        try{
            Files.delete(Path.of(filePath));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
