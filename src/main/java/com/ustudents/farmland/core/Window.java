package com.ustudents.farmland.core;

import com.ustudents.farmland.cli.print.Out;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
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

        findGlslVersion();

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        input = new Input();
        windowHandle = glfwCreateWindow(size.x, size.y, name, NULL, NULL);

        if (windowHandle == NULL) {
            String errorMessage = "Failed to create the glfw window!";
            Out.printlnError(errorMessage);
            throw new RuntimeException(errorMessage);
        }

        try ( MemoryStack stack = stackPush() ) {
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
        glfwSetCursorPosCallback(windowHandle, input.getMouseMove());
        glfwSetMouseButtonCallback(windowHandle, input.getMouseButton());

        glfwMakeContextCurrent(windowHandle);
        setVsync(vsync);

        GL.createCapabilities();
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void swap() {
        glfwSwapBuffers(windowHandle);
    }

    public void destroy() {
        glfwFreeCallbacks(windowHandle);
        glfwDestroyWindow(windowHandle);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
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

    private void findGlslVersion() {
        final boolean isMac = System.getProperty("os.name").toLowerCase().contains("mac");

        if (isMac) {
            glslVersion = "#version 150";
            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);  // 3.2+ only
            glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);          // Required on Mac
        } else {
            glslVersion = "#version 130";
            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);
        }
    }

    public void setVsync(boolean enabled) {
        glfwSwapInterval(enabled ? 1 : 0);
    }
}
