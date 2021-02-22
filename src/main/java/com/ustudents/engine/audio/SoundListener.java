package com.ustudents.engine.audio;

import com.ustudents.engine.Game;
import com.ustudents.engine.audio.empty.EmptySoundListener;
import com.ustudents.engine.audio.openal.ALSoundListener;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class SoundListener {
    private EmptySoundListener soundListener;

    public SoundListener() {
        this(new Vector2f(0, 0));
    }

    public SoundListener(Vector2f position) {
        switch (Game.get().getSoundSystemType()) {
            case Empty:
                soundListener = new EmptySoundListener();
                break;
            case OpenAL:
                soundListener = new ALSoundListener();
                break;
        }
    }

    public void setSpeed(Vector2f speed) {
        soundListener.setSpeed(speed);
    }

    public void setPosition(Vector2f position) {
        soundListener.setPosition(position);
    }

    public void setOrientation(Vector3f at, Vector3f up) {
        soundListener.setOrientation(at, up);
    }

    public EmptySoundListener getSoundListener() {
        return soundListener;
    }
}
