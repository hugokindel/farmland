package com.ustudents.engine.core;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.event.EventData;
import com.ustudents.engine.core.event.EventDispatcher;
import com.ustudents.engine.input.Input;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.scene.SceneManager;
import com.ustudents.farmland.Farmland;
import imgui.ImGui;
import imgui.ImGuiIO;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.stb.STBImage.stbi_load;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    public class SizeChangedEventData extends EventData {
        public Vector2i size;

        public SizeChangedEventData(Vector2i size) {
            this.size = size;
        }
    }

    public class KeyStateChangedEventData extends EventData {
        public int key;
        public int scancode;
        public int action;
        public int mods;

        public KeyStateChangedEventData(int key, int scancode, int action, int mods) {
            this.key = key;
            this.scancode = scancode;
            this.action = action;
            this.mods = mods;
        }
    }

    public class MouseButtonStateChangedEventData extends EventData {
        public int button;
        public int action;
        public int mods;

        public MouseButtonStateChangedEventData(int button, int action, int mods) {
            this.button = button;
            this.action = action;
            this.mods = mods;
        }
    }

    public class CursorMovedEventData extends EventData {
        public Vector2f position;

        public CursorMovedEventData(Vector2f position) {
            this.position = position;
        }
    }

    public class ScrollMovedEventData extends EventData {
        public Vector2f offets;

        public ScrollMovedEventData(Vector2f offets) {
            this.offets = offets;
        }
    }

    private String name;

    private Vector2i size;

    private long windowHandle;

    private String glslVersion;

    public EventDispatcher sizeChanged = new EventDispatcher(SizeChangedEventData.class);

    public EventDispatcher keyStateChanged = new EventDispatcher(KeyStateChangedEventData.class);

    public EventDispatcher mouseButtonStateChanged = new EventDispatcher(MouseButtonStateChangedEventData.class);

    public EventDispatcher cursorMoved = new EventDispatcher(CursorMovedEventData.class);

    public EventDispatcher scrollMoved = new EventDispatcher(ScrollMovedEventData.class);

    public void initialize(String name, Vector2i size, boolean vsync) {
        this.name = name;
        this.size = size;
        this.glslVersion = "";

        GLFWErrorCallback.createPrint(System.err).set();

        if ( !glfwInit() ) {
            String errorMessage = "Unable to initialize glfw!";
            Out.printlnError(errorMessage);
            throw new IllegalStateException(errorMessage);
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, 0);
        glfwWindowHint(GLFW_RESIZABLE, 1);

        findGlslVersion();

        windowHandle = glfwCreateWindow(size.x, size.y, name, NULL, NULL);

        if (windowHandle == NULL) {
            String errorMessage = "Failed to create the glfw window!";
            Out.printlnError(errorMessage);
            throw new RuntimeException(errorMessage);
        }

        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(windowHandle, pWidth, pHeight);

            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            assert vidmode != null;

            glfwSetWindowPos(
                    windowHandle,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        }

        glfwMakeContextCurrent(windowHandle);
        setVsync(vsync);

        GL.createCapabilities();
        glClearColor(0.7647f, 0.7411f, 0.6901f, 1.0f);

        if (Game.isDebugging()) {
            Out.printlnDebug("OpenGL version: " + glGetString(GL_VERSION));
            Out.printlnDebug("OpenGL vendor: " + glGetString(GL_VENDOR));
            Out.printlnDebug("OpenGL renderer: " + glGetString(GL_RENDERER));
            Out.printlnDebug("OpenGL shading language version: " + glGetString(GL_SHADING_LANGUAGE_VERSION));
        }

        actualizeCursorType();
        setupCallbacks();
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT);
    }

    public void swap() {
        glfwSwapBuffers(windowHandle);
    }

    public void destroy() {
        glfwFreeCallbacks(windowHandle);
        glfwDestroyWindow(windowHandle);
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    public void show(boolean show) {
        if (show) {
            glfwShowWindow(windowHandle);
            glfwFocusWindow(windowHandle);
        } else {
            glfwHideWindow(windowHandle);
        }
    }

    public void actualizeCursorType() {
        if (Game.get().isImGuiToolsEnabled()) {
            glfwSetInputMode(windowHandle, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        } else {
            glfwSetInputMode(windowHandle, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
        }
    }

    public boolean shouldQuit() {
        return glfwWindowShouldClose(windowHandle);
    }

    public void pollEvents() {
        glfwPollEvents();
    }

    public static Window get() {
        return Game.get().getWindow();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Vector2i getSize() {
        return size;
    }

    public void setSize(Vector2i size) {
        this.size = size;
    }

    public long getHandle() {
        return windowHandle;
    }

    public String getGlslVersion() {
        return glslVersion;
    }

    public Vector2i getPosition() {
        IntBuffer x = BufferUtils.createIntBuffer(1);
        IntBuffer y = BufferUtils.createIntBuffer(1);

        glfwGetWindowPos(windowHandle, x, y);

        return new Vector2i(x.get(0), y.get(0));
    }

    public void setVsync(boolean enabled) {
        glfwSwapInterval(enabled ? 1 : 0);
    }

    private void findGlslVersion() {
        glslVersion = "#version 330 core";
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
    }

    public void changeIcon(String filePath) {
        ByteBuffer imageBuffer;
        int width, heigh;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer comp = stack.mallocInt(1);
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);

            imageBuffer = stbi_load(Resources.getTexturesDirectory() + "/" + filePath, w, h, comp, 4);
            if (imageBuffer == null) {
                throw new IllegalStateException("Failed to load window icon.");
            }
            width = w.get();
            heigh = h.get();
        }
        GLFWImage image = GLFWImage.malloc(); GLFWImage.Buffer imagebf = GLFWImage.malloc(1);
        image.set(width, heigh, imageBuffer);
        imagebf.put(0, image);
        glfwSetWindowIcon(windowHandle, imagebf);
    }

    private void resize(Vector2i size) {
        this.size = size;
        Game.get().forceResizeBeforeNextFrame();
    }

    private void setupCallbacks() {
        glfwSetWindowSizeCallback(windowHandle, new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                resize(new Vector2i(width, height));
                sizeChanged.dispatch(new SizeChangedEventData(new Vector2i(width, height)));
            }
        });

        glfwSetKeyCallback(windowHandle, new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                /*if (Farmland.get().isImGuiEnabled() && (Farmland.get().isImGuiToolsEnabled() || SceneManager.getScene().isForceImGuiEnabled())) {
                    final ImGuiIO io = ImGui.getIO();

                    if (io.getWantCaptureKeyboard()) {
                        return;
                    }
                }*/

                keyStateChanged.dispatch(new KeyStateChangedEventData(key, scancode, action, mods));
            }
        });

        glfwSetMouseButtonCallback(windowHandle, new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                if (Farmland.get().isImGuiEnabled() && (Farmland.get().isImGuiToolsEnabled() || SceneManager.getScene().isForceImGuiEnabled())) {
                    final ImGuiIO io = ImGui.getIO();

                    if (io.getWantCaptureMouse()) {
                        return;
                    }
                }

                mouseButtonStateChanged.dispatch(new MouseButtonStateChangedEventData(button, action, mods));
            }
        });

        glfwSetCursorPosCallback(windowHandle, new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                cursorMoved.dispatch(new CursorMovedEventData(new Vector2f((float)xpos, (float)ypos)));
            }
        });

        glfwSetScrollCallback(windowHandle, new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xoffset, double yoffset) {
                if (Farmland.get().isImGuiEnabled() && (Farmland.get().isImGuiToolsEnabled() || SceneManager.getScene().isForceImGuiEnabled())) {
                    final ImGuiIO io = ImGui.getIO();

                    if (io.getWantCaptureMouse()) {
                        return;
                    }
                }

                scrollMoved.dispatch(new ScrollMovedEventData(new Vector2f((float)xoffset, (float)yoffset)));
            }
        });
    }
}
