package com.ustudents.engine.audio;


import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.cli.print.Out;
import org.joml.Matrix4f;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.system.CallbackI;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.sql.Types.NULL;
import static org.lwjgl.openal.ALC10.*;

public class SoundManager {

    private long device;

    private long context;

    private SoundListener listener;

    private final List<SoundBuffer> soundBufferList;

    private final Map<String, SoundSource> soundSourceMap;

    private final Matrix4f cameraMatrix;

    private SoundSource currentSoundSource;

    public SoundManager() {
        soundBufferList = new ArrayList<>();
        soundSourceMap = new HashMap<>();
        cameraMatrix = new Matrix4f();
    }

    public void init(){
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

    private void initMusic(){
        File soundFolder = new File(Resources.getSoundsDirectoryName());
        File[] list = soundFolder.listFiles();
        if (list == null)return;
        for(File file : list){
            if(file.getName().contains(".ogg")){
                try {
                    SoundBuffer tmp = new SoundBuffer(file.getPath());
                    boolean loop = false;
                    if(file.getName().contains("background")){
                        loop = true;
                    }
                    SoundSource temp = new SoundSource(true,true);
                    temp.setBuffer(tmp.getBufferId());
                    soundBufferList.add(tmp);
                    soundSourceMap.put(file.getName(),temp);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    public void playMusic(String name) {
        if(soundBufferList.isEmpty()) initMusic();
        currentSoundSource = soundSourceMap.get(name);
        currentSoundSource.play();
        Out.println(currentSoundSource.isPlaying());
    }

    public void stopMusic(){
        if (currentSoundSource != null){
            currentSoundSource.stop();
        }
    }

    public void destroy(){
        soundSourceMap.clear();
        for(SoundBuffer soundBuffer : soundBufferList){
            soundBuffer.cleanup();
        }
        soundBufferList.clear();
        currentSoundSource = null;
    }
}