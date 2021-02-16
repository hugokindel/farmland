package com.ustudents.engine;

import com.ustudents.engine.core.cli.option.Runnable;
import com.ustudents.engine.core.cli.option.annotation.Command;
import com.ustudents.engine.core.cli.option.annotation.Option;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.json.Json;
import com.ustudents.engine.core.json.JsonReader;
import com.ustudents.engine.core.json.JsonWriter;
import com.ustudents.engine.ecs.component.TransformComponent;
import com.ustudents.engine.graphic.imgui.ImGuiManager;
import com.ustudents.engine.input.Input;
import com.ustudents.engine.scene.SceneManager;
import com.ustudents.engine.core.Timer;
import com.ustudents.engine.core.Window;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

/** The main class of the project. */
public abstract class Game extends Runnable {
    @Option(names = "--no-ansi", description = "Disable ANSI code usage (useful for Windows compatibility).")
    protected boolean noAnsiCodes;

    @Option(names = "--debug", description = "Enable debug output.")
    protected boolean isDebugging;

    @Option(names = "--no-imgui", description = "Disable ImGui completely (debug graphical interface).")
    protected boolean noImGui;

    @Option(names = "--vsync", description = "Enable vertical synchronisation.")
    protected boolean vsync;

    /** The scene manager (handling every scene). */
    protected final SceneManager sceneManager;

    protected final Window window;

    protected final ImGuiManager imGuiManager;

    protected final Timer timer;

    protected boolean shouldClose;

    protected boolean imGuiVisible;

    private static Game game;

    private static String instanceName;

    /** Class constructor. */
    public Game() {
        // Default values for options.
        noAnsiCodes = false;
        isDebugging = false;
        noImGui = false;
        shouldClose = false;
        imGuiVisible = true;

        // Game managers.
        window = new Window();
        sceneManager = new SceneManager();
        imGuiManager = new ImGuiManager();
        timer = new Timer();
        game = this;
        instanceName = "game";
    }

    /**
     * Run the core of the project.
     *
     * @param args The arguments.
     * @return the exit code.
     */
    @Override
    public int run(String[] args) {
        if (getClass().getAnnotation(Command.class) != null) {
            instanceName = getClass().getAnnotation(Command.class).name();
        }
        Out.start(args, false, false);
        if (!readArguments(args, getClass())) {
            return 1;
        }
        Out.canUseAnsiCode(!noAnsiCodes);

        if (!showHelp && !showVersion) {
            Map<String, Object> test = new HashMap<>();

            _initialize();
            loop();
            _destroy();
        }

        Out.end();

        return 0;
    }

    protected abstract void initialize();

    protected abstract void update(float dt);

    protected abstract void render();

    protected abstract void destroy();

    /** Initialize everything. */
    private void _initialize() {
        if (isDebugging()) {
            Out.printlnDebug("Initializing...");
        }

        Resources.load();
        String commandName = getClass().getAnnotation(Command.class).name();
        window.initialize(
                commandName.substring(0, 1).toUpperCase() + commandName.substring(1),
                new Vector2i(1280, 720),
                vsync,
                "logo.png"
        );
        if (!noImGui) {
            imGuiManager.initialize(window.getHandle(), window.getGlslVersion());
        }
        sceneManager.initialize();
        initialize();
        window.show(true);

        if (isDebugging()) {
            Out.printlnDebug("Initialized.");
        }
    }

    /** Starts the game loop. */
    private void loop() {
        window.pollEvents();

        while (!window.shouldClose() && !shouldClose) {
            if (Input.isKeyDown(GLFW.GLFW_KEY_ESCAPE)) {
                break;
            }

            sceneManager.startFrame();
            _update();
            _render();
            sceneManager.endFrame();
            window.pollEvents();
        }
    }

    /** Updates the game logic. */
    private void _update() {
        timer.update();
        if (Input.isKeyPressed(GLFW.GLFW_KEY_F1)) {
            imGuiVisible = !imGuiVisible;
        }
        float dt = (float)timer.getDeltaTime();
        sceneManager.update(dt);
        update(dt);
    }

    /** Renders the game. */
    private void _render() {
        timer.render();
        window.clear();
        if (!noImGui && imGuiVisible) {
            imGuiManager.startFrame();
        }
        sceneManager.render();
        render();
        if (!noImGui && imGuiVisible) {
            sceneManager.renderImGui();
            imGuiManager.endFrame();
        }
        window.swap();
    }

    /** Destroy everything. */
    private void _destroy() {
        Json.serialize("scene.json", sceneManager.getScene());

        destroy();
        if (isDebugging()) {
            Out.printlnDebug("Destroying...");
        }

        window.show(false);
        sceneManager.destroy();
        if (!noImGui && imGuiVisible) {
            imGuiManager.destroy();
        }
        window.destroy();

        Resources.saveAndUnload();

        if (isDebugging()) {
            Out.printlnDebug("Destroyed.");
        }
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

    public static boolean isDebugging() {
        return get() != null && get().isDebugging;
    }

    public boolean getVsync() {
        return vsync;
    }

    public void setVsync(boolean vsync) {
        this.vsync = vsync;
        window.setVsync(vsync);
    }

    public void close() {
        shouldClose = true;
    }

    public static Game get() {
        return game;
    }

    public static String getInstanceName() {
        return instanceName;
    }

    public boolean isImGuiActive() {
        return !noImGui && imGuiVisible;
    }
}
