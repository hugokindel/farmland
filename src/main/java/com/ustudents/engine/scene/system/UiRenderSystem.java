package com.ustudents.engine.scene.system;

import com.ustudents.engine.scene.ecs.Entity;
import com.ustudents.engine.scene.component.graphics.UiRendererComponent;
import com.ustudents.engine.graphic.Spritebatch;
import com.ustudents.engine.scene.SceneManager;

public class UiRenderSystem extends RenderSystem {
    public UiRenderSystem() {
        requireComponent(UiRendererComponent.class);
    }

    @Override
    public void render() {
        if (getEntities().size() == 0) {
            return;
        }

        Spritebatch spritebatch = SceneManager.getScene().getSpritebatch();

        spritebatch.begin(SceneManager.getScene().getUiCamera());

        for (Entity entity : getEntities()) {
            if (entity.isEnabled()) {
                renderElement(spritebatch, entity, UiRendererComponent.class);
            }
        }

        spritebatch.end();
    }
}
