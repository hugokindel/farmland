package com.ustudents.engine.core.window.events;

import com.ustudents.engine.core.event.Event;
import org.joml.Vector2f;

public class ScrollMovedEvent extends Event {
    public Vector2f offets;

    public ScrollMovedEvent(Vector2f offets) {
        this.offets = offets;
    }
}