package com.ustudents.engine.audio.openal;

import com.ustudents.engine.audio.empty.EmptySound;
import com.ustudents.engine.audio.empty.EmptySoundSource;
import org.joml.Vector2f;

import static org.lwjgl.openal.AL10.*;

public class ALSoundSource extends EmptySoundSource {
    public ALSoundSource() {
        this(null, false, false);
    }

    public ALSoundSource(EmptySound sound) {
        this(sound, false, false);
    }

    public ALSoundSource(EmptySound sound, boolean loop) {
        this(sound, loop, false);
    }

    public ALSoundSource(EmptySound sound, boolean loop, boolean relative) {
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

    @Override
    public void pause() {
        alSourcePause(handle);
    }

    @Override
    public void stop() {
        alSourceStop(handle);
    }

    @Override
    public void destroy() {
        super.destroy();
        alSourcei(handle, AL_BUFFER, 0);
        alDeleteSources(handle);
    }

    @Override
    public void changeSound(EmptySound sound) {
        super.changeSound(sound);
        alSourcei(handle, AL_BUFFER, sound.getHandle());
    }

    @Override
    public void setPosition(Vector2f position) {
        alSource3f(handle, AL_POSITION, position.x, position.y,0);
    }

    @Override
    public void setSpeed(Vector2f speed) {
        alSource3f(handle, AL_VELOCITY, speed.x, speed.y,0);
    }

    @Override
    public void setGain(float gain) {
        alSourcef(handle, AL_GAIN, gain);
    }

    @Override
    public void setProperty(int param, float value) {
        alSourcef(handle, param, value);
    }

    @Override
    public boolean isPlaying() {
        return alGetSourcei(handle, AL_SOURCE_STATE) == AL_PLAYING;
    }

    @Override
    public boolean isPaused() {
        return alGetSourcei(handle, AL_SOURCE_STATE) == AL_PAUSED;
    }

    @Override
    public boolean isStopped() {
        int state = alGetSourcei(handle, AL_SOURCE_STATE);
        return state == AL_STOPPED || state == AL_INITIAL;
    }
}
