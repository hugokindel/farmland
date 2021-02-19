package com.ustudents.engine;

import com.ustudents.engine.audio.SoundManager;
import com.ustudents.engine.core.cli.option.Runnable;
import com.ustudents.engine.core.cli.option.annotation.Command;
import com.ustudents.engine.core.cli.option.annotation.Option;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.core.Resources;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.graphic.Spritebatch;
import com.ustudents.engine.graphic.Texture;
import com.ustudents.engine.graphic.imgui.ImGuiManager;
import com.ustudents.engine.input.Input;
import com.ustudents.engine.input.Key;
import com.ustudents.engine.scene.Scene;
import com.ustudents.engine.scene.SceneManager;
import com.ustudents.engine.core.Timer;
import com.ustudents.engine.core.Window;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL33;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.*;

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

    @Option(names = "--force-no-custom-cursor", description = "Force to use the default operating system cursor.")
    protected boolean forceNoCustomCursor;

    @Option(names = "--force-no-custom-icon", description = "Force to use the default operating system window icon.")
    protected boolean forceNoCustomIcon;

    /** The scene manager (handling every scene). */
    protected final SceneManager sceneManager;

    protected final Window window;

    protected final ImGuiManager imGuiManager;

    private final SoundManager soundManager;

    protected final Timer timer;

    protected boolean shouldClose;

    protected boolean imGuiVisible;

    private static Game game;

    private static String instanceName;

    private boolean shouldResize;

    protected Texture cursorTexture;

    protected boolean showDebugInterface;

    protected boolean debugTexts;

    /** Class constructor. */
    public Game() {
        // Default values for options.
        noAnsiCodes = false;
        isDebugging = false;
        noImGui = false;
        shouldClose = false;
        imGuiVisible = true;
        shouldResize = true;

        // Game managers.
        window = new Window();
        sceneManager = new SceneManager();
        imGuiManager = new ImGuiManager();
        soundManager = new SoundManager();
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
            _initialize(args);
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
    private void _initialize(String[] args) {
        if (isDebugging()) {
            Out.printlnDebug("Initializing...");
        }
        Resources.load();
        String commandName = getClass().getAnnotation(Command.class).name();
        if (!Arrays.asList(args).contains("--vsync")) {
            vsync = (Boolean)Resources.getSetting("vsync");
        }
        window.initialize(
                commandName.substring(0, 1).toUpperCase() + commandName.substring(1),
                new Vector2i(1280, 720),
                vsync
        );
        if (!noImGui) {
            imGuiManager.initialize(window.getHandle(), window.getGlslVersion());
        }

        sceneManager.initialize();
        soundManager.initialize();
        initialize();
        window.show(true);

        if (isImGuiActive()) {
            glfwSetInputMode(getWindow().getHandle(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        } else {
            glfwSetInputMode(getWindow().getHandle(), GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
        }

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

        if (Input.isKeyPressed(Key.F1)) {
            imGuiVisible = !imGuiVisible;

            if (isImGuiActive()) {
                glfwSetInputMode(getWindow().getHandle(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
            } else if (!forceNoCustomCursor && cursorTexture != null) {
                glfwSetInputMode(getWindow().getHandle(), GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
            }
        }

        if (Input.isKeyPressed(Key.F2)) {
            showDebugInterface = !showDebugInterface;
            debugTexts = !debugTexts;
        }

        float dt = timer.getDeltaTime();

        sceneManager.update(dt);
        update(dt);
    }

    private void resizeViewport() {
        Vector2i size = window.getSize();

        GL33.glViewport(0, 0, size.x, size.y);

        if (getSceneManager() != null && getSceneManager().getScene() != null) {
            Scene scene = getSceneManager().getScene();
            scene.getCamera().resize(size.x, size.y);
            scene.getUiCamera().resize(size.x, size.y);
            shouldResize = false;
        }
    }

    /** Renders the game. */
    private void _render() {
        Spritebatch spritebatch = sceneManager.getScene().getSpritebatch();

        timer.render();
        window.clear();

        if (shouldResize) {
            resizeViewport();
        }

        if (!noImGui && imGuiVisible) {
            imGuiManager.startFrame();
        }

        sceneManager.render();
        render();

        if (showDebugInterface) {
            Vector2i windowSize = getWindow().getSize();

            spritebatch.begin(sceneManager.getScene().getUiCamera());
            spritebatch.drawLine(new Vector2f(0, (float)windowSize.y / 2), new Vector2f(windowSize.x, (float)windowSize.y / 2));
            spritebatch.drawLine(new Vector2f((float)windowSize.x / 2, 0), new Vector2f((float)windowSize.x / 2, windowSize.y));
            spritebatch.end();
        }

        if (!noImGui && imGuiVisible) {
            sceneManager.renderImGui();
            imGuiManager.endFrame();
        }

        if (!isImGuiActive() && sceneManager.getScene() != null &&
                cursorTexture != null && Input.getMousePos() != null) {
            spritebatch.begin(sceneManager.getScene().getCursorCamera());
            spritebatch.drawTexture(
                    cursorTexture, Input.getMousePos(),
                    new Vector4f(0, 0, 11, 14),
                    0,
                    Color.WHITE,
                    0,
                    new Vector2f(2, 2)
            );
            spritebatch.end();
        }

        window.swap();
    }

    /** Destroy everything. */
    private void _destroy() {
        destroy();

        if (isDebugging()) {
            Out.printlnDebug("Destroying...");
        }

        window.show(false);
        soundManager.destroy();
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
        Resources.setSetting("vsync", vsync);
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

    public void forceResize() {
        shouldResize = true;
    }

    public void changeCursor(String filePath) {
        if (!forceNoCustomCursor) {
            cursorTexture = Resources.loadTexture(filePath);
        }
    }

    public void changeIcon(String filepath) {
        if (!forceNoCustomIcon) {
            window.changeIcon(filepath);
        }
    }

    public ImGuiManager getImGuiManager() {
        return imGuiManager;
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }

    public boolean isShowDebugInterface() {
        return showDebugInterface;
    }

    public boolean isDebugTexts() {
        return debugTexts;
    }
}
