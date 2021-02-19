package com.ustudents.engine.ecs.component;

import com.ustudents.engine.ecs.Component;
import com.ustudents.engine.graphic.Camera;
import com.ustudents.farmland.Farmland;

public abstract class BehaviourComponent extends Component {
    public abstract void update(float dt);

    public abstract void render();

    public Camera getCamera() {
        if (entity.hasComponent(UiComponent.class)) {
            return Farmland.get().getSceneManager().getScene().getUiCamera();
        }

        return Farmland.get().getSceneManager().getScene().getCamera();
    }
}
