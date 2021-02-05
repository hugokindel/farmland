package com.ustudents.farmland.core;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

public class Input {
    private static final boolean[] keys = new boolean[GLFW.GLFW_KEY_LAST];
    private static final boolean[] buttons = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];
    private double mouseX,mouseY;
    private final GLFWKeyCallback keyBoard;
    private final GLFWCursorPosCallback mouseMove;
    private final GLFWMouseButtonCallback mouseButton;

    public Input() {
        keyBoard = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                keys[key] = (action != GLFW.GLFW_RELEASE);
            }
        };

        mouseMove = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window,double posX,double posY) {
                mouseX = posX;
                mouseY = posY;
            }
        };

        mouseButton = new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                buttons[button] = (action != GLFW.GLFW_RELEASE);
            }
        };
    }

    public static boolean isKeyDown(int key){
        return keys[key];
    }

    public static boolean isButtonDown(int button){
        return buttons[button];
    }

    public double getMouseX() {
        return mouseX;
    }

    public double getMouseY() {
        return mouseY;
    }

    public GLFWKeyCallback getKeyBoard() {
        return keyBoard;
    }

    public GLFWCursorPosCallback getMouseMove() {
        return mouseMove;
    }

    public GLFWMouseButtonCallback getMouseButton() {
        return mouseButton;
    }

    public void destroy(){
        keyBoard.free();
        mouseMove.free();
        mouseButton.free();
    }
}
