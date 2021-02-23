package com.ustudents.engine.scene.system;

import com.ustudents.engine.scene.ecs.Entity;
import com.ustudents.engine.scene.ecs.System;
import com.ustudents.engine.scene.component.core.BehaviourComponent;

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
