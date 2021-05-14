package com.ustudents.engine.input;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.event.EventListener;
import com.ustudents.engine.core.window.events.KeyStateChangedEvent;
import com.ustudents.engine.core.window.events.MouseButtonStateChangedEvent;
import com.ustudents.engine.input.empty.EmptyInput;
import com.ustudents.engine.input.glfw.GLFWInput;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.Map;

public class Input {
    private static EmptyInput inputManager;

    public static void initialize() {
        switch (Game.get().getInputSystemType()) {
            case Empty:
                inputManager = new EmptyInput();
                break;
            case GLFW:
                inputManager = new GLFWInput();
                break;
        }

        inputManager.initialize();
    }

    public static void update(float dt) {
        inputManager.update(dt);
    }

    public static boolean isKeyDown(int key) {
        return inputManager.isKeyDown(key);
    }

    public static boolean isKeyUp(int key) {
        return inputManager.isKeyUp(key);
    }

    public static boolean isKeyPressed(int key) {
        return inputManager.isKeyPressed(key);
    }

    public static boolean isKeyReleased(int key) {
        return inputManager.isKeyReleased(key);
    }

    public static boolean isMouseDown(int button) {
        return inputManager.isMouseDown(button);
    }

    public static boolean isMouseUp(int key) {
        return inputManager.isMouseUp(key);
    }

    public static boolean isMousePressed(int button) {
        return inputManager.isMousePressed(button);
    }

    public static boolean isMouseRelease(int button) {
        return inputManager.isMouseRelease(button);
    }

    public static int getScroll() {
        return inputManager.getScroll();
    }

    public static Vector2f getMousePos() {
        return inputManager.getMousePos();
    }

    public static boolean isMouseInViewRect(Vector4f viewRect) {
        return inputManager.isMouseInViewRect(viewRect);
    }

    public static boolean isMouseInWorldViewRect(Vector4f viewRect) {
        return inputManager.isMouseInWorldViewRect(viewRect);
    }

    public static void recalculateMousePosition() {
        inputManager.recalculateMousePosition();
    }

    public static boolean isActionSuccessful(String action){
        if(Resources.getConfig().commands.containsKey(action)){
            return Resources.getConfig().commands.get(action).OneMappingIsSuccessful();
        }
        return false;
    }

    public static int findKey(){
        return ((GLFWInput)inputManager).findKey();
    }

    public static boolean actionExists(String name) {
        return Resources.getConfig().commands.containsKey(name);
    }

    public static void resetKeyAndButton() {
        inputManager.resetKeyAndButton();
    }

    public static void stopInputHandling() {
        inputManager.stopInputHandling();
    }

    public static void addKeyStateChangedListener(EventListener<KeyStateChangedEvent> listener, String name) {
        inputManager.addKeyStateChangedListener(listener, name);
    }

    public static void addMouseButtonStateChangedListener(EventListener<MouseButtonStateChangedEvent> listener, String name) {
        inputManager.addMouseButtonStateChangedListener(listener, name);
    }

    public static void removeKeyStateChangedListener(String name) {
        inputManager.removeKeyStateChangedListener(name);
    }

    public static void removeMouseButtonStateChangedListener(String name) {
        inputManager.removeMouseButtonStateChangedListener(name);
    }
}
