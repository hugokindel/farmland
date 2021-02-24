package com.ustudents.engine.input.glfw;

import com.ustudents.engine.core.window.Window;
import com.ustudents.engine.core.window.events.CursorMovedEvent;
import com.ustudents.engine.core.window.events.KeyStateChangedEvent;
import com.ustudents.engine.core.window.events.MouseButtonStateChangedEvent;
import com.ustudents.engine.core.window.events.ScrollMovedEvent;
import com.ustudents.engine.input.empty.EmptyInput;
import com.ustudents.engine.scene.SceneManager;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;

public class GLFWInput extends EmptyInput {
    private int[] keyStates;

    private boolean[] keys;

    private int[] mouseStates;

    private boolean[] mouseButtons;

    private Vector2f mousePos;

    private Vector2f mousePosInWorld;

    private Vector2f scroll;

    @Override
    public void initialize() {
        keyStates = new int[GLFW.GLFW_KEY_LAST];
        keys = new boolean[GLFW.GLFW_KEY_LAST];
        mouseStates = new int[GLFW.GLFW_MOUSE_BUTTON_LAST];
        mouseButtons = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];
        mousePos = new Vector2f();
        mousePosInWorld = new Vector2f();
        scroll = new Vector2f();

        setupCallbacks();
        resetKeyAndButton();
    }

    @Override
    public boolean isKeyDown(int key) {
        return keys[key];
    }

    @Override
    public boolean isKeyUp(int key) {
        return !keys[key];
    }

    @Override
    public boolean isKeyPressed(int key) {
        boolean releaseKey = false;

        if (keyStates[key] == GLFW.GLFW_PRESS) {
            releaseKey = true;
            keyStates[key] = -1;
        }

        return releaseKey;
    }

    @Override
    public boolean isKeyReleased(int key) {
        boolean releaseKey = false;

        if (keyStates[key] == GLFW.GLFW_RELEASE){
            releaseKey = true;
            keyStates[key] = -1;
        }

        return releaseKey;
    }

    @Override
    public boolean isMouseDown(int button) {
        return mouseButtons[button];
    }

    @Override
    public boolean isMouseUp(int key) {
        return !mouseButtons[key];
    }

    @Override
    public boolean isMousePressed(int button) {
        boolean releaseKey = false;

        if (mouseStates[button] == GLFW.GLFW_PRESS) {
            releaseKey = true;
            mouseStates[button] = -1;
        }

        return releaseKey;
    }

    @Override
    public boolean isMouseRelease(int button) {
        boolean releaseKey = false;

        if (mouseStates[button] == GLFW.GLFW_RELEASE) {
            releaseKey = true;
            mouseStates[button] = -1;
        }

        return releaseKey;
    }

    @Override
    public int getScroll() {
        if (scroll.y < 0) {
            scroll.y = 0;
            return -1;
        } else if (scroll.y > 0) {
            scroll.y = 0;
            return 1;
        }

        return 0;
    }

    @Override
    public Vector2f getMousePos() {
        return mousePos == null ? new Vector2f(-1, -1) : mousePos;
    }

    @Override
    public boolean isMouseInViewRect(Vector4f viewRect) {
        return mousePos.x > viewRect.x && mousePos.x < viewRect.z && mousePos.y > viewRect.y && mousePos.y < viewRect.w;
    }

    @Override
    public boolean isMouseInWorldViewRect(Vector4f viewRect) {
        return mousePosInWorld.x > viewRect.x && mousePosInWorld.x < viewRect.z && mousePosInWorld.y > viewRect.y && mousePosInWorld.y < viewRect.w;
    }

    @Override
    public void recalculateMousePosition() {
        mousePosInWorld = SceneManager.getScene().getWorldCamera().screenCoordToWorldCoord(mousePos);
    }

    private void setupCallbacks() {
        Window.get().getKeyStateChanged().add((dataType, data) -> {
            KeyStateChangedEvent eventData = (KeyStateChangedEvent) data;
            keys[eventData.key] = (eventData.action != GLFW.GLFW_RELEASE);
            keyStates[eventData.key] = eventData.action;
        });

        Window.get().getCursorMoved().add((dataType, data) -> {
            CursorMovedEvent eventData = (CursorMovedEvent) data;
            mousePos = new Vector2f(eventData.position.x, eventData.position.y);
            if (SceneManager.getScene().getWorldCamera() != null) {
                mousePosInWorld = SceneManager.getScene().getWorldCamera().screenCoordToWorldCoord(mousePos);
            }
        });

        Window.get().getMouseButtonStateChanged().add((dataType, data) -> {
            MouseButtonStateChangedEvent eventData = (MouseButtonStateChangedEvent) data;
            mouseButtons[eventData.button] = (eventData.action != GLFW.GLFW_RELEASE);
            mouseStates[eventData.button] = eventData.action;
        });

        Window.get().getScrollMoved().add((dataType, data) -> {
            ScrollMovedEvent eventData = (ScrollMovedEvent) data;
            scroll = new Vector2f(eventData.offets.x, eventData.offets.y);
        });
    }

    private void resetKeyAndButton() {
        Arrays.fill(keyStates, -1);
        Arrays.fill(mouseStates, -1);
    }
}
