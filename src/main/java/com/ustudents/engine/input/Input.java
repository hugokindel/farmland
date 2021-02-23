package com.ustudents.engine.input;

import com.ustudents.engine.core.Window;
import com.ustudents.engine.scene.SceneManager;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.glfw.*;

import java.util.Arrays;

public class Input {
    private static final int[] keyStates = new int[GLFW.GLFW_KEY_LAST];

    private static final boolean[] keys = new boolean[GLFW.GLFW_KEY_LAST];

    private static final int[] mouseStates = new int[GLFW.GLFW_MOUSE_BUTTON_LAST];

    private static final boolean[] mouseButtons = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];

    private static Vector2f mousePos = new Vector2f();

    private static Vector2f mousePosInWorld = new Vector2f();

    private static Vector2f scroll = new Vector2f();

    public Input() {
        initialize();

        Window.get().keyStateChanged.add((dataType, data) -> {
            Window.KeyStateChangedEventData eventData = (Window.KeyStateChangedEventData) data;
            keys[eventData.key] = (eventData.action != GLFW.GLFW_RELEASE);
            keyStates[eventData.key] = eventData.action;
        });

        Window.get().cursorMoved.add((dataType, data) -> {
            Window.CursorMovedEventData eventData = (Window.CursorMovedEventData) data;
            mousePos = new Vector2f(eventData.position.x, eventData.position.y);
            if (SceneManager.getScene().getWorldCamera() != null) {
                mousePosInWorld = SceneManager.getScene().getWorldCamera().screenCoordToWorldCoord(mousePos);
            }
        });

        Window.get().mouseButtonStateChanged.add((dataType, data) -> {
            Window.MouseButtonStateChangedEventData eventData = (Window.MouseButtonStateChangedEventData) data;
            mouseButtons[eventData.button] = (eventData.action != GLFW.GLFW_RELEASE);
            mouseStates[eventData.button] = eventData.action;
        });

        Window.get().scrollMoved.add((dataType, data) -> {
            Window.ScrollMovedEventData eventData = (Window.ScrollMovedEventData) data;
            scroll = new Vector2f(eventData.offets.x, eventData.offets.y);
        });
    }

    public void destroy() {

    }

    protected static void initialize() {
        resetKeyAndButton();
    }

    protected static void update() {
        resetKeyAndButton();

    }

    public static boolean isKeyDown(int key){
        return keys[key];
    }

    public static boolean isKeyUp(int key) {
        return !keys[key];
    }

    public static boolean isKeyPressed(int key) {
        boolean releaseKey = false;

        if (keyStates[key] == GLFW.GLFW_PRESS) {
            releaseKey = true;
            keyStates[key] = -1;
        }

        return releaseKey;
    }

    public static boolean isKeyReleased(int key) {
        boolean releaseKey = false;
        if (keyStates[key] == GLFW.GLFW_RELEASE){
            releaseKey = true;
            keyStates[key] = -1;
        }
        return releaseKey;
    }

    public static boolean isMouseDown(int button) {
        return mouseButtons[button];
    }

    public static boolean isMouseUp(int key) {
        return !mouseButtons[key];
    }

    public static boolean isMousePressed(int button) {
        boolean releaseKey = false;

        if (mouseStates[button] == GLFW.GLFW_PRESS) {
            releaseKey = true;
            mouseStates[button] = -1;
        }

        return releaseKey;
    }

    public static boolean isMouseRelease(int button) {
        boolean releaseKey = false;

        if (mouseStates[button] == GLFW.GLFW_RELEASE) {
            releaseKey = true;
            mouseStates[button] = -1;
        }

        return releaseKey;
    }

    public static int scroll() {
        if (scroll.y < 0){
            scroll.y = 0;
            return -1;
        } else if (scroll.y > 0) {
            scroll.y = 0;
            return 1;
        }
        return 0;
    }

    public static Vector2f getMousePos() {
        return mousePos == null ? new Vector2f(-1, -1) : mousePos;
    }

    public static boolean isMouseInViewRect(Vector4f viewRect) {
        return mousePos.x > viewRect.x && mousePos.x < viewRect.z && mousePos.y > viewRect.y && mousePos.y < viewRect.w;
    }

    public static boolean isMouseInWorldViewRect(Vector4f viewRect) {
        return mousePosInWorld.x > viewRect.x && mousePosInWorld.x < viewRect.z && mousePosInWorld.y > viewRect.y && mousePosInWorld.y < viewRect.w;
    }

    public static void recalculateMousePosition() {
        mousePosInWorld = SceneManager.getScene().getWorldCamera().screenCoordToWorldCoord(mousePos);
    }

    private static void resetKeyAndButton() {
        Arrays.fill(keyStates, -1);
        Arrays.fill(mouseStates, -1);
    }
}
