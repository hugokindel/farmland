package com.ustudents.engine.core.window.empty;

import com.ustudents.engine.core.event.EventDispatcher;
import com.ustudents.engine.core.window.Window;
import com.ustudents.engine.core.window.events.*;
import org.joml.Vector2i;

@SuppressWarnings("unchecked")
public class EmptyWindow {
    public EventDispatcher sizeChanged = new EventDispatcher(SizeChangedEvent.class);

    public EventDispatcher keyStateChanged = new EventDispatcher(KeyStateChangedEvent.class);

    public EventDispatcher mouseButtonStateChanged = new EventDispatcher(MouseButtonStateChangedEvent.class);

    public EventDispatcher cursorMoved = new EventDispatcher(CursorMovedEvent.class);

    public EventDispatcher scrollMoved = new EventDispatcher(ScrollMovedEvent.class);

    public void initialize(String name, Vector2i size, boolean vsync) {

    }

    public void clearBuffer() {

    }

    public void swapBuffer() {

    }

    public void clear() {

    }

    public void swap() {

    }

    public void destroy() {

    }

    public void show(boolean show) {

    }

    public void actualizeCursorType() {

    }

    public boolean shouldQuit() {
        return false;
    }

    public void pollEvents() {

    }

    public String getName() {
        return "";
    }

    public void setName(String name) {

    }

    public Vector2i getSize() {
        return new Vector2i();
    }

    public void setSize(Vector2i size) {

    }

    public long getHandle() {
        return -1;
    }

    public Vector2i getPosition() {
        return new Vector2i();
    }

    public void setVsync(boolean enabled) {

    }

    public void changeIcon(String filePath) {

    }

    public void renderToBuffer() {

    }

    public int getTexture() {
        return -1;
    }

    public void switchType(Window.Type type) {

    }
}
