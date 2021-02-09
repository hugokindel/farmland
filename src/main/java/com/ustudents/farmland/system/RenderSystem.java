package com.ustudents.farmland.system;

import com.ustudents.farmland.Farmland;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.core.cli.print.style.Style;
import com.ustudents.engine.scene.ecs.Entity;
import com.ustudents.engine.scene.ecs.Registry;
import com.ustudents.engine.scene.ecs.System;
import com.ustudents.farmland.component.SpriteComponent;
import com.ustudents.farmland.component.TransformComponent;

public class RenderSystem extends System {
    public RenderSystem(Registry registry) {
        super(registry);

        requireComponent(TransformComponent.class);
        requireComponent(SpriteComponent.class);
    }

    public void render() {
        for (Entity entity : getEntities()) {
            TransformComponent transformComponent = entity.getComponent(TransformComponent.class);
            SpriteComponent spriteComponent = entity.getComponent(SpriteComponent.class);

            if (Farmland.isDebugging()) {
                Out.printlnDebug("Render entity " + Style.Bold + entity.getId() + Style.Reset + ": ");
                Out.printlnDebug(" - " + transformComponent);
                Out.printlnDebug(" - " + spriteComponent);
            }
        }
    }
}
