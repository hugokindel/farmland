package com.ustudents.farmland.component;

import com.ustudents.engine.scene.component.core.BehaviourComponent;
import com.ustudents.engine.input.Input;
import com.ustudents.engine.input.Key;

public class PlayerMovementComponent extends BehaviourComponent {
    public Float movementSpeed;

    public PlayerMovementComponent(Float movementSpeed) {
        this.movementSpeed = movementSpeed;
    }

    @Override
    public void update(float dt) {
        if (Input.isKeyDown(Key.W) || Input.isKeyDown(Key.Up)) {
            getWorldCamera().moveTop(movementSpeed * dt);
        }

        if (Input.isKeyDown(Key.S) || Input.isKeyDown(Key.Down)) {
            getWorldCamera().moveBottom(movementSpeed * dt);
        }

        if (Input.isKeyDown(Key.A) || Input.isKeyDown(Key.Left)) {
            getWorldCamera().moveLeft(movementSpeed * dt);
        }

        if (Input.isKeyDown(Key.D) || Input.isKeyDown(Key.Right)) {
            getWorldCamera().moveRight(movementSpeed * dt);
        }
    }

    public void setMovementSpeed(Float movementSpeed) {
        this.movementSpeed = movementSpeed;
    }
}
