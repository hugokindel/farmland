package com.ustudents.engine.input;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.event.EventDispatcher;
import com.ustudents.farmland.Farmland;
import jdk.jfr.Event;
import org.joml.Vector2f;
import org.lwjgl.glfw.*;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;

public class Input {
    private static final int[] keyStates = new int[GLFW.GLFW_KEY_LAST];
    private static final boolean[] keys = new boolean[GLFW.GLFW_KEY_LAST];
    private static final int[] mouseStates = new int[GLFW.GLFW_MOUSE_BUTTON_LAST];
    private static final boolean[] mouseButtons = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];
    private static Vector2f mousePos;
    private static double scrollX,scrollY;
    private final GLFWKeyCallback keyBoard;
    private final GLFWMouseButtonCallback mouseButton;
    private static GLFWScrollCallback scrollCallback;
    public static EventDispatcher mouseMoved;

    public Input() {
        mouseMoved = new EventDispatcher();

        init();
        keyBoard = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                keys[key] = (action != GLFW.GLFW_RELEASE);
                keyStates[key] = action;
            }
        };

        glfwSetCursorPosCallback(Farmland.get().getWindow().getHandle(), new GLFWCursorPosCallback() {
            public void invoke(long window, double xpos, double ypos) {
                mousePos = new Vector2f((float)xpos, (float)ypos);
                mouseMoved.dispatch();
            }
        });

        mouseButton = new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                mouseButtons[button] = (action != GLFW.GLFW_RELEASE);
                mouseStates[button] = action;
            }
        };
    }

    protected static void init(){
        resetKeyAndButton();
    }

    protected static void update() {
        resetKeyAndButton();

    }

    private static void resetKeyAndButton(){
        Arrays.fill(keyStates, -1);
        Arrays.fill(mouseStates, -1);
    }

    public static boolean isKeyDown(int key){
        return keys[key];
    }

    public static boolean isKeyUp(int key){ ;
        return !keys[key];
    }

    public static boolean isKeyPressed(int key){
        boolean releaseKey = false;
        if(keyStates[key] == GLFW.GLFW_PRESS){
            releaseKey = true;
            keyStates[key] = -1;
        }
        return releaseKey;
    }

    public static boolean isKeyReleased(int key) {
        boolean releaseKey = false;
        if(keyStates[key] == GLFW.GLFW_RELEASE){
            releaseKey = true;
            keyStates[key] = -1;
        }
        return releaseKey;
    }

    public static boolean isMouseDown(int button){
        return mouseButtons[button];
    }

    public static boolean isMouseUp(int key){ ;
        return !mouseButtons[key];
    }

    public static boolean isMousePressed(int button){
        boolean releaseKey = false;
        if(mouseStates[button] == GLFW.GLFW_PRESS){
            releaseKey = true;
            mouseStates[button] = -1;
        }
        return releaseKey;
    }

    public static boolean isMouseRelease(int button){
        boolean releaseKey = false;
        if(mouseStates[button] == GLFW.GLFW_RELEASE){
            releaseKey = true;
            mouseStates[button] = -1;
        }
        return releaseKey;
    }

    private static void hasScroll(){
        glfwSetScrollCallback(Game.get().getWindow().getHandle(), scrollCallback = new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xoffset, double yoffset) {
                scrollX = xoffset;
                scrollY = yoffset;
            }
        });
    }

    public static int scroll(){
        if(scrollCallback == null) {
            hasScroll();
        }
        if(scrollY<0){
            scrollY = 0;
            return -1;
        }else if(scrollY>0){
            scrollY = 0;
            return 1;
        }
        return 0;
    }

    public static Vector2f getMousePos() {
        return mousePos;
    }

    public GLFWKeyCallback getKeyBoard() {
        return keyBoard;
    }

    public GLFWMouseButtonCallback getMouseButton() {
        return mouseButton;
    }

    public void destroy(){
        keyBoard.free();
        mouseButton.free();
    }
}
