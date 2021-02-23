package com.ustudents.engine;

import com.ustudents.engine.audio.SoundManager;
import com.ustudents.engine.audio.SoundSystemType;
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
import org.lwjgl.opengl.GL33;

import java.util.Arrays;

/** The main class of the project. */
public abstract class Game extends Runnable {
    /** Option to disable ANSI codes. */
    @Option(names = "--no-ansi", description = "Disable ANSI code usage (useful for Windows compatibility).")
    protected boolean noAnsiCodes = false;

    /** Option to enable debugging output. */
    @Option(names = "--debug", description = "Enable debug output.")
    protected boolean isDebugging = false;

    /** Option to disable ImGui. */
    @Option(names = "--no-imgui", description = "Disable ImGui completely (debug graphical interface).")
    protected boolean noImGui = false;

    /** Option to force V-Sync at launch. */
    @Option(names = "--vsync", description = "Enable vertical synchronisation.")
    protected boolean vsync = false;

    /** Option to force to disable any custom cursors. */
    @Option(names = "--no-custom-cursor", description = "Force to use the default operating system cursor.")
    protected boolean forceNoCustomCursor = false;

    /** Option to force to disable any custom window icons. */
    @Option(names = "--no-custom-icon", description = "Force to use the default operating system window icon.")
    protected boolean forceNoCustomIcon = false;

    /** Option to force to disable any custom cursors. */
    @Option(names = "--no-sound", description = "Force to disable any sounds.")
    protected boolean forceNoSound = false;

    /** The window. */
    protected final Window window = new Window();

    /** The scene manager (handling every scene). */
    protected final SceneManager sceneManager = new SceneManager();

    /** The sound manager (handle every sound sources). */
    private final SoundManager soundManager;

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

        soundManager = new SoundManager();
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

    /** Quits the game. */
    public void quit() {
        shouldQuit = true;
    }

    /** Force a resize of the game render viewport and camera matrices before the next frame. */
    public void forceResizeBeforeNextFrame() {
        shouldResize = true;
    }

    /**
     * Changes the game's window cursor.
     *
     * @param filePath The file path.
     */
    public void changeCursor(String filePath) {
        if (!forceNoCustomCursor) {
            cursorTexture = Resources.loadTexture(filePath);
        }
    }

    /**
     * Changes the game's window icon.
     *
     * @param filepath The file path.
     */
    public void changeIcon(String filepath) {
        if (!forceNoCustomIcon) {
            window.changeIcon(filepath);
        }
    }

    /** @return the game. */
    public static Game get() {
        return game;
    }

    /** @return the instance's name. */
    public static String getInstanceName() {
        return instanceName;
    }

    /** @return the window. */
    public Window getWindow() {
        return window;
    }

    /** @return the sound manager. */
    public SoundManager getSoundManager() {
        return soundManager;
    }


    /** @return the scene manager. */
    public SceneManager getSceneManager() {
        return sceneManager;
    }

    /** @return the ImGui manager. */
    public ImGuiManager getImGuiManager() {
        return imGuiManager;
    }

    /** @return the timer. */
    public Timer getTimer() {
        return timer;
    }

    /** @return the debug tools (using ImGui). */
    public ImGuiTools getImGuiTools() {
        return imGuiTools;
    }

    /** @return if ImGui tools are enabled. */
    public boolean isImGuiToolsEnabled() {
        return !noImGui && imGuiToolsEnabled;
    }

    public boolean isImGuiEnabled() {
        return !noImGui;
    }

    /** @return the debug tools. */
    public DebugTools getDebugTools() {
        return debugTools;
    }

    /** @return if the debug tools are enabled. */
    public boolean isDebugToolsEnabled() {
        return debugToolsEnabled;
    }

    /** @return if we are currently in a debugging session. */
    public static boolean isDebugging() {
        return get() != null && get().isDebugging;
    }

    /** @return if the V-Sync is enabled. */
    public boolean getVsync() {
        return vsync;
    }

    /**
     * Changes the V-Sync state.
     *
     * @param vsync The new V-Sync state.
     */
    public void setVsync(boolean vsync) {
        this.vsync = vsync;
        Resources.setSetting("vsync", vsync);
        window.setVsync(vsync);
    }

    public SoundSystemType getSoundSystemType() {
        return forceNoSound ? SoundSystemType.Empty : SoundSystemType.OpenAL;
    }

    /** Initialize the game. */
    protected void initialize() {

    }

    /** Updates the game's logic. */
    protected void update(float dt) {

    }

    /** Renders the game on the screen. */
    protected void render() {

    }

    /** Destroys the game's data. */
    protected void destroy() {

    }

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

        while (!window.shouldQuit() && !shouldQuit) {
            sceneManager.startFrame();
            updateInternal();
            renderInternal();
            sceneManager.endFrame();
            window.pollEvents();
        }
    }

    /** Updates the game logic. */
    private void updateInternal() {
        timer.update();

        if (Input.isKeyPressed(Key.F1)) {
            imGuiToolsEnabled = !imGuiToolsEnabled;
            window.actualizeCursorType();
        }

        if (Input.isKeyPressed(Key.F2)) {
            debugToolsEnabled = !debugToolsEnabled;
        }

        float dt = timer.getDeltaTime();

        sceneManager.update(dt);
        update(dt);
    }

    /** Renders the game. */
    private void renderInternal() {
        Spritebatch spritebatch = sceneManager.getCurrentScene().getSpritebatch();

        timer.render();
        window.clear();

        if (shouldResize) {
            resizeViewportAndCameras();
        }

        if (!noImGui && (imGuiToolsEnabled || SceneManager.getScene().isForceImGuiEnabled())) {
            imGuiManager.startFrame();
        }

        sceneManager.render();
        render();

        if (!noImGui && (imGuiToolsEnabled || SceneManager.getScene().isForceImGuiEnabled())) {
            sceneManager.renderImGui();
        }

        if (!noImGui && imGuiToolsEnabled) {
            imGuiTools.renderImGui();
        }

        if (!noImGui && (imGuiToolsEnabled || SceneManager.getScene().isForceImGuiEnabled())) {
            imGuiManager.endFrame();
        }

        if (noImGui || (!isImGuiToolsEnabled() && !SceneManager.getScene().isForceImGuiEnabled()) && sceneManager.getCurrentScene() != null &&
                cursorTexture != null && Input.getMousePos() != null) {
            spritebatch.begin(sceneManager.getCurrentScene().getCursorCamera());
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

    /** Resizes the render viewport and every camera matrices. */
    private void resizeViewportAndCameras() {
        Vector2i size = window.getSize();

        GL33.glViewport(0, 0, size.x, size.y);

        if (getSceneManager() != null && getSceneManager().getCurrentScene() != null) {
            Scene scene = getSceneManager().getCurrentScene();
            scene.getWorldCamera().resize(size.x, size.y);
            scene.getUiCamera().resize(size.x, size.y);
            shouldResize = false;
        }
    }
}
