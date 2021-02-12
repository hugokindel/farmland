package com.ustudents.engine.core.event;

import java.util.ArrayList;
import java.util.List;

public class EventDispatcher {
    List<EventListener> listeners;

    public EventDispatcher() {
        listeners = new ArrayList<>();
    }

    public void dispatch() {
        for (EventListener listener : listeners) {
            listener.onReceived();
        }
    }

    public void add(EventListener listener) {
        listeners.add(listener);
    }
}
