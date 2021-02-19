package com.ustudents.engine;

import com.ustudents.engine.audio.SoundManager;
import com.ustudents.engine.core.cli.option.Runnable;
import com.ustudents.engine.core.cli.option.annotation.Command;
import com.ustudents.engine.core.cli.option.annotation.Option;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.core.Resources;
import com.ustudents.engine.graphic.Spritebatch;
import com.ustudents.engine.graphic.Texture;
import com.ustudents.engine.graphic.debug.DebugTools;
import com.ustudents.engine.graphic.imgui.ImGuiManager;
import com.ustudents.engine.graphic.imgui.ImGuiTools;
import com.ustudents.engine.input.Input;
import com.ustudents.engine.input.Key;
import com.ustudents.engine.scene.Scene;
import com.ustudents.engine.scene.SceneManager;
import com.ustudents.engine.core.Timer;
import com.ustudents.engine.core.Window;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL33;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.*;

/** The main class of the project. */
public abstract class Game extends Runnable {
    /** Option to disable ANSI codes. */
    @Option(names = "--no-ansi", description = "Disable ANSI code usage (useful for Windows compatibility).")
    protected boolean noAnsiCodes = false;

    /** Option to enable debugging output. */
    @Option(names = "--debug", description = "Enable debug output.")
    protected boolean isDebugging = false;

    /** Option to disable ImGui. */
    @Option(names = "--force-no-imgui", description = "Disable ImGui completely (debug graphical interface).")
    protected boolean noImGui = false;

    /** Option to force V-Sync at launch. */
    @Option(names = "--force-vsync", description = "Enable vertical synchronisation.")
    protected boolean vsync = false;

    /** Option to force to disable any custom cursors. */
    @Option(names = "--force-no-custom-cursor", description = "Force to use the default operating system cursor.")
    protected boolean forceNoCustomCursor = false;

    /** Option to force to disable any custom window icons. */
    @Option(names = "--force-no-custom-icon", description = "Force to use the default operating system window icon.")
    protected boolean forceNoCustomIcon = false;

    /** The window. */
    protected final Window window = new Window();

    /** The scene manager (handling every scene). */
    protected final SceneManager sceneManager = new SceneManager();

    /** The sound manager (handle every sound sources). */
    private final SoundManager soundManager = new SoundManager();

    /** The ImGui manager (handle most debugging tools). */
    protected final ImGuiManager imGuiManager = new ImGuiManager();

    /** The timer (handle delta time management). */
    protected final Timer timer = new Timer();

    /** Defines if we should quit the game. */
    protected boolean shouldQuit = false;

    /** Defines if we should resize at the next frame. */
    protected boolean shouldResize = true;

    /** Defines if ImGui should be enabled (can be overridden by noImGui). */
    protected boolean imGuiToolsEnabled = false;

    /** Debugging tools using ImGui. */
    protected final ImGuiTools imGuiTools = new ImGuiTools();

    /** Defines if debug tools should be enabled. */
    protected boolean debugToolsEnabled = false;

    /** Debugging tools. */
    protected final DebugTools debugTools = new DebugTools();

    /** The name of the game instance. */
    protected static String instanceName = "game";

    /** Custom cursor texture. */
    protected Texture cursorTexture;

    /** The game instance. */
    private static Game game;

    /** Class constructor. */
    public Game() {
        game = this;
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
            initializeInternals(args);
            startGameLoop();
            destroyInternals();
        }

        Out.end();

        return 0;
    }

    /** Initialize the game. */
    protected abstract void initialize();

    /** Updates the game's logic. */
    protected abstract void update(float dt);

    /** Renders the game on the screen. */
    protected abstract void render();

    /** Destroys the game's data. */
    protected abstract void destroy();

    /** Initialize everything. */
    private void initializeInternals(String[] args) {
        if (isDebugging()) {
            Out.printlnDebug("Initializing...");
        }

        Resources.loadSettingsAndInitialize();

        if (!Arrays.asList(args).contains("--vsync")) {
            vsync = (Boolean)Resources.getSetting("vsync");
        }

        String commandName = getClass().getAnnotation(Command.class).name();
        window.initialize(
                commandName.substring(0, 1).toUpperCase() + commandName.substring(1),
                new Vector2i(1280, 720),
                vsync
        );

        Resources.loadDefaultResources();

        if (!noImGui) {
            imGuiManager.initialize(window.getHandle(), window.getGlslVersion());
        }

        soundManager.initialize();
        imGuiTools.initialize(sceneManager);
        debugTools.initialize();
        initialize();

        window.show(true);

        if (isDebugging()) {
            Out.printlnDebug("Initialized.");
        }
    }

    /** Starts the game loop. */
    private void startGameLoop() {
        window.pollEvents();

        while (!window.shouldClose() && !shouldQuit) {
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
            imGuiToolsEnabled = !imGuiToolsEnabled;

            if (isImGuiToolsEnabled()) {
                glfwSetInputMode(getWindow().getHandle(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
            } else if (!forceNoCustomCursor && cursorTexture != null) {
                glfwSetInputMode(getWindow().getHandle(), GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
            }
        }

        if (Input.isKeyPressed(Key.F2)) {
            debugToolsEnabled = !debugToolsEnabled;
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

        if (!noImGui && imGuiToolsEnabled) {
            imGuiManager.startFrame();
        }

        sceneManager.render();
        render();

        if (!noImGui && imGuiToolsEnabled) {
            sceneManager.renderImGui();
            imGuiManager.endFrame();
        }

        if (!isImGuiToolsEnabled() && sceneManager.getScene() != null &&
                cursorTexture != null && Input.getMousePos() != null) {
            spritebatch.begin(sceneManager.getScene().getCursorCamera());
            spritebatch.drawTexture(new Spritebatch.TextureData(cursorTexture, Input.getMousePos()) {{
                scale = new Vector2f(2.0f, 2.0f);
            }});
            spritebatch.end();
        }

        window.swap();
    }

    /** Destroy everything. */
    private void destroyInternals() {
        destroy();

        if (isDebugging()) {
            Out.printlnDebug("Destroying...");
        }

        window.show(false);
        soundManager.destroy();
        sceneManager.destroy();

        if (!noImGui && imGuiToolsEnabled) {
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

    public void quit() {
        shouldQuit = true;
    }

    public static Game get() {
        return game;
    }

    public static String getInstanceName() {
        return instanceName;
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

    /** @return the debug tools (using ImGui). */
    public ImGuiTools getImGuiTools() {
        return imGuiTools;
    }

    /** @return if ImGui tools are enabled. */
    public boolean isImGuiToolsEnabled() {
        return !noImGui && imGuiToolsEnabled;
    }

    /** @return the debug tools. */
    public DebugTools getDebugTools() {
        return debugTools;
    }

    public boolean isDebugToolsEnabled() {
        return debugToolsEnabled;
    }
}
