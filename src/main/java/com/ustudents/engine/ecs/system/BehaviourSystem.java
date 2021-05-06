package com.ustudents.engine.ecs.system;

import com.ustudents.engine.ecs.Entity;
import com.ustudents.engine.ecs.System;
import com.ustudents.engine.ecs.component.core.BehaviourComponent;

public class BehaviourSystem extends System {
    public BehaviourSystem() {
        requireComponent(BehaviourComponent.class);
    }

    @Override
    public void update(float dt) {
        for (Entity entity : getEntities()) {
            for (BehaviourComponent behaviourComponent : entity.getBehaviourComponents()) {
                behaviourComponent.update(dt);
            }
        }
    }

    @Override
    public void render() {
        for (Entity entity : getEntities()) {
            for (BehaviourComponent behaviourComponent : entity.getBehaviourComponents()) {
                behaviourComponent.render();
            }
        }
    }
}
