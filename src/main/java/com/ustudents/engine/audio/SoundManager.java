package com.ustudents.engine.audio;

import com.ustudents.engine.Game;
import com.ustudents.engine.audio.empty.EmptySoundManager;
import com.ustudents.engine.audio.empty.EmptySoundSource;
import com.ustudents.engine.audio.openal.ALSoundManager;

public class SoundManager {
    private EmptySoundManager soundManager;

    public SoundManager() {
        switch (Game.get().getSoundSystemType()) {
            case Empty:
                soundManager = new EmptySoundManager();
                break;
            case OpenAL:
                soundManager = new ALSoundManager();
                break;
        }
    }

    public void initialize() {
        soundManager.initialize();
    }

    public void destroy() {
        soundManager.destroy();
    }

    public void play(EmptySoundSource source) {
        soundManager.play(source);
    }

    public void stopAll() {
        soundManager.stopAll();
    }

    public void removeAll() {
        soundManager.removeAll();
    }

    public static SoundManager get() {
        return Game.get().getSoundManager();
    }

    public EmptySoundManager getSoundManager() {
        return soundManager;
    }
}