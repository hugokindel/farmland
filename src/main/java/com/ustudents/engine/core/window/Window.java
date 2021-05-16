package com.ustudents.engine.core.window;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.event.EventDispatcher;
import com.ustudents.engine.core.window.empty.EmptyWindow;
import com.ustudents.engine.core.window.glfw.GLFWWindow;
import org.joml.Vector2i;

public class Window {
    public enum Type {
        Windowed,
        Fullscreen,
        Borderless
    }

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

    public void clearBuffer() {
        windowManager.clearBuffer();
    }

    public void swapBuffer() {
        windowManager.swapBuffer();
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

    public void renderToTarget() {
        windowManager.renderToBuffer();
    }

    public int getTexture() {
       return windowManager.getTexture();
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

    public void switchType(Window.Type type) {
        windowManager.switchType(type);
    }

    public static Window get() {
        return Game.get().getWindow();
    }

    public EmptyWindow getWindow() {
        return windowManager;
    }

    public void chooseNextType() {
        int nextType = Resources.getConfig().windowType.ordinal() == 2 ? 0 : Resources.getConfig().windowType.ordinal() + 1;
        switchType(Type.values()[nextType]);
    }

    public void chooseDefaultType() {
        switchType(Type.values()[0]);
    }
}
