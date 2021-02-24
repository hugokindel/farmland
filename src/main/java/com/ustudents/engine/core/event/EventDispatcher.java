package com.ustudents.engine.core.event;

import java.util.ArrayList;
import java.util.List;

public class EventDispatcher {
    List<EventListener> listeners;
    Class<?> dataType;

    public EventDispatcher() {
        this(Event.class);
    }

    public EventDispatcher(Class<?> eventType) {
        this.listeners = new ArrayList<>();
        this.dataType = eventType;
    }

    public void dispatch() {
        dispatch(new Event());
    }

    public void dispatch(Object data) {
        for (EventListener listener : listeners) {
            if (listener != null) {
                listener.onReceived(dataType, data);
            }
        }
    }

    public void add(EventListener listener) {
        listeners.add(listener);
    }

    public void remove(EventListener listener) {
        listeners.remove(listener);
    }
}
