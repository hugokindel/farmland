package com.ustudents.engine.core.event;

import java.util.ArrayList;
import java.util.List;

public class EventDispatcher {
    List<EventListener> listeners;
    Class<?> dataType;

    public EventDispatcher() {
        this(EventData.class);
    }

    public EventDispatcher(Class<?> eventType) {
        this.listeners = new ArrayList<>();
        this.dataType = eventType;
    }

    public void dispatch() {
        dispatch(new EventData());
    }

    public void dispatch(Object data) {
        for (EventListener listener : listeners) {
            listener.onReceived(dataType, data);
        }
    }

    public void add(EventListener listener) {
        listeners.add(listener);
    }
}
