package com.ustudents.farmland.component;

import com.ustudents.engine.Game;
import com.ustudents.engine.network.NetMode;
import com.ustudents.engine.ecs.component.core.BehaviourComponent;
import com.ustudents.engine.ecs.component.core.TransformComponent;
import com.ustudents.engine.ecs.component.graphic.*;
import com.ustudents.engine.graphic.*;
import com.ustudents.engine.graphic.imgui.annotation.Viewable;
import com.ustudents.engine.input.Input;
import com.ustudents.engine.input.Key;
import com.ustudents.engine.input.MouseButton;
import com.ustudents.engine.utility.SeedRandom;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.core.grid.Cell;
import com.ustudents.farmland.scene.InGameScene;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class GridComponent extends BehaviourComponent implements RenderableComponent {
    @Viewable
    public Vector2i gridSize;

    @Viewable
    public NineSlicedSprite gridBackground;

    @Viewable
    public Texture cellBackground;

    @Viewable
    public AnimatedSprite selectionCursor;

    @Viewable
    public Spritesheet territoryTexture;

    @Viewable
    public List<List<Cell>> cells;

    private Vector2i currentSelectedCell;

    private Vector2i cellSize;

    private Vector2i gridBackgroundSideSize;

    private boolean selectionCursorEnabled;

    private boolean showTypeOfTerritory;

    public GridComponent(Vector2i gridSize, Vector2i cellSize, NineSlicedSprite gridBackground, Texture cellBackground,
                         AnimatedSprite selectionCursor, Spritesheet territoryTexture) {
        this.gridSize = gridSize;
        this.gridBackground = gridBackground;
        this.cellBackground = cellBackground;
        this.selectionCursor = selectionCursor;
        this.territoryTexture = territoryTexture;
        this.currentSelectedCell = new Vector2i(-1, -1);
        this.cellSize = cellSize;
        this.gridBackgroundSideSize = new Vector2i(5, 5);
        this.selectionCursorEnabled = true;
        this.showTypeOfTerritory = false;
    }

    @Override
    public void initialize() {
        recalculateCells();

        Farmland.get().loadedSaveChanged.add((dataType, data) -> recalculateCells());
    }

    @Override
    public void onSceneLoaded() {
        TransformComponent transformComponent = getEntity().getComponent(TransformComponent.class);

        Camera camera = getScene().getWorldCamera();
        camera.setMinimalX(transformComponent.position.x + gridBackgroundSideSize.x);
        camera.setMaximalX(transformComponent.position.x + gridBackgroundSideSize.x + getGridSize().x * cellSize.x);
        camera.setMinimalY(transformComponent.position.x + gridBackgroundSideSize.y);
        camera.setMaximalY(transformComponent.position.y + gridBackgroundSideSize.y + getGridSize().x * cellSize.y);

        if (Farmland.get().getLoadedSave() == null) {
            camera.centerOnPosition(new Vector2f(
                    transformComponent.position.x + gridBackgroundSideSize.x + (getGridSize().x * cellSize.x) / 2.0f,
                    transformComponent.position.y + gridBackgroundSideSize.y + (getGridSize().y * cellSize.y) / 2.0f));
        } else {
            if (Farmland.get().getNetMode() != NetMode.DedicatedServer) {
                if (Farmland.get().getLoadedSave().getLocalPlayer().position != null) {
                    camera.centerOnPosition(new Vector2f(
                            Farmland.get().getLoadedSave().getLocalPlayer().position.x,
                            Farmland.get().getLoadedSave().getLocalPlayer().position.y));
                } else {
                    camera.centerOnPosition(new Vector2f(
                            Farmland.get().getLoadedSave().getLocalPlayer().village.position.x + 24,
                            Farmland.get().getLoadedSave().getLocalPlayer().village.position.y + 24));
                }
            }
        }

        camera.moved.add((dataType, data) -> {
            Camera.PositionChanged eventData = (Camera.PositionChanged) data;
            if (Farmland.get().getLoadedSave() != null) {
                Farmland.get().getLoadedSave().getLocalPlayer().position = new Vector2f(eventData.position.x, eventData.position.y);
            }
        });
    }

    @Override
    public void update(float dt) {
        selectionCursor.update(dt);

        showTypeOfTerritory = Input.isActionSuccessful("showTerritory");

        if (currentSelectedCell.x == -1 || !Input.isMouseInWorldViewRect(
                getCell(currentSelectedCell).viewRectangle)) {
            updateCurrentSelectedCell();
        }

        if (selectionDrawingEnabled() && currentSelectedCell.x != -1 &&
                !Input.isKeyDown(Key.LeftAlt) && !Input.isKeyDown(Key.RightAlt) &&
                Input.isMousePressed(MouseButton.Left) &&
                Farmland.get().getLoadedSave().getCurrentPlayer().getId().equals(Farmland.get().getLoadedSave().getLocalPlayer().getId()) &&
                cellIsOwnedByLocalPlayer(currentSelectedCell.x, currentSelectedCell.y) && getCell(currentSelectedCell).isOwnedByCurrentPlayer() &&
                Farmland.get().getLoadedSave().getLocalPlayer().selectedItemId != null
                && !getCell(currentSelectedCell).hasItem()) {
            Farmland.get().getLoadedSave().getLocalPlayer().placeSelectedItem(currentSelectedCell);
        }

        if (selectionDrawingEnabled() && currentSelectedCell.x != -1 && showTypeOfTerritory &&
                !Input.isKeyDown(Key.LeftAlt) && !Input.isKeyDown(Key.RightAlt) &&
                Input.isMousePressed(MouseButton.Left) &&
                Farmland.get().getLoadedSave().getCurrentPlayer().getId().equals(Farmland.get().getLoadedSave().getLocalPlayer().getId()) &&
                Farmland.get().getLoadedSave().getLocalPlayer().getCurrentItemFromInventory() == null &&
                !cellIsOwnedByLocalPlayer(currentSelectedCell.x, currentSelectedCell.y) &&
                cellIsClosedToOwnedCellByLocalPlayer(currentSelectedCell.x, currentSelectedCell.y) &&
                Farmland.get().getLoadedSave().currentPlayerId.equals(Farmland.get().getLoadedSave().localPlayerId)
                && Farmland.get().getLoadedSave().getLocalPlayer().money >= 25) {
            Farmland.get().getLoadedSave().getLocalPlayer().buyCell(currentSelectedCell);
        }

        if (selectionDrawingEnabled() && currentSelectedCell.x != -1 &&
                !Input.isKeyDown(Key.LeftAlt) && !Input.isKeyDown(Key.RightAlt) &&
                Input.isMousePressed(MouseButton.Right) &&
                Farmland.get().getLoadedSave().getCurrentPlayer().getId().equals(Farmland.get().getLoadedSave().getLocalPlayer().getId()) &&
                cellIsOwnedByLocalPlayer(currentSelectedCell.x, currentSelectedCell.y) && getCell(currentSelectedCell).isOwnedByCurrentPlayer() &&
                getCell(currentSelectedCell).hasItem() && getCell(currentSelectedCell).item.shouldBeDestroyed()) {
            Farmland.get().getLoadedSave().getLocalPlayer().harvestSelectedItem(currentSelectedCell);
        }
    }

    public void updateCurrentSelectedCell() {
        for (int x = 0; x < getGridSize().x; x++) {
            for (int y = 0; y < getGridSize().y; y++) {
                Cell cell = getCell(x, y);

                if (Input.isMouseInWorldViewRect(cell.viewRectangle)) {
                    currentSelectedCell = new Vector2i(x, y);
                    return;
                }
            }
        }
    }

    @Override
    public void render(Spritebatch spritebatch, RendererComponent rendererComponent,
                       TransformComponent transformComponent) {
        renderBackground(spritebatch, rendererComponent, transformComponent);
        renderItems(spritebatch, rendererComponent, transformComponent);
        renderCells(spritebatch, rendererComponent, transformComponent);

        if (selectionDrawingEnabled() && currentSelectedCell.x != -1) {
            renderSelectionCursor(spritebatch, rendererComponent, transformComponent);

            if (Farmland.get().getLoadedSave() != null && Farmland.get().getLoadedSave().isLocalPlayerTurn()) {
                renderSelectedItem(spritebatch, rendererComponent, transformComponent);
            }
        }

        if (selectionDrawingEnabled() && showTypeOfTerritory) {
            renderTerritory(spritebatch, rendererComponent, transformComponent);
        }
    }

    public void setGridSize(Vector2i size) {
        this.gridSize = size;
    }

    public void setSelectionCursorEnabled(boolean selectionCursorEnabled) {
        this.selectionCursorEnabled = selectionCursorEnabled;
    }

    public boolean cellIsOwned(int x, int y) {
        return getCell(x, y).isOwned();
    }

    public boolean cellIsOwnedByLocalPlayer(int x, int y) {
        return getCell(x, y).isOwnedByLocalPlayer();
    }

    public boolean cellIsClosedToOwnedCell(int x, int y) {
        return (x < getGridSize().x - 1 && getCell(x + 1, y).isOwned()) ||
                (x > 0 && getCell(x - 1, y).isOwned()) ||
                (y < getGridSize().y - 1 && getCell(x, y + 1).isOwned()) ||
                (y > 0 && getCell(x, y - 1).isOwned());
    }

    public boolean cellIsClosedToOwnedCellByLocalPlayer(int x, int y) {
        return (x < getGridSize().x - 1 && getCell(x + 1, y).isOwnedByLocalPlayer()) ||
                (x > 0 && getCell(x - 1, y).isOwnedByLocalPlayer()) ||
                (y < getGridSize().y - 1 && getCell(x, y + 1).isOwnedByLocalPlayer()) ||
                (y > 0 && getCell(x, y - 1).isOwnedByLocalPlayer());
    }

    private void renderBackground(Spritebatch spritebatch, RendererComponent rendererComponent,
                                  TransformComponent transformComponent) {
        Spritebatch.NineSlicedSpriteData spriteData = new Spritebatch.NineSlicedSpriteData(
                gridBackground,
                new Vector2f(transformComponent.position.x, transformComponent.position.y),
                new Vector2f(getGridSize().x * cellSize.x, getGridSize().y * cellSize.y));
        spriteData.zIndex = rendererComponent.zIndex;

        spritebatch.drawNineSlicedSprite(spriteData);
    }

    private void renderCells(Spritebatch spritebatch, RendererComponent rendererComponent,
                             TransformComponent transformComponent) {
        for (int x = 0; x < getGridSize().x; x++) {
            for (int y = 0; y < getGridSize().y; y++) {
                Spritebatch.SpriteData spriteData = new Spritebatch.SpriteData(
                        getCell(x, y).sprite,
                        new Vector2f(
                                transformComponent.position.x + gridBackgroundSideSize.x + x * cellSize.x,
                                transformComponent.position.y + gridBackgroundSideSize.y + y * cellSize.y));
                spriteData.zIndex = rendererComponent.zIndex + 1;

                spritebatch.drawSprite(spriteData);
            }
        }
    }

    private void renderItems(Spritebatch spritebatch, RendererComponent rendererComponent,
                             TransformComponent transformComponent) {
        for (int x = 0; x < getGridSize().x; x++) {
            for (int y = 0; y < getGridSize().y; y++) {
                Cell cell = getCell(x, y);

                if (cell.hasItem()) {
                    Spritebatch.SpriteData spriteData = new Spritebatch.SpriteData(
                            cell.item.getSprite(),
                            new Vector2f(
                                    transformComponent.position.x + gridBackgroundSideSize.x +
                                            x * cellSize.x,
                                    transformComponent.position.y + gridBackgroundSideSize.y +
                                            y * cellSize.y));
                    spriteData.zIndex = rendererComponent.zIndex + 2;

                    spritebatch.drawSprite(spriteData);
                }
            }
        }
    }

    private void renderSelectionCursor(Spritebatch spritebatch, RendererComponent rendererComponent,
                                       TransformComponent transformComponent) {
        Spritebatch.SpriteData spriteData = new Spritebatch.SpriteData(
                selectionCursor.sprite,
                new Vector2f(
                        transformComponent.position.x + gridBackgroundSideSize.x +
                                currentSelectedCell.x * cellSize.x,
                        transformComponent.position.y + gridBackgroundSideSize.y +
                                currentSelectedCell.y * cellSize.y));
        spriteData.zIndex = rendererComponent.zIndex + 4;

        spritebatch.drawSprite(spriteData);
    }

    private void renderSelectedItem(Spritebatch spritebatch, RendererComponent rendererComponent,
                                    TransformComponent transformComponent) {
        if (Farmland.get().getLoadedSave() != null && Farmland.get().getLoadedSave().getLocalPlayer().selectedItemId != null) {
            Spritebatch.SpriteData spriteData = new Spritebatch.SpriteData(
                    Farmland.get().getItem(Farmland.get().getLoadedSave().getLocalPlayer().selectedItemId).getSprite(),
                    new Vector2f(
                            transformComponent.position.x + gridBackgroundSideSize.x +
                                    currentSelectedCell.x * cellSize.x,
                            transformComponent.position.y + gridBackgroundSideSize.y +
                                    currentSelectedCell.y * cellSize.y));
            spriteData.zIndex = rendererComponent.zIndex + 5;

            if (cellIsOwnedByLocalPlayer(currentSelectedCell.x, currentSelectedCell.y) && !getCell(currentSelectedCell).hasItem() && getCell(currentSelectedCell).isOwnedByCurrentPlayer()) {
                spriteData.tint = Color.GREEN;
            } else {
                spriteData.tint = Color.RED;
            }

            spritebatch.drawSprite(spriteData);
        }
    }

    private void renderTerritory(Spritebatch spritebatch, RendererComponent rendererComponent,
                                 TransformComponent transformComponent) {
        for (int x = 0; x < getGridSize().x; x++) {
            for (int y = 0; y < getGridSize().y; y++) {
                if (cellIsOwned(x, y)) {
                    renderTerritoryCell("owned", x, y, spritebatch, rendererComponent, transformComponent, getCell(x, y).ownerId);
                } else if (cellIsClosedToOwnedCellByLocalPlayer(x, y)) {
                    renderTerritoryCell("notOwned", x, y, spritebatch, rendererComponent, transformComponent, Farmland.get().getLoadedSave().localPlayerId);
                }
            }
        }
    }

    private void renderTerritoryCell(String type, int x, int y, Spritebatch spritebatch,
                                     RendererComponent rendererComponent, TransformComponent transformComponent, int ownderId) {
        if (Farmland.get().getLoadedSave() != null) {
            Spritebatch.SpriteData spriteData = new Spritebatch.SpriteData(
                    territoryTexture.getSprite(type),
                    new Vector2f(
                            transformComponent.position.x + gridBackgroundSideSize.x + x * cellSize.x + 1,
                            transformComponent.position.y + gridBackgroundSideSize.y + y * cellSize.y + 1));
            spriteData.tint = Farmland.get().getLoadedSave().players.get(ownderId).bannerColor;
            spriteData.zIndex = rendererComponent.zIndex + 3;

            spritebatch.drawSprite(spriteData);
        }
    }

    private Cell getCell(int x, int y) {
        return cells == null ? Farmland.get().getLoadedSave().getCell(x, y) : cells.get(x).get(y);
    }

    private Cell getCell(Vector2i position) {
        return cells == null ? Farmland.get().getLoadedSave().getCell(position) : cells.get(position.x).get(position.y);
    }

    public Vector2i getGridSize() {
        return cells == null ? new Vector2i(
                Farmland.get().getLoadedSave().cells.size(),
                Farmland.get().getLoadedSave().cells.isEmpty()
                        ? 0 : Farmland.get().getLoadedSave().cells.get(0).size()) : gridSize;
    }

    private void recalculateCells() {
        SeedRandom random = new SeedRandom();
        TransformComponent transformComponent = getEntity().getComponentSafe(TransformComponent.class);

        if (transformComponent != null) {
            if (Farmland.get().getNetMode() == NetMode.DedicatedServer || Farmland.get().getLoadedSave() == null) {
                cells = new ArrayList<>();

                for (int x = 0; x < getGridSize().x; x++) {
                    this.cells.add(new ArrayList<>());

                    for (int y = 0; y < getGridSize().y; y++) {
                        Vector2f spriteRegion = new Vector2f(
                                cellSize.x * random.generateInRange(1, 120 / cellSize.x),
                                cellSize.y * random.generateInRange(1, 120 / cellSize.y));
                        Sprite sprite = new Sprite(cellBackground,
                                new Vector4f(spriteRegion.x, spriteRegion.y, cellSize.x, cellSize.y));
                        Vector4f viewRectangle = new Vector4f(
                                transformComponent.position.x + gridBackgroundSideSize.x + x * cellSize.x,
                                transformComponent.position.y + gridBackgroundSideSize.y + y * cellSize.y,
                                transformComponent.position.x + gridBackgroundSideSize.x + x * cellSize.x + cellSize.x,
                                transformComponent.position.y + gridBackgroundSideSize.y + y * cellSize.y + cellSize.y);

                        this.cells.get(x).add(new Cell(sprite, viewRectangle));
                    }
                }
            }
        }
    }

    private boolean selectionDrawingEnabled() {
        return selectionCursorEnabled && (!(Game.get().getSceneManager().getCurrentScene() instanceof InGameScene) || !((InGameScene) Game.get().getSceneManager().getCurrentScene()).inPause);
    }
}
