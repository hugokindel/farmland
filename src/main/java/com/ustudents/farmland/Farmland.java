package com.ustudents.farmland;

import com.ustudents.farmland.cli.option.Runnable;
import com.ustudents.farmland.cli.option.annotation.Command;
import com.ustudents.farmland.cli.option.annotation.Option;
import com.ustudents.farmland.cli.print.Out;
import com.ustudents.farmland.common.Resources;
import com.ustudents.farmland.core.ImGuiManager;
import com.ustudents.farmland.core.SceneManager;
import com.ustudents.farmland.core.Timer;
import com.ustudents.farmland.core.Window;
import com.ustudents.farmland.game.scene.ExampleScene;
import org.joml.Vector2i;

import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

/** The main class of the project. */
@Command(name = "farmland", version = "1.0.0", description = "A management game about farming.")
public class Farmland extends Runnable {
    @Option(names = "--no-ansi", description = "Disable ANSI code usage (useful for Windows compatibility).")
    private boolean noAnsiCodes;

    @Option(names = "--debug", description = "Enable debug output.")
    private boolean isDebugging;

    @Option(names = "--no-imgui", description = "Disable ImGui completely (debug graphical interface).")
    private boolean noImGui;

    @Option(names = "--vsync", description = "Enable vertical synchronisation.")
    private boolean vsync;

    /** The scene manager (handling every scene). */
    private final SceneManager sceneManager;

    private final Window window;

    private final ImGuiManager imGuiManager;

    private final Timer timer;

    /** Class constructor. */
    public Farmland() {
        // Default values for options.
        noAnsiCodes = false;
        isDebugging = false;
        noImGui = false;

        // Game managers.
        window = new Window();
        sceneManager = new SceneManager();
        imGuiManager = new ImGuiManager();
        timer = new Timer();
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

        window.initialize("Farmland", new Vector2i(1280, 720), vsync);
        if (!noImGui) {
            imGuiManager.initialize(window.getHandle(), window.getGlslVersion());
        }
        sceneManager.initialize(ExampleScene.class);

        window.show(true);
    }

    /** Starts the game loop. */
    private void loop() {
        while (!window.shouldClose()) {
            processInput();
            update();
            render();

            sceneManager.updateRegistry();
            window.pollEvents();
        }
    }

    /** Processes the input. */
    private void processInput() {
        sceneManager.processInput();
    }

    /** Updates the game logic. */
    private void update() {
        timer.update();
        Out.println(timer.getDeltaTime());
        sceneManager.update(timer.getDeltaTime());
    }

    /** Renders the game. */
    private void render() {
        timer.render();
        window.clear();
        if (!noImGui) {
            imGuiManager.startFrame();
        }
        sceneManager.render();
        if (!noImGui) {
            sceneManager.renderImGui();
            imGuiManager.endFrame();
        }
        window.swap();
    }

    /** Destroy everything. */
    private void destroy() {
        window.show(false);

        sceneManager.destroy();
        if (!noImGui) {
            imGuiManager.destroy();
        }
        window.destroy();

        Resources.save();
    }

    /** @return the scene manager. */
    public SceneManager getSceneManager() {
        return sceneManager;
    }

    public Window getWindow() {
        return window;
    }

    public Timer getTimer() {
        return timer;
    }

    public static Farmland get() {
        return Main.farmland;
    }

    public static boolean isDebugging() {
        return Main.farmland != null && Main.farmland.isDebugging;
    }

    public boolean getVsync() {
        return vsync;
    }

    public void setVsync(boolean vsync) {
        this.vsync = vsync;
        window.setVsync(vsync);
    }
}
