package com.ustudents.engine.audio;

import com.ustudents.engine.Game;
import com.ustudents.engine.audio.empty.EmptySoundManager;
import com.ustudents.engine.audio.empty.EmptySoundSource;
import com.ustudents.engine.audio.openal.ALSoundManager;

public class SoundManager {
    private EmptySoundManager soundManager;
    private boolean noSound;

    public void initialize() {
        switch (Game.get().getSoundSystemType()) {
            case Empty:
                soundManager = new EmptySoundManager();
                break;
            case OpenAL:
                soundManager = new ALSoundManager();

                if (!soundManager.initialize()) {
                    soundManager = new EmptySoundManager();
                }

                break;
        }
    }

    public void destroy() {
        soundManager.destroy();
    }

    public void play(EmptySoundSource source) {
        soundManager.play(source);
        noSound = false;
    }

    public void stopAll() {
        soundManager.stopAll();
        noSound = true;
    }

    public void removeAll() {
        soundManager.removeAll();
    }

    public static SoundManager get() {
        return Game.get().getSoundManager();
    }

    public boolean getNoSound(){return noSound;}

    public EmptySoundManager getSoundManager() {
        return soundManager;
    }
}