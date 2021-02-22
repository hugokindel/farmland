package com.ustudents.engine.audio;

import com.ustudents.engine.Game;
import com.ustudents.engine.audio.empty.EmptySound;
import com.ustudents.engine.audio.openal.ALSound;

public class Sound {
    private EmptySound sound;

    public Sound(String filePath) {
        switch (Game.get().getSoundSystemType()) {
            case Empty:
                sound = new EmptySound(filePath);
                break;
            case OpenAL:
                sound = new ALSound(filePath);
                break;
        }
    }

    public void destroy() {
        sound.destroy();
    }

    public int getHandle() {
        return sound.getHandle();
    }

    public boolean isDestroyed() {
        return sound.isDestroyed();
    }

    public String getPath() {
        return sound.getPath();
    }

    public EmptySound getSound() {
        return sound;
    }
}
