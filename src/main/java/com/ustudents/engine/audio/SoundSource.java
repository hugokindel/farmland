package com.ustudents.engine.audio;

import com.ustudents.engine.Game;
import com.ustudents.engine.audio.empty.EmptySound;
import com.ustudents.engine.audio.empty.EmptySoundSource;
import com.ustudents.engine.audio.openal.ALSoundSource;
import org.joml.Vector2f;

public class SoundSource {
    private EmptySoundSource soundSource;

    public SoundSource() {
        this(null, false, false);
    }

    public SoundSource(Sound sound) {
        this(sound, false, false);
    }

    public SoundSource(Sound sound, boolean loop) {
        this(sound, loop, false);
    }

    public SoundSource(Sound sound, boolean loop, boolean relative) {
        switch (Game.get().getSoundSystemType()) {
            case Empty:
                soundSource = new EmptySoundSource(sound.getSound(), loop, relative);
                break;
            case OpenAL:
                soundSource = new ALSoundSource(sound.getSound(), loop, relative);
                break;
        }
    }

    public void play() {
        soundSource.play();
    }

    public void pause() {
        soundSource.pause();
    }

    public void stop() {
        soundSource.stop();
    }

    public void destroy() {
        soundSource.stop();
    }

    public void changeSound(EmptySound sound) {
        soundSource.changeSound(sound);
    }

    public void setPosition(Vector2f position) {
        soundSource.setPosition(position);
    }

    public void setSpeed(Vector2f speed) {
        soundSource.setSpeed(speed);
    }

    public void setGain(float gain) {
        soundSource.setGain(gain);
    }

    public void setProperty(int param, float value) {
        soundSource.setProperty(param, value);
    }

    public boolean isPlaying() {
        return soundSource.isPlaying();
    }

    public boolean isPaused() {
        return soundSource.isPaused();
    }

    public boolean isStopped() {
        return soundSource.isStopped();
    }

    public int getHandle() {
        return soundSource.getHandle();
    }

    public EmptySound getSound() {
        return soundSource.getSound();
    }

    public EmptySoundSource getSoundSource() {
        return soundSource;
    }
}
