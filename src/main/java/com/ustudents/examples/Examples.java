package com.ustudents.examples;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.cli.option.annotation.Command;
import com.ustudents.examples.scenes.EcsExample1;

/** The main class of the project. */
@Command(name = "examples", version = "1.0.0", description = "Various examples.")
public class Examples extends Game {
    @Override
    protected void initialize() {
        sceneManager.changeScene(EcsExample1.class);
    }

    @Override
    protected void update(float dt) {

    }

    @Override
    protected void render() {

    }

    @Override
    protected void destroy() {

    }
}
