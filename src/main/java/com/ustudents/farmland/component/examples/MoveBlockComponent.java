package com.ustudents.farmland.component.examples;

import com.ustudents.engine.ecs.component.core.BehaviourComponent;
import com.ustudents.engine.ecs.component.core.TransformComponent;
import com.ustudents.engine.input.Input;
import org.lwjgl.glfw.GLFW;

public class MoveBlockComponent extends BehaviourComponent {
    boolean canMove;

    public MoveBlockComponent() {
        canMove = true;
    }

    @Override
    public void update(float dt) {
        if (Input.isKeyPressed(GLFW.GLFW_KEY_ENTER)) {
            canMove = !canMove;
        }

        if (canMove) {
            getEntity().getComponent(TransformComponent.class).position.x += 100 * dt;
        }
    }

    @Override
    public void render() {

    }
}
