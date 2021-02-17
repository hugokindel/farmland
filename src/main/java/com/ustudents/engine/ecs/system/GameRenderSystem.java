package com.ustudents.engine.ecs.system;

import com.ustudents.engine.ecs.Entity;
import com.ustudents.engine.ecs.Registry;
import com.ustudents.engine.ecs.component.*;
import com.ustudents.engine.graphic.Spritebatch;

public class GameRenderSystem extends RenderSystem {
    public GameRenderSystem(Registry registry) {
        super(registry);
    }

    @Override
    public void render() {
        if (getEntities().size() == 0) {
            return;
        }

        Spritebatch spritebatch = getScene().getSpritebatch();

        spritebatch.begin(getScene().getCamera());

        for (Entity entity : getEntities()) {
            if (entity.hasComponent(UiComponent.class)) {
                continue;
            }

            renderElement(spritebatch, entity);
        }

        spritebatch.end();
    }
}
