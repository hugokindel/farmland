package com.ustudents.engine.core.window.events;

import com.ustudents.engine.core.event.Event;

public class MouseButtonStateChangedEvent extends Event {
    public int button;
    public int action;
    public int mods;

    public MouseButtonStateChangedEvent(int button, int action, int mods) {
        this.button = button;
        this.action = action;
        this.mods = mods;
    }
}