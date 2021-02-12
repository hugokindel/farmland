package com.ustudents.engine.core;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Timer {
    private double deltaTime;
    private double previousFrameDuration;
    private int framesCounter;
    private int fps;
    private double previousFrameCounterTime;

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
}
