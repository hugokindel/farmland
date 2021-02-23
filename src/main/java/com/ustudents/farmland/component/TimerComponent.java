package com.ustudents.farmland.component;

public class TimerComponent {
    private static float currentTime;
    private final static float timerPerPlayer = 90.0f;
    private final static float timeBeforeRename = 0.0f;

    public static float getCurrentTime() {
        return currentTime;
    }

    public static void update(float dt) {
        currentTime+=dt;
    }

    public static void setCurrentTime(float time) {
        currentTime = time;
    }

    public static float getTimerPerPlayer() {
        return timerPerPlayer;
    }

    public static float getTimeBeforeRename() {
        return timeBeforeRename;
    }
}
