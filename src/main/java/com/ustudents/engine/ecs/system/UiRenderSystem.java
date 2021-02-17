package com.ustudents.engine.ecs.system;

import com.ustudents.engine.ecs.Entity;
import com.ustudents.engine.ecs.Registry;
import com.ustudents.engine.ecs.component.UiComponent;
import com.ustudents.engine.graphic.Spritebatch;

public class UiRenderSystem extends RenderSystem {
    public UiRenderSystem(Registry registry) {
        super(registry);

        requireComponent(UiComponent.class);
    }

    @Override
    public void render() {
        if (getEntities().size() == 0) {
            return;
        }

        Spritebatch spritebatch = getScene().getSpritebatch();

        spritebatch.begin(getScene().getUiCamera());

        for (Entity entity : getEntities()) {
            renderElement(spritebatch, entity);
        }

        spritebatch.end();
    }
}
