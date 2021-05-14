package com.ustudents.engine.input.empty;

import com.ustudents.engine.core.event.EventListener;
import com.ustudents.engine.core.window.events.KeyStateChangedEvent;
import com.ustudents.engine.core.window.events.MouseButtonStateChangedEvent;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class EmptyInput {
    public void initialize() {

    }

    public void update(float dt) {

    }

    public boolean isKeyDown(int key) {
        return false;
    }

    public boolean isKeyUp(int key) {
        return false;
    }

    public boolean isKeyPressed(int key) {
        return false;
    }

    public boolean isKeyReleased(int key) {
        return false;
    }

    public boolean isMouseDown(int button) {
        return false;
    }

    public boolean isMouseUp(int key) {
        return false;
    }

    public boolean isMousePressed(int button) {
        return false;
    }

    public boolean isMouseRelease(int button) {
        return false;
    }

    public int getScroll() {
        return 0;
    }

    public Vector2f getMousePos() {
        return new Vector2f(0f, 0f);
    }

    public boolean isMouseInViewRect(Vector4f viewRect) {
        return false;
    }

    public boolean isMouseInWorldViewRect(Vector4f viewRect) {
        return false;
    }

    public void recalculateMousePosition() {

    }

    public void resetKeyAndButton() {

    }

    public void stopInputHandling() {

    }

    public void addKeyStateChangedListener(EventListener<KeyStateChangedEvent> listener, String name) {

    }

    public void addMouseButtonStateChangedListener(EventListener<MouseButtonStateChangedEvent> listener, String name) {

    }

    public void removeKeyStateChangedListener(String name) {

    }

    public void removeMouseButtonStateChangedListener(String name) {

    }
}
