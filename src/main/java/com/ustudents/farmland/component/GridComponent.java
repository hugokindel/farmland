package com.ustudents.farmland.component;

import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.core.event.EventDispatcher;
import com.ustudents.engine.scene.component.core.BehaviourComponent;
import com.ustudents.engine.scene.component.core.TransformComponent;
import com.ustudents.engine.scene.component.graphics.*;
import com.ustudents.engine.graphic.*;
import com.ustudents.engine.graphic.imgui.annotation.Viewable;
import com.ustudents.engine.input.Input;
import com.ustudents.engine.input.Key;
import com.ustudents.engine.input.MouseButton;
import com.ustudents.engine.utility.SeedRandom;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.core.grid.Cell;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

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

    public EventDispatcher onItemUsed = new EventDispatcher();

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
        this.cells = new ArrayList<>();
        this.currentSelectedCell = new Vector2i(-1, -1);
        this.cellSize = cellSize;
        this.gridBackgroundSideSize = new Vector2i(5, 5);
        this.selectionCursorEnabled = true;
        this.showTypeOfTerritory = false;
    }

    @Override
    public void initialize() {
        SeedRandom random = new SeedRandom();
        TransformComponent transformComponent = getEntity().getComponent(TransformComponent.class);

        if (Farmland.get().getCurrentSave() == null) {
            for (int x = 0; x < gridSize.x; x++) {
                this.cells.add(new ArrayList<>());

                for (int y = 0; y < gridSize.y; y++) {
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
        } else {
            for (int x = 0; x < gridSize.x; x++) {
                this.cells.add(new ArrayList<>());

                for (int y = 0; y < gridSize.y; y++) {
                    this.cells.get(x).add(Farmland.get().getCurrentSave().cells.get(x).get(y));
                }
            }
        }
    }

    @Override
    public void onSceneLoaded() {
        TransformComponent transformComponent = getEntity().getComponent(TransformComponent.class);

        Camera camera = getScene().getWorldCamera();
        camera.setMinimalX(transformComponent.position.x + gridBackgroundSideSize.x);
        camera.setMaximalX(transformComponent.position.x + gridBackgroundSideSize.x + gridSize.x * cellSize.x);
        camera.setMinimalY(transformComponent.position.x + gridBackgroundSideSize.y);
        camera.setMaximalY(transformComponent.position.y + gridBackgroundSideSize.y + gridSize.x * cellSize.y);

        if (Farmland.get().getCurrentSave() == null) {
            camera.centerOnPosition(new Vector2f(
                    transformComponent.position.x + gridBackgroundSideSize.x + (gridSize.x * cellSize.x) / 2.0f,
                    transformComponent.position.y + gridBackgroundSideSize.y + (gridSize.y * cellSize.y) / 2.0f));
        } else {
            if (Farmland.get().getCurrentSave().players.get(0).position != null) {
                camera.centerOnPosition(new Vector2f(
                        Farmland.get().getCurrentSave().players.get(0).position.x,
                        Farmland.get().getCurrentSave().players.get(0).position.y));
            } else {
                camera.centerOnPosition(new Vector2f(
                        Farmland.get().getCurrentSave().players.get(0).village.position.x,
                        Farmland.get().getCurrentSave().players.get(0).village.position.y));
            }
        }

        camera.moved.add((dataType, data) -> {
            Camera.PositionChanged eventData = (Camera.PositionChanged) data;
            if (Farmland.get().getCurrentSave() != null) {
                Farmland.get().getCurrentSave().players.get(0).position = new Vector2f(eventData.position.x, eventData.position.y);
            }
        });
    }

    @Override
    public void update(float dt) {
        selectionCursor.update(dt);

        showTypeOfTerritory = Input.isKeyDown(Key.LeftControl) || Input.isKeyDown(Key.RightControl);

        if (currentSelectedCell.x == -1 || !Input.isMouseInWorldViewRect(
                cells.get(currentSelectedCell.x).get(currentSelectedCell.y).viewRectangle)) {
            updateCurrentSelectedCell();
        }

        if (selectionCursorEnabled && currentSelectedCell.x != -1 &&
                !Input.isKeyDown(Key.LeftAlt) && !Input.isKeyDown(Key.RightAlt) &&
                Input.isMousePressed(MouseButton.Left) &&
                cellIsOwned(currentSelectedCell.x, currentSelectedCell.y) &&
                Farmland.get().getCurrentSave().getCurrentPlayer().selectedItemID != null
                && cells.get(currentSelectedCell.x).get(currentSelectedCell.y).itemId == null) {
            Out.println("add item");
            cells.get(currentSelectedCell.x).get(currentSelectedCell.y).setItem(Farmland.get().getCurrentSave().getCurrentPlayer().selectedItemID);
            if (Farmland.get().getCurrentSave().getCurrentPlayer().deleteFromInventory(Farmland.get().getItemDatabase().get(Farmland.get().getCurrentSave().getCurrentPlayer().selectedItemID))) {
                Farmland.get().getCurrentSave().getCurrentPlayer().selectedItemID = null;
            }
            onItemUsed.dispatch();
        }

        if (selectionCursorEnabled && currentSelectedCell.x != -1 && showTypeOfTerritory &&
                !Input.isKeyDown(Key.LeftAlt) && !Input.isKeyDown(Key.RightAlt) &&
                Input.isMousePressed(MouseButton.Left) &&
                !cellIsOwned(currentSelectedCell.x, currentSelectedCell.y) &&
                cellIsClosedToOwnedCell(currentSelectedCell.x, currentSelectedCell.y) &&
                Farmland.get().getCurrentSave().currentPlayerId == 0) {
            Out.println("set owned");
            cells.get(currentSelectedCell.x).get(currentSelectedCell.y).setOwned(true, 0);
        }
    }

    public void updateCurrentSelectedCell() {
        for (int x = 0; x < gridSize.x; x++) {
            for (int y = 0; y < gridSize.y; y++) {
                Cell cell = cells.get(x).get(y);

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

        if (selectionCursorEnabled && currentSelectedCell.x != -1) {
            renderSelectionCursor(spritebatch, rendererComponent, transformComponent);
        }

        if (selectionCursorEnabled && showTypeOfTerritory) {
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
        return cells.get(x).get(y).isOwned();
    }

    public boolean cellIsClosedToOwnedCell(int x, int y) {
        return (x < gridSize.x - 1 && cells.get(x + 1).get(y).isOwnedByCurrentPlayer()) ||
                (x > 0 && cells.get(x - 1).get(y).isOwnedByCurrentPlayer()) ||
                (y < gridSize.y - 1 && cells.get(x).get(y + 1).isOwnedByCurrentPlayer()) ||
                (y > 0 && cells.get(x).get(y - 1).isOwnedByCurrentPlayer());
    }

    private void renderBackground(Spritebatch spritebatch, RendererComponent rendererComponent,
                                  TransformComponent transformComponent) {
        Spritebatch.NineSlicedSpriteData spriteData = new Spritebatch.NineSlicedSpriteData(
                gridBackground,
                new Vector2f(transformComponent.position.x, transformComponent.position.y),
                new Vector2f(gridSize.x * cellSize.x, gridSize.y * cellSize.y));
        spriteData.zIndex = rendererComponent.zIndex;

        spritebatch.drawNineSlicedSprite(spriteData);
    }

    private void renderCells(Spritebatch spritebatch, RendererComponent rendererComponent,
                             TransformComponent transformComponent) {
        for (int x = 0; x < gridSize.x; x++) {
            for (int y = 0; y < gridSize.y; y++) {
                Spritebatch.SpriteData spriteData = new Spritebatch.SpriteData(
                        cells.get(x).get(y).sprite,
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
        for (int x = 0; x < gridSize.x; x++) {
            for (int y = 0; y < gridSize.y; y++) {
                Cell cell = cells.get(x).get(y);

                if (cell.hasItem()) {
                    Spritebatch.SpriteData spriteData = new Spritebatch.SpriteData(
                            Farmland.get().getItemDatabase().get(cell.itemId).spritesheet.getSprite(cell.itemId + "1"),
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

    private void renderTerritory(Spritebatch spritebatch, RendererComponent rendererComponent,
                                 TransformComponent transformComponent) {
        for (int x = 0; x < gridSize.x; x++) {
            for (int y = 0; y < gridSize.y; y++) {
                if (cellIsOwned(x, y)) {
                    renderTerritoryCell("owned", x, y, spritebatch, rendererComponent, transformComponent, cells.get(x).get(y).ownerId);
                } else if (cellIsClosedToOwnedCell(x, y)) {
                    renderTerritoryCell("notOwned", x, y, spritebatch, rendererComponent, transformComponent, 0);
                }
            }
        }
    }

    private void renderTerritoryCell(String type, int x, int y, Spritebatch spritebatch,
                                     RendererComponent rendererComponent, TransformComponent transformComponent, int ownderId) {
        if (Farmland.get().getCurrentSave() != null) {
            Spritebatch.SpriteData spriteData = new Spritebatch.SpriteData(
                    territoryTexture.getSprite(type),
                    new Vector2f(
                            transformComponent.position.x + gridBackgroundSideSize.x + x * cellSize.x + 1,
                            transformComponent.position.y + gridBackgroundSideSize.y + y * cellSize.y + 1));
            spriteData.tint = Farmland.get().getCurrentSave().players.get(ownderId).color;
            spriteData.zIndex = rendererComponent.zIndex + 3;

            spritebatch.drawSprite(spriteData);
        }
    }
}
