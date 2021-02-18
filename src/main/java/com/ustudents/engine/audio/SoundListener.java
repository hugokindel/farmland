package com.ustudents.engine.audio;

import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.AL.*;

public class SoundListener {

    public SoundListener() {
        this(new Vector2f(0, 0));
    }

    public SoundListener(Vector2f position) {
        alListener3f(AL_POSITION, position.x, position.y, 0);
        alListener3f(AL_VELOCITY, 0, 0, 0);

    }

    public void setSpeed(Vector2f speed) {
        alListener3f(AL_VELOCITY, speed.x, speed.y, 0);
    }

    public void setPosition(Vector2f position) {
        alListener3f(AL_POSITION, position.x, position.y, 0);
    }

    public void setOrientation(Vector3f at, Vector3f up) {
        float[] data = new float[6];
        data[0] = at.x;
        data[1] = at.y;
        data[2] = at.z;
        data[3] = up.x;
        data[4] = up.y;
        data[5] = up.z;
        alListenerfv(AL_ORIENTATION, data);
    }
}
