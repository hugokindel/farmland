package com.ustudents.engine.ecs.system;

import com.ustudents.engine.ecs.Component;
import com.ustudents.engine.ecs.Entity;
import com.ustudents.engine.ecs.Registry;
import com.ustudents.engine.ecs.System;
import com.ustudents.engine.ecs.component.core.BehaviourComponent;

public class BehaviourSystem extends System {
    public BehaviourSystem(Registry registry) {
        super(registry);

        requireComponent(BehaviourComponent.class);
    }

    @Override
    public void update(float dt) {
        for (Entity entity : getEntities()) {
            for (Component component : entity.getComponents()) {
                if (component instanceof BehaviourComponent) {
                    ((BehaviourComponent)component).update(dt);
                }
            }
        }
    }

    @Override
    public void render() {
        for (Entity entity : getEntities()) {
            for (Component component : entity.getComponents()) {
                if (component instanceof BehaviourComponent) {
                    ((BehaviourComponent)component).render();
                }
            }
        }
    }
}
