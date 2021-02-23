package com.ustudents.farmland.core;

import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.event.EventDispatcher;
import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.graphic.Sprite;
import com.ustudents.engine.graphic.Texture;
import com.ustudents.engine.utility.SeedRandom;
import com.ustudents.farmland.core.grid.Cell;
import com.ustudents.farmland.core.player.Player;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@JsonSerializable
public class SaveGame {
    public static final int timePerTurn = 90;

    @JsonSerializable
    public String name;

    @JsonSerializable
    public Integer turn;

    @JsonSerializable
    public Integer turnTimePassed;

    @JsonSerializable
    public Long seed;

    @JsonSerializable
    public Integer currentPlayerId;

    @JsonSerializable
    public List<Player> players;

    @JsonSerializable
    public List<List<Cell>> cells;

    public String path;

    public EventDispatcher turnEnded = new EventDispatcher();

    public SaveGame() {

    }

    public SaveGame(String name, String playerName, String playerVillageName, Color playerColor, Vector2i mapSize, Long seed) {
        this.seed = seed;

        if (this.seed == null) {
            this.seed = System.currentTimeMillis();
        }

        SeedRandom random = new SeedRandom(this.seed);

        this.turn = 0;
        this.turnTimePassed = 0;
        this.currentPlayerId = 0;
        this.name = name;
        this.players = new ArrayList<>();
        this.players.add(new Player(playerName, playerVillageName, playerColor));
        this.players.get(0).village.position = new Vector2f(5 + (mapSize.x / 2) * 24, 5 + (mapSize.y / 2) * 24);

        this.cells = new ArrayList<>();

        Texture cellBackground = Resources.loadTexture("map/grass.png");
        
        for (int x = 0; x < mapSize.x; x++) {
            this.cells.add(new ArrayList<>());

            for (int y = 0; y < mapSize.y; y++) {
                Vector2f spriteRegion = new Vector2f(
                        24 * random.generateInRange(1, cellBackground.getWidth() / 24),
                        24 * random.generateInRange(1, cellBackground.getHeight() / 24));
                Sprite sprite = new Sprite(cellBackground,
                        new Vector4f(spriteRegion.x, spriteRegion.y, 24, 24));
                Vector4f viewRectangle = new Vector4f(
                        5 + x * 24,
                        5 + y * 24,
                        5 + x * 24 + 24,
                        5 + y * 24 + 24);

                Cell cell = new Cell(sprite, viewRectangle);

                if ((x == mapSize.x / 2 && y == mapSize.y / 2) ||
                        (x == mapSize.x / 2 - 1 && y == mapSize.y / 2) ||
                        (x == mapSize.x / 2 && y == mapSize.y / 2 - 1) ||
                        (x == mapSize.x / 2 - 1 && y == mapSize.y / 2 - 1)) {
                    cell.setOwned(true);
                }

                cells.get(x).add(cell);
            }
        }

        File f = new File(Resources.getSavesDirectoryName());
        this.path = "save-" + f.list().length + ".json";
    }

    public void endTurn() {
        if (currentPlayerId == players.size() - 1) {
            turn++;
            currentPlayerId = 0;
        } else {
            currentPlayerId++;
        }

        turnEnded.dispatch();
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerId);
    }
}
