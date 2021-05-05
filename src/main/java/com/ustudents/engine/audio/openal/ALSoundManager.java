package com.ustudents.engine.audio.openal;


import com.ustudents.engine.audio.empty.EmptySoundManager;
import com.ustudents.engine.audio.empty.EmptySoundSource;
import com.ustudents.engine.core.cli.print.Out;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

import static java.sql.Types.NULL;
import static org.lwjgl.openal.AL10.alSourcePlay;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.openal.ALC11.ALC_ALL_DEVICES_SPECIFIER;

public class ALSoundManager extends EmptySoundManager {
    @Override
    public boolean initialize() {
        if (Objects.requireNonNull(alcGetString(NULL, ALC_ALL_DEVICES_SPECIFIER)).isEmpty()) {
            Out.printlnError("No OpenAL devices available.");
            return false;
        }

        this.device = alcOpenDevice((ByteBuffer) null);
        if (device == NULL) {
            Out.printlnError("Failed to open the default OpenAL device.");
            return false;
        }

        ALCCapabilities deviceCaps = ALC.createCapabilities(device);

        this.context = alcCreateContext(device, (IntBuffer) null);
        if (context == NULL) {
            Out.printlnError("Failed to create OpenAL context.");
            return false;
        }

        alcMakeContextCurrent(context);
        AL.createCapabilities(deviceCaps);

        return true;
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