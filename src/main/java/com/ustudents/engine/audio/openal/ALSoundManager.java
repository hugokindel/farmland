package com.ustudents.engine.audio.openal;


import com.ustudents.engine.audio.empty.EmptySoundManager;
import com.ustudents.engine.audio.empty.EmptySoundSource;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static java.sql.Types.NULL;
import static org.lwjgl.openal.AL10.alSourcePlay;
import static org.lwjgl.openal.ALC10.*;

public class ALSoundManager extends EmptySoundManager {
    @Override
    public void initialize() {
        this.device = alcOpenDevice((ByteBuffer) null);
        if (device == NULL) {
            throw new IllegalStateException("Failed to open the default OpenAL device.");
        }

        ALCCapabilities deviceCaps = ALC.createCapabilities(device);

        this.context = alcCreateContext(device, (IntBuffer) null);
        if (context == NULL) {
            throw new IllegalStateException("Failed to create OpenAL context.");
        }

        alcMakeContextCurrent(context);
        AL.createCapabilities(deviceCaps);
    }

    @Override
    public void destroy() {
        super.destroy();
        alcCloseDevice(device);
    }

    @Override
    public void play(EmptySoundSource source) {
        super.play(source);
        alSourcePlay(source.getHandle());
    }
}