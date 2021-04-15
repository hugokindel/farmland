package com.ustudents.engine;

import com.ustudents.engine.audio.SoundManager;
import com.ustudents.engine.audio.SoundSystemType;
import com.ustudents.engine.core.cli.option.Runnable;
import com.ustudents.engine.core.cli.option.annotation.Command;
import com.ustudents.engine.core.cli.option.annotation.Option;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.window.WindowSystemType;
import com.ustudents.engine.core.window.glfw.GLFWWindow;
import com.ustudents.engine.graphic.RenderSystemType;
import com.ustudents.engine.graphic.Spritebatch;
import com.ustudents.engine.graphic.Texture;
import com.ustudents.engine.graphic.debug.DebugTools;
import com.ustudents.engine.graphic.imgui.ImGuiManager;
import com.ustudents.engine.graphic.imgui.ImGuiTools;
import com.ustudents.engine.input.Input;
import com.ustudents.engine.input.InputSystemType;
import com.ustudents.engine.input.Key;
import com.ustudents.engine.network.*;
import com.ustudents.engine.network.NetMode;
import com.ustudents.engine.scene.Scene;
import com.ustudents.engine.scene.SceneManager;
import com.ustudents.engine.core.Timer;
import com.ustudents.engine.core.window.Window;
import com.ustudents.farmland.Farmland;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL33;

import java.util.Arrays;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.glfwInit;

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

    /** Option to force to disable any custom cursors. */
    @Option(names = "--no-input", description = "Force to disable any inputs.")
    protected boolean forceNoInput = false;

    /** Option to force to disable any custom cursors. */
    @Option(names = "--no-render", description = "Force to disable any renders.")
    protected boolean forceNoRender = false;

    /** Option to force to disable any custom cursors. */
    @Option(names = "--listen", description = "Launches this instance in listen server mode.")
    protected boolean listenServerEnabled = false;

    /** Option to force to disable any custom cursors. */
    @Option(names = "--server", description = "Launches this instance in dedicated server mode.")
    protected boolean dedicatedServerEnabled = false;

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

    protected boolean enableDocking;

    protected Client client = new Client();

    protected Server server = new Server();

    /** The game instance. */
    private static Game game;

    private boolean forceDisableTools;

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

        Out.start(args, false, true);

        if (!readArguments(args, getClass())) {
            return 1;
        }

        Out.canUseAnsiCode(!noAnsiCodes);

        if (!noImGui && forceNoRender) {
            noImGui = true;
        }

        if (dedicatedServerEnabled) {
            noImGui = true;
            forceNoInput = true;
            forceNoRender = true;
            forceNoSound = true;
        }

        if (!showHelp && !showVersion) {
            if (preLoadCondition(args)) {
                initializeInternals(args);
                startGameLoop();
                destroyInternals();
            }
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

    public boolean shouldQuit() {
        return window.shouldQuit() || shouldQuit;
    }

    /**
     * Changes the V-Sync state.
     *
     * @param vsync The new V-Sync state.
     */
    public void setVsync(boolean vsync) {
        this.vsync = vsync;
        Resources.getConfig().useVsync = vsync;
        window.setVsync(vsync);
    }

    public SoundSystemType getSoundSystemType() {
        return forceNoSound ? SoundSystemType.Empty : SoundSystemType.OpenAL;
    }

    public InputSystemType getInputSystemType() {
        return forceNoInput ? InputSystemType.Empty : InputSystemType.GLFW;
    }

    public RenderSystemType getRenderSystemType() {
        return forceNoRender ? RenderSystemType.Empty : RenderSystemType.OpenGL;
    }

    public WindowSystemType getWindowSystemType() {
        return forceNoRender ? WindowSystemType.Empty : WindowSystemType.GLFW;
    }

    public NetMode getNetMode() {
        return dedicatedServerEnabled ? NetMode.DedicatedServer : (listenServerEnabled ? NetMode.ListenServer : client.isAlive() ? NetMode.Client : NetMode.Standalone);
    }

    public boolean canRender() {
        return !dedicatedServerEnabled && !forceNoRender;
    }

    // if true Dedicated Server, Listen Server, Standalone, false Client
    public boolean hasAuthority() {
        return dedicatedServerEnabled || listenServerEnabled || !client.isAlive();
    }

    // if true Listen Server, Client, Standalone, false Dedicated Server
    public boolean isLocallyControlled() {
        return !dedicatedServerEnabled;
    }

    // if true Client, false Standalone
    public boolean isConnectedToServer() {
        return client.isAlive();
    }

    public void disconnectFromServer() {
        client.stop();
    }

    public void disableTools() {
        forceDisableTools = true;
    }

    protected boolean preLoadCondition(String[] args) {
        return true;
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

    /** Renders ImGui content. */
    protected void renderImGui() {

    }

    /** Destroys the game's data. */
    protected void destroy() {

    }

    public void onServerStarted() {

    }

    public void onServerDestroyed() {

    }

    /** Initialize everything. */
    public void initializeInternals(String[] args) {
        Thread.currentThread().setName("MainThread");

        if (isDebugging()) {
            Out.printlnDebug("Initializing...");
        }

        Resources.loadSettingsAndInitialize();

        if (!Arrays.asList(args).contains("--vsync")) {
            vsync = Resources.getConfig().useVsync;
        }

        String commandName = getClass().getAnnotation(Command.class).name();
        window.initialize(
                commandName.substring(0, 1).toUpperCase() + commandName.substring(1),
                Resources.getConfig().windowSize,
                vsync
        );

        if (getNetMode() == NetMode.DedicatedServer) {
            if (!glfwInit()) {
                String errorMessage = "Unable to initialize glfw!";
                Out.printlnError(errorMessage);
                throw new IllegalStateException(errorMessage);
            }
        }

        Input.initialize();

        soundManager.initialize();

        if (canRender()) {
            Resources.loadDefaultResources();

            if (!noImGui) {
                imGuiManager.initialize(window.getHandle(), ((GLFWWindow)window.getWindow()).getGlslVersion(), enableDocking);
            }

            imGuiTools.initialize(sceneManager);
            debugTools.initialize();
        }

        if (getNetMode() == NetMode.DedicatedServer) {
            server.start();
            onServerStarted();
            Out.println("Waiting for world initialization...");
        }

        initialize();

        if (getNetMode() == NetMode.DedicatedServer) {
            Out.println("World initialized.");
        }

        window.show(true);

        if (isDebugging()) {
            Out.printlnDebug("Initialized.");
        }
    }

    /** Starts the game loop. */
    public void startGameLoop() {
        window.pollEvents();

        while (!shouldQuit()) {
            if (getNetMode() == NetMode.DedicatedServer) {
                long lastTime = System.nanoTime();
                final double ns = 1000000000.0 / 128.0;
                double delta = 0;
                while(!shouldQuit) {
                    long now = System.nanoTime();
                    delta += (now - lastTime) / ns;
                    lastTime = now;
                    while(delta >= 1){
                        sceneManager.startFrame();
                        updateInternal();
                        timer.render();
                        sceneManager.endFrame();
                        window.pollEvents();
                        delta--;
                    }
                }
            } else {
                sceneManager.startFrame();
                updateInternal();
                if (canRender()) {
                    renderInternal();
                }
                sceneManager.endFrame();
                window.pollEvents();
            }
        }
    }

    public static boolean isMainThread() {
        return Thread.currentThread().getName().equals("GameMainThread");
    }

    /** Updates the game logic. */
    private void updateInternal() {
        timer.update();

        if (Input.isKeyPressed(Key.F1)) {
            if (!forceDisableTools) {
                imGuiToolsEnabled = !imGuiToolsEnabled;
                window.actualizeCursorType();
            }
        }

        if (Input.isKeyPressed(Key.F2)) {
            if (!forceDisableTools) {
                debugToolsEnabled = !debugToolsEnabled;
            }
        }

        if (getNetMode() == NetMode.DedicatedServer || getNetMode() == NetMode.ListenServer) {
            if (!getServer().getMessagesToHandleOnMainThread().isEmpty()) {
                Objects.requireNonNull(getServer().getMessagesToHandleOnMainThread().poll()).process();
            }
        } else if (getNetMode() == NetMode.ListenServer || getNetMode() == NetMode.Client) {
            if (!getClient().getMessagesToHandleOnMainThread().isEmpty()) {
                Objects.requireNonNull(getClient().getMessagesToHandleOnMainThread().poll()).process();
            }
        }

        float dt = timer.getDeltaTime();
        sceneManager.update(dt);
        update(dt);
        Input.update(dt);
    }

    /** Renders the game. */
    private void renderInternal() {
        timer.render();
        window.clear();

        window.clearBuffer();

        if (shouldResize) {
            resizeViewportAndCameras();
        }

        if (!noImGui && (imGuiToolsEnabled || SceneManager.getScene() == null || SceneManager.getScene().isForceImGuiEnabled())) {
            imGuiManager.startFrame();
        }

        sceneManager.render();
        render();

        window.swapBuffer();

        if (!noImGui && (imGuiToolsEnabled || (SceneManager.getScene() != null && SceneManager.getScene().isForceImGuiEnabled()))) {
            sceneManager.renderImGui();
        }

        if (!noImGui) {
            renderImGui();
        }

        if (!noImGui && imGuiToolsEnabled) {
            imGuiTools.renderImGui();
        }

        if (!noImGui && (imGuiToolsEnabled || SceneManager.getScene() == null || SceneManager.getScene().isForceImGuiEnabled())) {
            imGuiManager.endFrame();
        }

        if (sceneManager.getCurrentScene() != null) {
            Spritebatch spritebatch = sceneManager.getCurrentScene().getSpritebatch();

            if (noImGui || (!isImGuiToolsEnabled() && !SceneManager.getScene().isForceImGuiEnabled()) && sceneManager.getCurrentScene() != null &&
                    cursorTexture != null && Input.getMousePos() != null) {
                spritebatch.begin(sceneManager.getCurrentScene().getCursorCamera());
                spritebatch.drawTexture(new Spritebatch.TextureData(cursorTexture, Input.getMousePos()) {{
                    scale = new Vector2f(2.0f, 2.0f);
                }});
                spritebatch.end();
            }
        }

        window.swap();
    }

    /** Destroy everything. */
    public void destroyInternals() {
        if (client.isAlive()) {
            disconnectFromServer();
        }

        try {
            if (server.isAlive()) {
                server.stop();
                onServerDestroyed();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        destroy();

        if (isDebugging()) {
            Out.printlnDebug("Destroying...");
        }

        window.show(false);
        soundManager.destroy();
        sceneManager.destroy();

        if (canRender() && !noImGui && imGuiToolsEnabled) {
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

    public Client getClient() {
        return client;
    }

    public Server getServer() {
        return server;
    }
}
