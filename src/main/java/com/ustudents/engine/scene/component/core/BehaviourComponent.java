package com.ustudents.engine.scene.component.core;

import com.ustudents.engine.Game;
import com.ustudents.engine.scene.ecs.Component;
import com.ustudents.engine.graphic.Camera;
import com.ustudents.engine.scene.Scene;

public abstract class BehaviourComponent extends Component {
    public void update(float dt) {

    }

    public void render() {

    }

    protected Scene getScene() {
        return Game.get().getSceneManager().getCurrentScene();
    }

    protected Camera getWorldCamera() {
        return Game.get().getSceneManager().getCurrentScene().getWorldCamera();
    }
}
