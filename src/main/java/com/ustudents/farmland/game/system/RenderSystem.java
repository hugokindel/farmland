package com.ustudents.farmland.game.system;

import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.cli.print.Out;
import com.ustudents.farmland.cli.print.style.Style;
import com.ustudents.farmland.ecs.Entity;
import com.ustudents.farmland.ecs.Registry;
import com.ustudents.farmland.ecs.System;
import com.ustudents.farmland.game.component.SpriteComponent;
import com.ustudents.farmland.game.component.TransformComponent;

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
