package com.ustudents.engine.core.event;

public interface EventListener<T extends Event> {
    public void onReceived(Class<?> dataType, T data);
}
