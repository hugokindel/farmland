package com.ustudents.farmland.component.examples;

import com.ustudents.engine.ecs.component.core.BehaviourComponent;
import com.ustudents.engine.ecs.component.core.TransformComponent;
import com.ustudents.engine.input.Input;
import org.lwjgl.glfw.GLFW;

public class RotateBlockComponent extends BehaviourComponent {
    boolean canRotate;

    public RotateBlockComponent() {
        canRotate = true;
    }

    @Override
    public void update(float dt) {
        if (Input.isKeyPressed(GLFW.GLFW_KEY_P)) {
            canRotate = !canRotate;
        }

        if (canRotate) {
            getEntity().getComponent(TransformComponent.class).rotation += 5.0f * dt;
        }
    }

    @Override
    public void render() {

    }
}
