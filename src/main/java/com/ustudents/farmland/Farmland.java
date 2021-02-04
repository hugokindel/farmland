package com.ustudents.farmland;

import com.ustudents.farmland.cli.option.Runnable;
import com.ustudents.farmland.cli.option.annotation.Command;
import com.ustudents.farmland.cli.option.annotation.Option;
import com.ustudents.farmland.cli.print.Out;
import com.ustudents.farmland.cli.print.style.BackgroundColor;
import com.ustudents.farmland.cli.print.style.Style;
import com.ustudents.farmland.cli.print.style.TextColor;
import com.ustudents.farmland.common.Resources;
import com.ustudents.farmland.core.SceneManager;
import com.ustudents.farmland.json.Json;
import com.ustudents.farmland.json.JsonWriter;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/** The main class of the project. */
@Command(name = "farmland", version = "1.0.0", description = "A management game about farming.")
public class Farmland extends Runnable {
    @Option(names = "--no-ansi", description = "Disable ANSI code usage (useful for Windows compatibility).")
    private boolean noAnsiCodes;

    @Option(names = "--debug", description = "Enable debug output.")
    private boolean isDebugging;

    /** The scene manager (handling every scene). */
    private static SceneManager sceneManager;

    /** Class constructor. */
    public Farmland() {
        // Default values for options.
        noAnsiCodes = false;
        isDebugging = false;
    }

    /**
     * Run the core of the project.
     *
     * @param args The arguments.
     * @return the exit code.
     */
    @Override
    public int run(String[] args) {
        Out.start(args, false, false);
        if (!readArguments(args, Farmland.class)) {
            return 1;
        }
        Out.canUseAnsiCode(!noAnsiCodes);

        if (!showHelp && !showVersion) {
            initialize();
            loop();
            destroy();
        }

        Out.end();

        return 0;
    }

    /** Initialize everything. */
    private void initialize() {
        Resources.load();
        sceneManager = new SceneManager();
    }

    /** Starts the game loop. */
    private void loop() {
        int i = 0;

        while (i < 2) {
            processInput();
            update(0.0);
            render();

            sceneManager.updateRegistry();

            i++;
        }
    }

    /** Processes the input. */
    private void processInput() {
        sceneManager.processInput();
    }

    /** Updates the game logic. */
    private void update(double dt) {
        sceneManager.update(dt);
    }

    /** Renders the game. */
    private void render() {
        sceneManager.render();
    }

    /** Destroy everything. */
    private void destroy() {
        sceneManager.destroy();
        Resources.save();
    }

    /** @return the scene manager. */
    public SceneManager getSceneManager() {
        return sceneManager;
    }

    public static Farmland get() {
        return Main.farmland;
    }

    public static boolean isDebugging() {
        return Main.farmland != null && Main.farmland.isDebugging;
    }
}
