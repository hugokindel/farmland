package com.ustudents.engine.audio.openal;

import com.ustudents.engine.audio.empty.EmptySound;
import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.utility.FileUtil;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static java.sql.Types.NULL;
import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.*;

public class ALSound extends EmptySound {
    @JsonSerializable
    private String path;

    public ALSound(String filePath) {
        super(filePath);
        loadSound(filePath);
    }

    @Override
    public void destroy() {
        if (!isDestroyed) {
            alDeleteBuffers(this.handle);
            isDestroyed = true;
        }
    }

    private void loadSound(String filePath) {
        try {
            this.handle = alGenBuffers();

            try (STBVorbisInfo info = STBVorbisInfo.malloc()) {
                try (MemoryStack stack = MemoryStack.stackPush()) {
                    ByteBuffer vorbis = FileUtil.readFile(filePath);
                    IntBuffer error = stack.mallocInt(1);
                    assert vorbis != null;
                    long decoder = stb_vorbis_open_memory(vorbis, error, null);
                    if (decoder == NULL) {
                        throw new IllegalStateException("Failed to open Ogg Vorbis file. Error: " + error.get(0));
                    }

                    stb_vorbis_get_info(decoder, info);

                    int channels = info.channels();

                    int lengthSamples = stb_vorbis_stream_length_in_samples(decoder);

                    ShortBuffer pcm = MemoryUtil.memAllocShort(lengthSamples);

                    pcm.limit(stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm) * channels);
                    stb_vorbis_close(decoder);

                    alBufferData(handle, info.channels() == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16, pcm, info.sample_rate());
                }
            }

            isDestroyed = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
