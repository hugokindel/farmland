package com.ustudents.engine.core.event;

import java.util.ArrayList;
import java.util.List;

public class EventDispatcher<T extends Event> {
    List<EventListener<T>> listeners;
    Class<?> dataType;

    public EventDispatcher() {
        this(Event.class);
    }

    public EventDispatcher(Class<?> eventType) {
        this.listeners = new ArrayList<>();
        this.dataType = eventType;
    }

    public void dispatch() {
        dispatch(null);
    }

    public void dispatch(T data) {
        for (EventListener<T> listener : listeners) {
            if (listener != null) {
                listener.onReceived(dataType, data);
            }
        }
    }

    public void add(EventListener<T> listener) {
        listeners.add(listener);
    }

    public void remove(EventListener<T> listener) {
        listeners.remove(listener);
    }
}
