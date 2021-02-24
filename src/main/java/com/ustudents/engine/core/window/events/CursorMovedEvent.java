package com.ustudents.engine.core.window.events;

import com.ustudents.engine.core.event.Event;
import org.joml.Vector2f;

public class CursorMovedEvent extends Event {
    public Vector2f position;

    public CursorMovedEvent(Vector2f position) {
        this.position = position;
    }
}