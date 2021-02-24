package com.ustudents.engine.core.window.glfw;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.core.event.EventDispatcher;
import com.ustudents.engine.core.window.empty.EmptyWindow;
import com.ustudents.engine.core.window.events.*;
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
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL20.GL_SHADING_LANGUAGE_VERSION;
import static org.lwjgl.stb.STBImage.stbi_load;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GLFWWindow extends EmptyWindow {
    private String name;

    private Vector2i size;

    private long windowHandle;

    private String glslVersion;

    @Override
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

        setGlslVersion();

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

    @Override
    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void swap() {
        glfwSwapBuffers(windowHandle);
    }

    @Override
    public void destroy() {
        glfwFreeCallbacks(windowHandle);
        glfwDestroyWindow(windowHandle);
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    @Override
    public void show(boolean show) {
        if (show) {
            glfwShowWindow(windowHandle);
            glfwFocusWindow(windowHandle);
        } else {
            glfwHideWindow(windowHandle);
        }
    }

    @Override
    public void actualizeCursorType() {
        if (Game.get().isImGuiToolsEnabled()) {
            glfwSetInputMode(windowHandle, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        } else {
            glfwSetInputMode(windowHandle, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
        }
    }

    @Override
    public boolean shouldQuit() {
        return glfwWindowShouldClose(windowHandle);
    }

    @Override
    public void pollEvents() {
        glfwPollEvents();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Vector2i getSize() {
        return size;
    }

    @Override
    public void setSize(Vector2i size) {
        this.size = size;
    }

    @Override
    public long getHandle() {
        return windowHandle;
    }

    @Override
    public Vector2i getPosition() {
        IntBuffer x = BufferUtils.createIntBuffer(1);
        IntBuffer y = BufferUtils.createIntBuffer(1);

        glfwGetWindowPos(windowHandle, x, y);

        return new Vector2i(x.get(0), y.get(0));
    }

    public void setVsync(boolean enabled) {
        glfwSwapInterval(enabled ? 1 : 0);
    }

    @Override
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

    public String getGlslVersion() {
        return glslVersion;
    }

    private void setGlslVersion() {
        glslVersion = "#version 330 core";
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
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
                sizeChanged.dispatch(new SizeChangedEvent(new Vector2i(width, height)));
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

                keyStateChanged.dispatch(new KeyStateChangedEvent(key, scancode, action, mods));
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

                mouseButtonStateChanged.dispatch(new MouseButtonStateChangedEvent(button, action, mods));
            }
        });

        glfwSetCursorPosCallback(windowHandle, new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                cursorMoved.dispatch(new CursorMovedEvent(new Vector2f((float)xpos, (float)ypos)));
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

                scrollMoved.dispatch(new ScrollMovedEvent(new Vector2f((float)xoffset, (float)yoffset)));
            }
        });
    }
}