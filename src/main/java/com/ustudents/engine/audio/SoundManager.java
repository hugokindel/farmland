package com.ustudents.engine.audio;


import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.cli.print.Out;
import org.joml.Matrix4f;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.*;

import static java.sql.Types.NULL;
import static org.lwjgl.openal.AL10.alSourcePlay;
import static org.lwjgl.openal.ALC10.*;

public class SoundManager {
    private long device;

    private long context;

    private Set<SoundSource> sources;

    public SoundManager() {
        sources = new HashSet<>();
    }

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

    public void destroy() {
        removeAll();
        alcCloseDevice(device);
    }

    public void play(SoundSource source) {
        alSourcePlay(source.getHandle());
        sources.add(source);
    }

    public void stopAll() {
        for (SoundSource source : sources) {
            source.stop();
        }
    }

    public void removeAll() {
        for (SoundSource source : sources) {
            source.destroy();
        }

        sources.clear();
    }
}