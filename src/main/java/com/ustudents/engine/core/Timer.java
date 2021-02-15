package com.ustudents.engine.core;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Timer {
    private double deltaTime;
    private double previousFrameDuration;
    private int framesCounter;
    private int fps;
    private double previousFrameCounterTime;
    private static int currentTime;
    private final static int timerPerPlayer = 90*10000;

    private final static int timeBeforeRename = 3*10000;

    public Timer() {
        deltaTime = 0.0;
        previousFrameDuration = 0.0;
    }

    public void update() {
        deltaTime = (glfwGetTime() - previousFrameDuration);
        previousFrameDuration = glfwGetTime();
    }

    public void render() {
        double currentTime = glfwGetTime();
        framesCounter++;

        if (currentTime - previousFrameCounterTime >= 1.0) {
            fps = framesCounter;
            framesCounter = 0;
            previousFrameCounterTime = currentTime;
        }
    }

    public double getDeltaTime() {
        return deltaTime;
    }

    public int getFPS() {
        return fps;
    }

    public double getFrameDuration() {
        return 1000.0 / (double)fps;
    }

    public static int getCurrentTime(){
        return currentTime;
    }

    public static void increaseCurrentTime(){
        currentTime++;
    }

    public static void setCurrentTime(int time){
        currentTime = time;
    }

    public static int getTimerPerPlayer() {
        return timerPerPlayer;
    }

    public static int getTimeBeforeRename() {
        return timeBeforeRename;
    }
}
