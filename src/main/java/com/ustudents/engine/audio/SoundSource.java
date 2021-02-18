package com.ustudents.engine.audio;

import com.ustudents.engine.Game;
import org.joml.Vector2f;

import static org.lwjgl.openal.AL10.*;

public class SoundSource {
    private final int handle;

    private Sound sound;

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
        this.handle = alGenSources();

        if (sound != null) {
            changeSound(sound);
        }

        if (loop) {
            alSourcei(handle, AL_LOOPING, AL_TRUE);
        }

        if (relative) {
            alSourcei(handle, AL_SOURCE_RELATIVE, AL_TRUE);
        }
    }

    public void play() {
        Game.get().getSoundManager().play(this);
    }

    public void pause() {
        alSourcePause(handle);
    }

    public void stop() {
        alSourceStop(handle);
    }

    public void destroy() {
        stop();
        alSourcei(handle, AL_BUFFER, 0);
        alDeleteSources(handle);
    }

    public void changeSound(Sound sound) {
        stop();
        this.sound = sound;
        alSourcei(handle, AL_BUFFER, sound.getHandle());
    }

    public void setPosition(Vector2f position) {
        alSource3f(handle, AL_POSITION, position.x, position.y,0);
    }

    public void setSpeed(Vector2f speed) {
        alSource3f(handle, AL_VELOCITY, speed.x, speed.y,0);
    }

    public void setGain(float gain) {
        alSourcef(handle, AL_GAIN, gain);
    }

    public void setProperty(int param, float value) {
        alSourcef(handle, param, value);
    }

    public boolean isPlaying() {
        return alGetSourcei(handle, AL_SOURCE_STATE) == AL_PLAYING;
    }

    public boolean isPaused() {
        return alGetSourcei(handle, AL_SOURCE_STATE) == AL_PAUSED;
    }

    public boolean isStopped() {
        int state = alGetSourcei(handle, AL_SOURCE_STATE);
        return state == AL_STOPPED || state == AL_INITIAL;
    }

    public int getHandle() {
        return handle;
    }

    public Sound getSound() {
        return sound;
    }
}
