package com.ustudents.engine.core.window;

import com.ustudents.engine.Game;
import com.ustudents.engine.audio.empty.EmptySound;
import com.ustudents.engine.audio.openal.ALSound;
import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.event.Event;
import com.ustudents.engine.core.event.EventDispatcher;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.core.window.empty.EmptyWindow;
import com.ustudents.engine.core.window.events.*;
import com.ustudents.engine.core.window.glfw.GLFWWindow;
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
    EmptyWindow windowManager;

    public void initialize(String name, Vector2i size, boolean vsync) {
        switch (Game.get().getWindowSystemType()) {
            case Empty:
                windowManager = new EmptyWindow();
                break;
            case GLFW:
                windowManager = new GLFWWindow();
                break;
        }

        windowManager.initialize(name, size, vsync);
    }

    public void clear() {
        windowManager.clear();
    }

    public void swap() {
        windowManager.swap();
    }

    public void destroy() {
        windowManager.destroy();
    }

    public void show(boolean show) {
        windowManager.show(show);
    }

    public void actualizeCursorType() {
        windowManager.actualizeCursorType();
    }

    public boolean shouldQuit() {
        return windowManager.shouldQuit();
    }

    public void pollEvents() {
        windowManager.pollEvents();
    }

    public String getName() {
        return windowManager.getName();
    }

    public void setName(String name) {
        windowManager.setName(name);
    }

    public Vector2i getSize() {
        return windowManager.getSize();
    }

    public void setSize(Vector2i size) {
        windowManager.setSize(size);
    }

    public long getHandle() {
        return windowManager.getHandle();
    }

    public Vector2i getPosition() {
        return windowManager.getPosition();
    }

    public void setVsync(boolean enabled) {
        windowManager.setVsync(enabled);
    }

    public void changeIcon(String filePath) {
        windowManager.changeIcon(filePath);
    }

    public EventDispatcher getSizeChanged() {
        return windowManager.sizeChanged;
    }

    public EventDispatcher getKeyStateChanged() {
        return windowManager.keyStateChanged;
    }

    public EventDispatcher getMouseButtonStateChanged() {
        return windowManager.mouseButtonStateChanged;
    }

    public EventDispatcher getCursorMoved() {
        return windowManager.cursorMoved;
    }

    public EventDispatcher getScrollMoved() {
        return windowManager.scrollMoved;
    }

    public static Window get() {
        return Game.get().getWindow();
    }

    public EmptyWindow getWindow() {
        return windowManager;
    }
}
