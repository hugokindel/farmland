package com.ustudents.engine.core.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventDispatcher<T extends Event> {
    List<EventListener<T>> listeners;
    Map<String, EventListener<T>> listenersPerName;
    Class<?> dataType;

    public EventDispatcher() {
        this(Event.class);
    }

    public EventDispatcher(Class<?> eventType) {
        this.listeners = new ArrayList<>();
        this.listenersPerName = new HashMap<>();
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

    public void add(EventListener<T> listener, String name) {
        remove(name);
        listeners.add(listener);
        listenersPerName.put(name, listener);
    }

    public void remove(EventListener<T> listener) {
        listeners.remove(listener);
    }

    public void remove(String name) {
        if (listenersPerName.containsKey(name)) {
            listeners.remove(listenersPerName.get(name));
            listenersPerName.remove(name);
        }
    }

    public void clear() {
        listeners.clear();
        listenersPerName.clear();
    }
}
