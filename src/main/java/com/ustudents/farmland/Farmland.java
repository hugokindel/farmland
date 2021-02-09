package com.ustudents.farmland;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.cli.option.annotation.Command;
import com.ustudents.farmland.scene.MainMenu;

/** The main class of the project. */
@Command(name = "farmland", version = "1.0.0", description = "A management game about farming.")
public class Farmland extends Game {
    public static Farmland get() {
        return Main.farmland;
    }

    @Override
    protected void initialize() {
        sceneManager.initialize(MainMenu.class);
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
