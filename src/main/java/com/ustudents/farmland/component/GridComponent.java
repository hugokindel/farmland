package com.ustudents.farmland.component;

import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.ecs.component.core.BehaviourComponent;
import com.ustudents.engine.ecs.component.core.TransformComponent;
import com.ustudents.engine.ecs.component.graphics.*;
import com.ustudents.engine.graphic.*;
import com.ustudents.engine.graphic.imgui.annotation.Viewable;
import com.ustudents.engine.input.Input;
import com.ustudents.engine.scene.SceneManager;
import com.ustudents.engine.utility.SeedRandom;
import com.ustudents.farmland.core.Cell;
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
    public List<List<Cell>> cells;

    private Vector2i currentSelectedCell;

    private Vector2i cellSize;

    private Vector2i gridBackgroundSideSize;

    public GridComponent(Vector2i gridSize, Vector2i cellSize, NineSlicedSprite gridBackground, Texture cellBackground,
                         AnimatedSprite selectionCursor) {
        this.gridSize = gridSize;
        this.gridBackground = gridBackground;
        this.cellBackground = cellBackground;
        this.selectionCursor = selectionCursor;
        this.currentSelectedCell = new Vector2i(-1, -1);
        this.cellSize = cellSize;
        this.gridBackgroundSideSize = new Vector2i(
                (int)gridBackground.topLeft.getRegion().z, (int)gridBackground.topLeft.getRegion().w);
        this.cells = new ArrayList<>();
    }

    @Override
    public void initialize() {
        SeedRandom random = new SeedRandom();
        TransformComponent transformComponent = getEntity().getComponent(TransformComponent.class);

        for (int x = 0; x < gridSize.x; x++) {
            this.cells.add(new ArrayList<>());

            for (int y = 0; y < gridSize.y; y++) {
                Vector2f spriteRegion = new Vector2f(
                        cellSize.x * random.generateInRange(1, cellBackground.getWidth() / cellSize.x),
                        cellSize.y * random.generateInRange(1, cellBackground.getHeight() / cellSize.y));
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

        Camera camera = getScene().getCamera();
        camera.setMinimalX(transformComponent.position.x + gridBackgroundSideSize.x);
        camera.setMaximalX(transformComponent.position.x + gridBackgroundSideSize.x + gridSize.x * cellSize.x);
        camera.setMinimalY(transformComponent.position.x + gridBackgroundSideSize.y);
        camera.setMaximalY(transformComponent.position.y + gridBackgroundSideSize.y + gridSize.x * cellSize.y);
        camera.centerOnPosition(new Vector2f(
                transformComponent.position.x + gridBackgroundSideSize.x + (gridSize.x * cellSize.x) / 2.0f,
                transformComponent.position.y + gridBackgroundSideSize.y + (gridSize.y * cellSize.y) / 2.0f));
    }

    @Override
    public void update(float dt) {
        selectionCursor.update(dt);

        if (currentSelectedCell.x == -1 || !Input.isMouseInWorldViewRect(
                cells.get(currentSelectedCell.x).get(currentSelectedCell.y).viewRectangle)) {
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
    }

    @Override
    public void render(Spritebatch spritebatch, RendererComponent rendererComponent,
                       TransformComponent transformComponent) {
        renderBackground(spritebatch, rendererComponent, transformComponent);
        renderCells(spritebatch, rendererComponent, transformComponent);

        if (currentSelectedCell.x != -1) {
            renderSelectionCursor(spritebatch, rendererComponent, transformComponent);
        }
    }

    public void setGridSize(Vector2i size) {
        this.gridSize = size;
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

    private void renderSelectionCursor(Spritebatch spritebatch, RendererComponent rendererComponent,
                                       TransformComponent transformComponent) {
        Spritebatch.SpriteData spriteData = new Spritebatch.SpriteData(
                selectionCursor.sprite,
                new Vector2f(
                        transformComponent.position.x + gridBackgroundSideSize.x +
                                currentSelectedCell.x * cellSize.x,
                        transformComponent.position.y + gridBackgroundSideSize.y +
                                currentSelectedCell.y * cellSize.y));
        spriteData.zIndex = rendererComponent.zIndex + 2;

        spritebatch.drawSprite(spriteData);
    }
}
