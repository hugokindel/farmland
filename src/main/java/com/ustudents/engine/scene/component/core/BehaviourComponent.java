package com.ustudents.engine.scene.component.core;

import com.ustudents.engine.scene.ecs.Component;
import com.ustudents.engine.graphic.Camera;
import com.ustudents.engine.scene.Scene;
import com.ustudents.farmland.Farmland;

public abstract class BehaviourComponent extends Component {
    public void update(float dt) {

    }

    public void render() {

    }

    protected Scene getScene() {
        return Farmland.get().getSceneManager().getCurrentScene();
    }

    protected Camera getWorldCamera() {
        return Farmland.get().getSceneManager().getCurrentScene().getWorldCamera();
    }
}
