package com.ustudents.engine.input.glfw;

import com.ustudents.engine.core.window.Window;
import com.ustudents.engine.core.window.events.CursorMovedEvent;
import com.ustudents.engine.core.window.events.KeyStateChangedEvent;
import com.ustudents.engine.core.window.events.MouseButtonStateChangedEvent;
import com.ustudents.engine.core.window.events.ScrollMovedEvent;
import com.ustudents.engine.input.empty.EmptyInput;
import com.ustudents.engine.scene.Scene;
import com.ustudents.engine.scene.SceneManager;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.scene.menus.SettingsKeybindMenu;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;

@SuppressWarnings("unchecked")
public class GLFWInput extends EmptyInput {
    private int[] keyStates;

    private int[] keyPressedStates;

    private boolean[] keys;

    private int[] mouseStates;

    private int[] mousePressedStates;

    private boolean[] mouseButtons;

    private Vector2f mousePos;

    private Vector2f mousePosInWorld;

    private Vector2f scroll;

    @Override
    public void initialize() {
        keyStates = new int[GLFW.GLFW_KEY_LAST];
        keyPressedStates = new int[GLFW.GLFW_KEY_LAST];
        keys = new boolean[GLFW.GLFW_KEY_LAST];
        mouseStates = new int[GLFW.GLFW_MOUSE_BUTTON_LAST];
        mousePressedStates = new int[GLFW.GLFW_MOUSE_BUTTON_LAST];
        mouseButtons = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];
        mousePos = new Vector2f();
        mousePosInWorld = new Vector2f();
        scroll = new Vector2f();

        setupCallbacks();
        resetKeyAndButton();
    }

    @Override
    public void update(float dt) {
        Arrays.fill(keyPressedStates, -1);
        Arrays.fill(mousePressedStates, -1);
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
        return keyPressedStates[key] == GLFW.GLFW_PRESS;
    }

    @Override
    public boolean isKeyReleased(int key) {
        return keyPressedStates[key] == GLFW.GLFW_RELEASE;
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
        return mousePressedStates[button] == GLFW.GLFW_PRESS;
    }

    @Override
    public boolean isMouseRelease(int button) {
        return mousePressedStates[button] == GLFW.GLFW_RELEASE;
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

    public int findKey(){
        for(int i = 0; i < keys.length; i++){
            if(keys[i]){
                return i;
            }
        }
        return -1;
    }

    private void setupCallbacks() {
        Window.get().getKeyStateChanged().add((dataType, data) -> {
            KeyStateChangedEvent eventData = (KeyStateChangedEvent) data;
            if (eventData.key != -1) {
                Scene currentScene = Farmland.get().getSceneManager().getCurrentScene();
                if((currentScene instanceof SettingsKeybindMenu && ((SettingsKeybindMenu)currentScene).searchAction() != null &&
                        ((SettingsKeybindMenu)currentScene).avoidKey(eventData.key))) {
                    currentScene.changeScene(new SettingsKeybindMenu(2), false);
                }else if(currentScene instanceof SettingsKeybindMenu &&
                        ((SettingsKeybindMenu)currentScene).bindNotAlreadyDefine(eventData.key, true)) {
                    ((SettingsKeybindMenu) currentScene).selectNewBind(true, eventData.key);
                    currentScene.changeScene(new SettingsKeybindMenu(), false);
                }else if((currentScene instanceof SettingsKeybindMenu && ((SettingsKeybindMenu)currentScene).searchAction() != null &&
                        !((SettingsKeybindMenu)currentScene).bindNotAlreadyDefine(eventData.key, true))){
                    currentScene.changeScene(new SettingsKeybindMenu(1), false/*(((CommandsMenu) currentScene).searchAction())*/);
                }else{
                    keys[eventData.key] = (eventData.action != GLFW.GLFW_RELEASE);
                    keyStates[eventData.key] = eventData.action;
                    keyPressedStates[eventData.key] = eventData.action;
                }
            }
        });

        Window.get().getCursorMoved().add((dataType, data) -> {
            CursorMovedEvent eventData = (CursorMovedEvent) data;
            mousePos = new Vector2f(eventData.position.x, eventData.position.y);
            if (SceneManager.getScene() != null && SceneManager.getScene().getWorldCamera() != null) {
                mousePosInWorld = SceneManager.getScene().getWorldCamera().screenCoordToWorldCoord(mousePos);
            }
        });

        Window.get().getMouseButtonStateChanged().add((dataType, data) -> {
            MouseButtonStateChangedEvent eventData = (MouseButtonStateChangedEvent) data;
            Scene currentScene = Farmland.get().getSceneManager().getCurrentScene();
            if(currentScene instanceof SettingsKeybindMenu &&
                    ((SettingsKeybindMenu)currentScene).bindNotAlreadyDefine(eventData.button, false)){
                ((SettingsKeybindMenu)currentScene).selectNewBind(false, eventData.button);
            }else{
                mouseButtons[eventData.button] = (eventData.action != GLFW.GLFW_RELEASE);
                mouseStates[eventData.button] = eventData.action;
                mousePressedStates[eventData.button] = eventData.action;
            }
        });

        Window.get().getScrollMoved().add((dataType, data) -> {
            ScrollMovedEvent eventData = (ScrollMovedEvent) data;
            scroll = new Vector2f(eventData.offets.x, eventData.offets.y);
        });
    }

    @Override
    public void resetKeyAndButton() {
        Arrays.fill(keys, false);
        Arrays.fill(keyStates, -1);
        Arrays.fill(mouseStates, -1);
        Arrays.fill(keyPressedStates, -1);
        Arrays.fill(mousePressedStates, -1);
    }
}
