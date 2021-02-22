package com.ustudents.engine.audio.empty;

import com.ustudents.engine.audio.SoundManager;
import org.joml.Vector2f;

public class EmptySoundSource {
    protected int handle;

    protected EmptySound sound;

    public EmptySoundSource() {
        this(null, false, false);
    }

    public EmptySoundSource(EmptySound sound) {
        this(sound, false, false);
    }

    public EmptySoundSource(EmptySound sound, boolean loop) {
        this(sound, loop, false);
    }

    public EmptySoundSource(EmptySound sound, boolean loop, boolean relative) {
        this.sound = sound;
        this.handle = -1;
    }

    public void play() {
        SoundManager.get().play(this);
    }

    public void pause() {

    }

    public void stop() {

    }

    public void destroy() {
        stop();
    }

    public void changeSound(EmptySound sound) {
        stop();
        this.sound = sound;
    }

    public void setPosition(Vector2f position) {

    }

    public void setSpeed(Vector2f speed) {

    }

    public void setGain(float gain) {

    }

    public void setProperty(int param, float value) {

    }

    public boolean isPlaying() {
        return false;
    }

    public boolean isPaused() {
        return false;
    }

    public boolean isStopped() {
        return false;
    }

    public int getHandle() {
        return handle;
    }

    public EmptySound getSound() {
        return sound;
    }
}
