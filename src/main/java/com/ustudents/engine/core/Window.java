package com.ustudents.engine.core;

import com.ustudents.engine.Game;
import com.ustudents.engine.input.Input;
import com.ustudents.engine.core.cli.print.Out;
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
    private String name;
    private Vector2i size;
    private long windowHandle;
    private String glslVersion;
    public Input input;

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
        input = new Input();

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
        glfwSetKeyCallback(windowHandle, input.getKeyBoard());
        glfwSetMouseButtonCallback(windowHandle, input.getMouseButton());

        glfwMakeContextCurrent(windowHandle);
        setVsync(vsync);

        GL.createCapabilities();
        glClearColor(0.1725f, 0.1882f, 0.2117f, 1.0f);

        if (Game.isDebugging()) {
            Out.printlnDebug("OpenGL version: " + glGetString(GL_VERSION));
            Out.printlnDebug("OpenGL vendor: " + glGetString(GL_VENDOR));
            Out.printlnDebug("OpenGL renderer: " + glGetString(GL_RENDERER));
            Out.printlnDebug("OpenGL shading language version: " + glGetString(GL_SHADING_LANGUAGE_VERSION));
        }

        glfwSetWindowSizeCallback(windowHandle, new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                needsResize(new Vector2i(width, height));
            }
        });
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
        } else {
            glfwHideWindow(windowHandle);
        }
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(windowHandle);
    }

    public void pollEvents() {
        glfwPollEvents();
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

    private void needsResize(Vector2i size) {
        this.size = size;
        Game.get().forceResize();
    }
}
