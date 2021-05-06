package com.ustudents.engine.ecs.system;

import com.ustudents.engine.ecs.Entity;
import com.ustudents.engine.ecs.component.graphic.WorldRendererComponent;
import com.ustudents.engine.graphic.Spritebatch;
import com.ustudents.engine.scene.SceneManager;

public class WorldRenderSystem extends RenderSystem {
    public WorldRenderSystem() {
        requireComponent(WorldRendererComponent.class);
    }

    @Override
    public void render() {
        if (getEntities().size() == 0) {
            return;
        }

        Spritebatch spritebatch = SceneManager.getScene().getSpritebatch();

        spritebatch.begin(SceneManager.getScene().getWorldCamera());

        for (Entity entity : getEntities()) {
            if (entity.isEnabled()) {
                renderElement(spritebatch, entity, WorldRendererComponent.class);
            }
        }

        spritebatch.end();
    }
}
