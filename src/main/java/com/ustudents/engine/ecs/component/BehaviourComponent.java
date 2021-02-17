package com.ustudents.engine.ecs.component;

import com.ustudents.engine.ecs.Component;

public abstract class BehaviourComponent extends Component {
    public abstract void update(float dt);

    public abstract void render();
}
