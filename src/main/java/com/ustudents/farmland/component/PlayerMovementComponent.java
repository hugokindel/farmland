package com.ustudents.farmland.component;

import com.ustudents.engine.Game;
import com.ustudents.engine.ecs.component.core.BehaviourComponent;
import com.ustudents.engine.input.Input;
import com.ustudents.engine.input.Key;
import com.ustudents.farmland.scene.InGameScene;

public class PlayerMovementComponent extends BehaviourComponent {
    public Float movementSpeed;

    public PlayerMovementComponent(Float movementSpeed) {
        this.movementSpeed = movementSpeed;
    }

    @Override
    public void update(float dt) {
        if (!((InGameScene) Game.get().getSceneManager().getCurrentScene()).inPause) {
            //if (Input.isKeyDown(Key.W) || Input.isKeyDown(Key.Up)) {
            if (Input.isActionSuccessful("goUp")) {
                getWorldCamera().moveTop(movementSpeed * dt);
            }

            //if (Input.isKeyDown(Key.S) || Input.isKeyDown(Key.Down)) {
            if (Input.isActionSuccessful("goDown")) {
                getWorldCamera().moveBottom(movementSpeed * dt);
            }

            //if (Input.isKeyDown(Key.A) || Input.isKeyDown(Key.Left)) {
            if (Input.isActionSuccessful("goLeft")) {
                getWorldCamera().moveLeft(movementSpeed * dt);
            }

            //if (Input.isKeyDown(Key.D) || Input.isKeyDown(Key.Right)) {
            if (Input.isActionSuccessful("goRight")) {
                getWorldCamera().moveRight(movementSpeed * dt);
            }
        }
    }

    public void setMovementSpeed(Float movementSpeed) {
        this.movementSpeed = movementSpeed;
    }
}
