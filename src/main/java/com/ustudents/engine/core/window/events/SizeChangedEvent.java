package com.ustudents.engine.core.window.events;

import com.ustudents.engine.core.event.Event;
import org.joml.Vector2i;

public class SizeChangedEvent extends Event {
    public Vector2i size;

    public SizeChangedEvent(Vector2i size) {
        this.size = size;
    }
}